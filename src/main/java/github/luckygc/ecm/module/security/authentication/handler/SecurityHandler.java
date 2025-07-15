/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package github.luckygc.ecm.module.security.authentication.handler;

import github.luckygc.ecm.common.domain.Result;
import github.luckygc.ecm.module.security.authentication.domain.constant.AuthenticationErrorCode;
import github.luckygc.ecm.util.ExceptionsSuppliers;
import github.luckygc.ecm.util.RequestUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * 安全处理工具类 统一处理认证和授权异常，提供JSON格式的响应
 */
@Slf4j
@Component
public class SecurityHandler implements AuthenticationFailureHandler,
        AuthenticationSuccessHandler,
        LogoutSuccessHandler,
        AccessDeniedHandler,
        AuthenticationEntryPoint {

    private final HttpMessageConverter<Object> messageConverter;

    @SuppressWarnings("unchecked")
    public SecurityHandler(List<HttpMessageConverter<?>> converters) {

        this.messageConverter = (HttpMessageConverter<Object>) converters.stream()
                .filter(c -> c instanceof MappingJackson2HttpMessageConverter)
                .findFirst()
                .orElseThrow(ExceptionsSuppliers.illegalState("No JSON message converter found"));
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception)
            throws IOException, ServletException {

        // 根据具体的认证异常类型返回不同的错误代码
        String errorCode;
        if (exception instanceof BadCredentialsException) {
            errorCode = AuthenticationErrorCode.INVALID_CREDENTIALS;
        } else if (exception instanceof DisabledException) {
            errorCode = AuthenticationErrorCode.ACCOUNT_DISABLED;
        } else if (exception instanceof LockedException) {
            errorCode = AuthenticationErrorCode.ACCOUNT_LOCKED;
        } else if (exception instanceof AccountExpiredException) {
            errorCode = AuthenticationErrorCode.ACCOUNT_EXPIRED;
        } else if (exception instanceof CredentialsExpiredException) {
            errorCode = AuthenticationErrorCode.CREDENTIALS_EXPIRED;
        } else {
            errorCode = AuthenticationErrorCode.AUTHENTICATION_FAILED;
        }

        Result<Void> result =
                Result.error(errorCode, StringUtils.defaultIfEmpty(exception.getMessage(), "认证失败"));
        writeJsonResponse(response, result);
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        String username = authentication.getName();
        SecurityContext context = SecurityContextHolder.getContext();
        HttpSession session = request.getSession(true);
        String token = session.getId();

        var result = Result.ok(Map.of("token", token));
        messageConverter.write(
                result, MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
        log.info("用户 {} 登录成功，Token: {}，context:{}", username, token, context);
    }

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        if (authentication != null) {
            log.info("用户 {} 登出成功", authentication.getName());
        }

        writeJsonResponse(response, Result.ok());
    }

    private void writeJsonResponse(HttpServletResponse response, Object result) {
        writeJsonResponse(response, result, HttpStatus.OK);
    }

    private void writeJsonResponse(HttpServletResponse response, Object result, HttpStatus status) {
        try {
            response.setStatus(status.value());
            messageConverter.write(
                    result, MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
        } catch (IOException e) {
            log.error("请求响应处理异常", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        log.warn("未授权访问 [{}][{}]", RequestUtils.getClientIp(request), request.getRequestURI());
        Result<Void> result = Result.error("access_denied", "访问被拒绝，权限不足");
        writeJsonResponse(response, result, HttpStatus.FORBIDDEN);
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {

        log.warn("未认证访问 [{}][{}]", RequestUtils.getClientIp(request), request.getRequestURI());
        Result<Void> result = Result.error("unauthorized", "未认证，请先登录");
        writeJsonResponse(response, result, HttpStatus.UNAUTHORIZED);
    }
}
