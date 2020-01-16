package com.github.prontera.web;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.prontera.config.RequestAttributeConst;
import com.github.prontera.util.Jacksons;
import com.github.prontera.util.converter.jackson.OffsetDateTimeToIso8601Serializer;
import com.github.prontera.util.converter.jackson.StringToMapSerializer;
import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author Zhao Junjian
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"}, ignoreUnknown = true)
public class RequestDetailsLogger {

    @JsonIgnore
    private final HttpServletRequest request = ServletContextHolder.getRequest();

    @JsonProperty("request_id")
    private String requestId = ServletContextHolder.fetchRequestId();

    @JsonProperty("url")
    private String url = request.getRequestURL().toString();

    @JsonProperty("method")
    private String method = request.getMethod();

    @JsonProperty("params_map")
    private ImmutableMap<String, Object> paramsMap = fetParamsMap(request);

    @JsonProperty("headers")
    private ImmutableMap<String, Object> headers = fetchHttpHeaders(request);

    @JsonProperty("api_desc")
    private String apiDesc;

    @JsonProperty("request_body")
    @JsonSerialize(using = StringToMapSerializer.class)
    private String requestBody = (String) ServletContextHolder.getRequest().getAttribute(RequestAttributeConst.REQUEST_BODY_KEY);

    @JsonProperty("request_time")
    @JsonSerialize(using = OffsetDateTimeToIso8601Serializer.class)
    private OffsetDateTime requestTime = OffsetDateTime.now();

    @JsonProperty("response_time")
    @JsonSerialize(using = OffsetDateTimeToIso8601Serializer.class)
    private OffsetDateTime responseTime;

    @JsonProperty("character_encoding")
    private String characterEncoding = request.getCharacterEncoding();

    @JsonProperty("content_length")
    private long contentLength = request.getContentLengthLong();

    @JsonProperty("remote_host")
    private String remoteHost = request.getRemoteHost();

    @JsonProperty("remote_port")
    private int remotePort = request.getRemotePort();

    private ImmutableMap<String, Object> fetParamsMap(HttpServletRequest request) {
        final Map<String, String[]> parameterMap = request.getParameterMap();
        final ImmutableMap.Builder<String, Object> singleValueParams = ImmutableMap.builder();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            singleValueParams.put(entry.getKey(), entry.getValue()[0]);
        }
        return singleValueParams.build();
    }

    private ImmutableMap<String, Object> fetchHttpHeaders(HttpServletRequest request) {
        final ImmutableMap.Builder<String, Object> headerBuilder = ImmutableMap.builder();
        final Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final String headerName = headerNames.nextElement();
            headerBuilder.put(headerName, request.getHeader(headerName));
        }
        return headerBuilder.build();
    }

    @Override
    public String toString() {
        return Jacksons.parseInPrettyMode(this);
    }
}
