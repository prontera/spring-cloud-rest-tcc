/**
 * Copyright 2015 Lime - HighTech Solutions s.r.o.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.prontera.web.filter;

import com.github.prontera.config.RequestAttributeConst;
import com.github.prontera.web.ServletContextHolder;
import com.google.common.base.Charsets;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Request filter that intercepts the request body, forwards it to the controller
 * as a request attribute and resets the stream.
 *
 * @author Petr Dvorak
 */
public class ResettableRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ResettableStreamHttpServletRequest wrapperRequest = new ResettableStreamHttpServletRequest(request);
        byte[] body = wrapperRequest.getRequestBody();
        if (body != null) {
            ServletContextHolder.getRequest().setAttribute(RequestAttributeConst.REQUEST_BODY_KEY, new String(body, Charsets.UTF_8));
        }
        super.doFilter(wrapperRequest, response, filterChain);
    }

}