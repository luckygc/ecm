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

package github.luckygc.ecm.module.security.authentication.filter;

import github.luckygc.cap.CapManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 用户名密码captcha认证过滤器
 */
@Slf4j
public class UsernamePasswordCapAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String CAP_TOKEN_PARAMETER = "capToken";

    private final CapManager capManager;

    public UsernamePasswordCapAuthenticationFilter(AuthenticationManager authenticationManager, CapManager capManager) {
        super(authenticationManager);
        this.capManager = capManager;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String capToken = request.getParameter(CAP_TOKEN_PARAMETER);
        if (StringUtils.isEmpty(capToken)) {
            throw new AuthenticationServiceException("请完成人机验证");
        }

        try {
            if (!capManager.validateCapToken(capToken)) {
                throw new AuthenticationServiceException("人机认证失败，请重试");
            }
        } catch (IllegalArgumentException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }

        return super.attemptAuthentication(request, response);
    }
}
