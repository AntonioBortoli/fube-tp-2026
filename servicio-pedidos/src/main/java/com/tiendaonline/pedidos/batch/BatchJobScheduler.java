package com.tiendaonline.pedidos.batch;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
/**
 * Controlador REST para la ejecución de jobs de procesamiento por lotes relacionados con pedidos.
 * Permite disparar el job de expiración de pedidos tanto de manera programada como manual.
 */
@RestController
@RequestMapping("/pedidos/batch")
@RequiredArgsConstructor
@Tag(name = "Batch", description = "Operaciones de procesamiento por lotes")
public class BatchJobScheduler {

    private static final Logger log = LoggerFactory.getLogger(BatchJobScheduler.class);

    private final JobLauncher jobLauncher;
    private final Job expiracionPedidosJob;
    /**
     * Método programado para ejecutar el job de expiración de pedidos cada día a las 2:00 AM.
     * Utiliza la anotación @Scheduled con una expresión cron para definir la frecuencia de ejecución.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void ejecutarJobNocturno() {
        log.info("Disparando job nocturno de expiracion por cron...");
        dispararJob();
    }
    /**
     * Endpoint REST para ejecutar manualmente el job de expiración de pedidos.
     * Permite a los usuarios disparar el job bajo demanda mediante una solicitud HTTP POST.
     *
     * @return ResponseEntity con un mensaje indicando que el job fue ejecutado correctamente.
     */
    @PostMapping("/expirar")
    @Operation(summary = "Ejecutar manualmente el job de expiración de pedidos")
    public ResponseEntity<Map<String, String>> ejecutarManualmente() {
        dispararJob();
        return ResponseEntity.ok(Map.of("mensaje", "Job de expiracion ejecutado correctamente"));
    }
    /**
     * Método privado que encapsula la lógica para disparar el job de expiración de pedidos.
     * Crea los parámetros del job y maneja cualquier excepción que pueda ocurrir durante la ejecución.
     */
    private void dispararJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(expiracionPedidosJob, params);
        } catch (Exception e) {
            log.error("Error al ejecutar el job de expiracion", e);
        }
    }
}