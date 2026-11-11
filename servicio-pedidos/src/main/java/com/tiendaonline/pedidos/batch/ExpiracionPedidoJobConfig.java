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
 * Configuración del Job de expiración de pedidos.
 * Este job busca los pedidos en estado CREADO que hayan sido creados hace más de 24 horas,
 * libera la reserva de stock asociada a cada pedido y actualiza su estado a EXPIRADO.
 */
@Configuration
public class ExpiracionPedidoJobConfig {

    private static final Logger log = LoggerFactory.getLogger(ExpiracionPedidoJobConfig.class);
    /**
     * Bean que define el ItemReader para leer los pedidos que deben expirar.
     * Filtra los pedidos en estado CREADO que fueron creados hace más de 24 horas.
     *
     * @param pedidoRepository Repositorio de pedidos para acceder a la base de datos.
     * @return ItemReader que devuelve los pedidos a procesar.
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
     * Bean que define el ItemProcessor para procesar cada pedido que debe expirar.
     * Libera la reserva de stock asociada al pedido y actualiza su estado a EXPIRADO.
     *
     * @param inventarioClient Cliente REST para interactuar con el servicio de inventario.
     * @return ItemProcessor que procesa cada pedido.
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
     * Bean que define el ItemWriter para escribir los pedidos expirados.
     *
     * @param pedidoRepository Repositorio de pedidos para acceder a la base de datos.
     * @return ItemWriter que guarda los pedidos expirados en la base de datos.
     */
    @Bean
    public ItemWriter<Pedido> expiracionPedidosWriter(PedidoRepository pedidoRepository) {
        return chunk -> pedidoRepository.saveAll(chunk.getItems());
    }
    /**
     * Bean que define el Step del job de expiración de pedidos.
     * Configura el reader, processor y writer para el procesamiento por lotes.
     *
     * @param jobRepository Repositorio de jobs para gestionar la ejecución del job.
     * @param transactionManager Gestor de transacciones para manejar la persistencia.
     * @param reader ItemReader para leer los pedidos a expirar.
     * @param processor ItemProcessor para procesar cada pedido.
     * @param writer ItemWriter para escribir los pedidos expirados.
     * @return Step configurado para el job de expiración de pedidos.
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
     * Bean que define el Job de expiración de pedidos.
     * Configura el step y un listener para mostrar un resumen al finalizar la ejecución del job.
     *
     * @param jobRepository Repositorio de jobs para gestionar la ejecución del job.
     * @param expiracionPedidosStep Step que contiene la lógica de expiración de pedidos.
     * @param resumenListener Listener que muestra un resumen al finalizar el job.
     * @return Job configurado para la expiración de pedidos.
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
     * Bean que define el listener para mostrar un resumen al finalizar la ejecución del job.
     *
     * @return JobExecutionListener que muestra el resumen.
     */
    @Bean
    public JobExecutionListener resumenListener() {
        return new JobExecutionListener() {
            /**
             * Método que se ejecuta después de la finalización del job.
             * Muestra un resumen de la ejecución, incluyendo el número de pedidos leídos,
             * el número de pedidos expirados y la duración total del job.
             *
             * @param jobExecution Información sobre la ejecución del job.
             */
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