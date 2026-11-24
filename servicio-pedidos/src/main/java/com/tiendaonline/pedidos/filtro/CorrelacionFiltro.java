package com.tiendaonline.pedidos.filtro;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filtro HTTP para propagar el identificador de correlación (X-Correlation-ID) en los logs vía MDC.
 */
@Component
public class CorrelacionFiltro extends OncePerRequestFilter {

    public static final String CORRELACION_ID_HEADER = "X-Correlation-ID";
    public static final String MDC_TRACE_ID_KEY = "traceId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String correlacionId = request.getHeader(CORRELACION_ID_HEADER);
        if (correlacionId == null || correlacionId.isBlank()) {
            correlacionId = UUID.randomUUID().toString();
        }

        MDC.put(MDC_TRACE_ID_KEY, correlacionId);
        response.setHeader(CORRELACION_ID_HEADER, correlacionId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_TRACE_ID_KEY);
        }
    }
}
