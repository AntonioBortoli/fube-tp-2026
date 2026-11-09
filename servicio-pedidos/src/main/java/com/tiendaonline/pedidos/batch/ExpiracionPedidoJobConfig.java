package com.tiendaonline.pedidos.batch;

import com.tiendaonline.pedidos.client.InventarioClient;
import com.tiendaonline.pedidos.model.Pedido;
import com.tiendaonline.pedidos.model.enums.EstadoPedido;
import com.tiendaonline.pedidos.repository.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
/**
 * Configuración del job de expiración de pedidos.
 * Este job se encarga de identificar los pedidos que han estado en estado CREADO por más de 24 horas,
 * cambiarlos a estado EXPIRADO y liberar la reserva de stock correspondiente en el servicio de inventario.
 */
@Configuration
public class ExpiracionPedidoJobConfig {

    private static final Logger log = LoggerFactory.getLogger(ExpiracionPedidoJobConfig.class);

    /**
     * READER: Lee todos los pedidos en estado CREADO que tengan más de 24 horas.
     * @param pedidoRepository Repositorio de pedidos para acceder a la base de datos.
     * @return Un ItemReader que lee los pedidos que cumplen con los criterios de expiración.
     */
    @Bean
    @StepScope
    public ItemReader<Pedido> expiracionPedidosReader(PedidoRepository pedidoRepository) {
        LocalDateTime limite = LocalDateTime.now().minusHours(24);
        List<Pedido> pedidosVencidos = pedidoRepository.findByEstadoAndFechaCreacionBefore(
                EstadoPedido.CREADO, limite);
        return new IteratorItemReader<>(pedidosVencidos);
    }

    /**
     * PROCESSOR: Cambia el estado del pedido a EXPIRADO y libera la reserva en inventario.
     * @param inventarioClient Cliente para interactuar con el servicio de inventario.
     * @return Un ItemProcessor que procesa cada pedido, cambiando su estado y liberando la reserva de stock.
     */
    @Bean
    public ItemProcessor<Pedido, Pedido> expiracionPedidosProcessor(InventarioClient inventarioClient) {
        return pedido -> {
            log.info("Expirando pedido {} y liberando stock...", pedido.getId());
            inventarioClient.liberarReservaPorPedido(pedido.getId());
            pedido.setEstado(EstadoPedido.EXPIRADO);
            pedido.setFechaActualizacion(LocalDateTime.now());
            return pedido;
        };
    }

    /**
     * WRITER: Persiste los cambios en la base de datos.
     * @param pedidoRepository Repositorio de pedidos para guardar los cambios.
     * @return Un ItemWriter que guarda los pedidos procesados en la base de datos.
     */
    @Bean
    public ItemWriter<Pedido> expiracionPedidosWriter(PedidoRepository pedidoRepository) {
        return chunk -> pedidoRepository.saveAll(chunk.getItems());
    }

    /**
     * Configura el paso del job de expiración de pedidos.
     * Este paso se encarga de leer, procesar y escribir los pedidos que cumplen con los criterios de expiración.
     *
     * @param jobRepository Repositorio de jobs para manejar la ejecución del job.
     * @param transactionManager Administrador de transacciones para manejar la persistencia de datos.
     * @param reader Lector de pedidos que cumplen con los criterios de expiración.
     * @param processor Procesador que actualiza el estado del pedido y libera la reserva en inventario.
     * @param writer Escritor que persiste los cambios en la base de datos.
     * @return Un Step configurado para la expiración de pedidos.
     */
    @Bean
    public Step expiracionPedidosStep(JobRepository jobRepository,
                                      PlatformTransactionManager transactionManager,
                                      ItemReader<Pedido> reader,
                                      ItemProcessor<Pedido, Pedido> processor,
                                      ItemWriter<Pedido> writer) {
        return new StepBuilder("expiracionPedidosStep", jobRepository)
                .<Pedido, Pedido>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    /**
     * Job de expiración de pedidos.
     * Este job se ejecuta periódicamente y realiza los siguientes pasos:
     * 1. Lee todos los pedidos en estado CREADO que tengan más de 24 horas.
     * 2. Procesa cada pedido, cambiando su estado a EXPIRADO y liberando la reserva de stock en el servicio de inventario.
     * 3. Persiste los cambios en la base de datos.
     *
     * @param jobRepository Repositorio de jobs para manejar la ejecución del job.
     * @param expiracionPedidosStep Paso que contiene la lógica de lectura, procesamiento y escritura de pedidos.
     * @param resumenListener Listener que genera un resumen al finalizar el job.
     * @return Un Job configurado para la expiración de pedidos.
     */
    @Bean
    public Job expiracionPedidosJob(JobRepository jobRepository,
                                    Step expiracionPedidosStep,
                                    JobExecutionListener resumenListener) {
        return new JobBuilder("expiracionPedidosJob", jobRepository)
                .start(expiracionPedidosStep)
                .listener(resumenListener)
                .build();
    }

    /**
     * Listener para generar un resumen de la corrida del job de expiración de pedidos.
     * Muestra información sobre el total de pedidos leídos, expirados y la duración total del job.
     *
     * @return Un JobExecutionListener que imprime el resumen al finalizar el job.
     */
    @Bean
    public JobExecutionListener resumenListener() {
        return new JobExecutionListener() {
            @Override
            public void afterJob(JobExecution jobExecution) {
                long totalLeidos = jobExecution.getStepExecutions().stream()
                        .mapToLong(StepExecution::getReadCount).sum();
                long totalExpirados = jobExecution.getStepExecutions().stream()
                        .mapToLong(StepExecution::getWriteCount).sum();

                log.info("================ RESUMEN CORRIDA BATCH ================");
                log.info("Job: {}", jobExecution.getJobInstance().getJobName());
                log.info("Estado final: {}", jobExecution.getStatus());
                log.info("Pedidos leidos: {}", totalLeidos);
                log.info("Pedidos expirados con stock liberado: {}", totalExpirados);
                if (jobExecution.getStartTime() != null && jobExecution.getEndTime() != null) {
                    long duracion = Duration.between(jobExecution.getStartTime(), jobExecution.getEndTime()).toMillis();
                    log.info("Duracion total: {} ms", duracion);
                }
                log.info("=======================================================");
            }
        };
    }
}