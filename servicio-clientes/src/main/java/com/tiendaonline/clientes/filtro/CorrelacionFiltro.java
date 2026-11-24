package com.tiendaonline.clientes.filtro;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;




import java.io.IOException;
import java.util.UUID;



@Component

public class CorrelacionFiltro extends OncePerRequestFilter {

    public static final String CORRELACION_ID_HEADER = "X-Correlation-ID";
    public static final String MDC_TRACE_ID_KEY = "traceId";


    @Override
    protected void doFilterInternal(
            HttpServletRequest pedido,
            HttpServletResponse respuesta,
            FilterChain filterChain
    )throws ServletException,IOException{

        String correlacionId = respuesta.getHeader(CORRELACION_ID_HEADER);
        if(correlacionId == null || correlacionId.isEmpty()){
            correlacionId= UUID.randomUUID().toString();
        }


        MDC.put(MDC_TRACE_ID_KEY, correlacionId);
        respuesta.setHeader(CORRELACION_ID_HEADER, correlacionId);

        try {
            filterChain.doFilter(pedido, respuesta);
        } finally {
            MDC.remove(MDC_TRACE_ID_KEY);
        }

    }


}
