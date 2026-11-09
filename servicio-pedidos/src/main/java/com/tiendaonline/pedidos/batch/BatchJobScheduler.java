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

@RestController
@RequestMapping("/pedidos/batch")
@RequiredArgsConstructor
@Tag(name = "Batch", description = "Operaciones de procesamiento por lotes")
public class BatchJobScheduler {

    private static final Logger log = LoggerFactory.getLogger(BatchJobScheduler.class);

    private final JobLauncher jobLauncher;
    private final Job expiracionPedidosJob;

    // Ejecución nocturna automática (ej: todos los días a las 2:00 AM)
    @Scheduled(cron = "0 0 2 * * ?")
    public void ejecutarJobNocturno() {
        log.info("Disparando job nocturno de expiracion por cron...");
        dispararJob();
    }

    // Endpoint manual para disparar el batch durante la demo del coloquio
    @PostMapping("/expirar")
    @Operation(summary = "Ejecutar manualmente el job de expiración de pedidos (demo)")
    public ResponseEntity<Map<String, String>> ejecutarManualmente() {
        dispararJob();
        return ResponseEntity.ok(Map.of("mensaje", "Job de expiracion ejecutado correctamente"));
    }

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