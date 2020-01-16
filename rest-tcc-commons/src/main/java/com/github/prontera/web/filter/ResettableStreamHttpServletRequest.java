package com.github.prontera.web.filter;

import com.google.common.primitives.Bytes;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Resettable HTTP servlet request stream.
 *
 * @author Petr Dvorak
 */
public class ResettableStreamHttpServletRequest extends HttpServletRequestWrapper {

    public static final byte[] EMPTY = new byte[0];
    private byte[] requestBody = EMPTY;
    private boolean bufferFilled;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public ResettableStreamHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    /**
     * Get request body.
     *
     * @return Bytes with the request body contents.
     * @throws IOException In case stream reqding fails.
     */
    public byte[] getRequestBody() throws IOException {
        final byte[] resultBytes;
        if (bufferFilled) {
            // 已经填入则直接返回
            resultBytes = Arrays.copyOf(requestBody, requestBody.length);
        } else {
            // 未填入则进行处理
            InputStream inputStream = super.getInputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                requestBody = Bytes.concat(this.requestBody, Arrays.copyOfRange(buffer, 0, bytesRead));
            }
            // 置标志位
            bufferFilled = true;
            resultBytes = Arrays.copyOf(requestBody, requestBody.length);
        }
        return resultBytes;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ResettableServletInputStream(getRequestBody());
    }

}