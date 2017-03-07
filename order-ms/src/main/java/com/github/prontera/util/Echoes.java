package com.github.prontera.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author Zhao Junjian
 */
@Component
public class Echoes {
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${server.port}")
    private String serverPort;
    @Value("${spring.application.name}")
    private String applicationName;

    public ImmutableMap<String, ?> mark(HttpServletRequest request, String requestBody) throws IOException {
        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        ImmutableMap.Builder<String, Object> headerBuilder = this.fetchHttpHeaders(request);
        builder.put("request_uri", request.getRequestURI());
        builder.put("request_method", request.getMethod());
        builder.put("application_name", applicationName);
        builder.put("params_map", fetParamsMap(request));
        try {
            builder.put("request_body", requestBody == null ? ImmutableMap.of() : objectMapper.readValue(requestBody, Map.class));
        } catch (IOException e) {
            builder.put("request_body", requestBody);
        }
        builder.put("character_encoding", request.getCharacterEncoding());
        builder.put("content_length", request.getContentLengthLong());
        builder.put("remote_host", request.getRemoteHost());
        builder.put("remote_port", request.getRemotePort());
        builder.put("request_header", headerBuilder.build());
        builder.put("server_port", Integer.valueOf(serverPort));
        return builder.build();
    }

    private ImmutableMap<String, Object> fetParamsMap(HttpServletRequest request) {
        final Map<String, String[]> parameterMap = request.getParameterMap();
        final ImmutableMap.Builder<String, Object> singleValueParams = ImmutableMap.builder();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            singleValueParams.put(entry.getKey(), entry.getValue()[0]);
        }
        return singleValueParams.build();
    }

    private ImmutableMap.Builder<String, Object> fetchHttpHeaders(HttpServletRequest request) {
        final ImmutableMap.Builder<String, Object> headerBuilder = ImmutableMap.builder();
        final Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final String headerName = headerNames.nextElement();
            headerBuilder.put(headerName, request.getHeader(headerName));
        }
        return headerBuilder;
    }
}
