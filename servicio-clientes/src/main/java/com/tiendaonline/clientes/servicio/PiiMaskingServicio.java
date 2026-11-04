package com.tiendaonline.clientes.servicio;

import com.statsig.sdk.Statsig;
import com.statsig.sdk.StatsigOptions;
import com.statsig.sdk.StatsigUser;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@Slf4j




public class PiiMaskingServicio {

    //es la compuerta que habilita o deshabilta el enmascarado de PII. esta relacionada con la contraseña del .yml
    private static final String GATE_MASK_PII = "mask-pii";


    @Value("${statsig.secret-key}")
    private String secretKey;



    private boolean sdkInicializado = false;


    @PostConstruct
    public void inicializarStatsig() {
        if (secretKey == null || secretKey.isBlank() || secretKey.contains("PEGAR_TU_CLAVE")) {
            log.warn("Statsig secret-key no configurada. La bandera '{}' devolvera false por defecto", GATE_MASK_PII);

            return;
        }


        try {
            StatsigOptions opciones = new StatsigOptions();
            Statsig.initializeAsync(secretKey, opciones).get();
            sdkInicializado = true;
            log.info("SDK de Statsig inicializado correctamente.");
        } catch (Exception e) {
            log.error("Error al inicializar Statsig: {}", e.getMessage());
        }
    }


    @PreDestroy
    public void cerrarStatsig() {
        if (sdkInicializado) {
            Statsig.shutdown();
        }
    }


    public boolean debeEnmascarar(String clienteId){
        if(!sdkInicializado){
            return false;

        }
        StatsigUser user = new StatsigUser(clienteId);
        return Statsig.checkGateSync(user, GATE_MASK_PII);
    }



    public String enmascararNombre(String nombre){
        if(nombre==null || nombre.isBlank()){
            return nombre;
        }
        String[] palabras=nombre.trim().split("\\s+");
        StringBuilder resultado=new StringBuilder();

        for(int i=0;i<palabras.length;i++){
            String palabra=palabras[i];
            if(!palabra.isEmpty()){
                resultado.append(palabra.charAt(0)).append("***");
            }
            if(i<palabras.length-1){
                resultado.append(" ");
            }
        }

        return resultado.toString();

    }


    public String enmascararEmail(String email){
        if(email==null || !email.contains("@")){
            return email;
        }

        int indicePunto=email.lastIndexOf(".");

        String extension=(indicePunto != -1 && indicePunto<email.indexOf('@'))
                ? email.substring(indicePunto+1)
                : ".com";


        return "***@***" + extension;
    }


}
