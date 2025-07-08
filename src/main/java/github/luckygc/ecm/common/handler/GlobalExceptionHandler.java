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

package github.luckygc.ecm.common.handler;

import github.luckygc.ecm.common.constant.GeneralErrorCode;
import github.luckygc.ecm.common.domain.Result;
import github.luckygc.ecm.common.exception.BusinessException;

import jakarta.validation.ConstraintViolationException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/** 全局异常处理器 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 处理资源不存在异常 */
    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleNoHandlerFoundException() {
        String errorMessage = "找不到路径处理器";
        log.warn("资源不存在: {}", errorMessage);
        return Result.error(GeneralErrorCode.RESOURCE_NOT_FOUND, errorMessage);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        ex.getBindingResult().getFieldError();
        Map<String, String> errorMap = Maps.newHashMapWithExpectedSize(fieldErrors.size());
        String defaultMessage = null;
        for (FieldError fieldError : fieldErrors) {
            String message = fieldError.getDefaultMessage();
            errorMap.put(fieldError.getField(), message);
            if (StringUtils.isEmpty(defaultMessage)) {
                defaultMessage = message;
            }
        }

        return Result.error(GeneralErrorCode.ARGUMENT_NOT_VALID, defaultMessage, errorMap);
    }

    @ExceptionHandler({ConstraintViolationException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<Void> handleConstraintViolationExceptionOrIllegalArgumentException(Exception ex) {
        return Result.error(GeneralErrorCode.ARGUMENT_NOT_VALID, ex.getMessage());
    }

    /** 处理业务异常 */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<Result<Void>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {

        var detail =
                Map.of(
                        "supportedMethods",
                        Objects.requireNonNullElse(e.getSupportedMethods(), "[]"));
        Result<Void> result =
                Result.error(GeneralErrorCode.HTTP_METHOD_NOT_ALLOWED, e.getMessage(), detail);

        return ResponseEntity.status(e.getStatusCode())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(result);
    }

    /** 处理认证异常 注意：由于Spring Security在过滤器层面已经处理了认证异常， 这个方法通常不会被调用，但保留以防万一有认证异常泄露到Controller层 */
    @ExceptionHandler(AuthenticationException.class)
    public void handleAuthenticationException(AuthenticationException e) {
        // Spring Security已在过滤器层面处理，此处不记录日志避免重复
        // 如果到达这里，说明有认证异常泄露，静默处理
    }

    /** 处理其他异常 */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        String traceId = UUID.randomUUID().toString();
        log.error("系统异常, traceId[{}] :", traceId, e);
        return Result.error(
                GeneralErrorCode.UNKNOWN_ERROR, "系统异常，请联系管理员", Map.of("traceId", traceId));
    }
}
