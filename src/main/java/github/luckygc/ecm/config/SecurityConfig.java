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

package github.luckygc.ecm.config;

import github.luckygc.ecm.config.property.SecurityProperties;
import github.luckygc.ecm.module.security.authentication.filter.UsernamePasswordCapAuthenticationFilter;
import github.luckygc.ecm.module.security.authentication.handler.SecurityHandler;
import github.luckygc.ecm.module.security.captcha.Cap;
import github.luckygc.ecm.module.user.domain.enums.BuiltInRoleEnum;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AnonymousConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityProperties securityProperties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityHandler securityHandler(List<HttpMessageConverter<?>> converters) {
        return new SecurityHandler(converters);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setHideUserNotFoundExceptions(true);
        return new ProviderManager(provider);
    }

    @Bean
    public UsernamePasswordCapAuthenticationFilter customFormAuthenticationFilter(
            AuthenticationManager authenticationManager, SecurityHandler securityHandler, Cap cap) {

        var filter = new UsernamePasswordCapAuthenticationFilter(authenticationManager, cap);
        filter.setAuthenticationSuccessHandler(securityHandler);
        filter.setAuthenticationFailureHandler(securityHandler);
        filter.setPostOnly(false);
        return filter;
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            UsernamePasswordCapAuthenticationFilter customFormAuthenticationFilter,
            SecurityHandler securityHandler,
            SecurityContextRepository securityContextRepository)
            throws Exception {

        HttpSecurity httpSecurity =
                http.securityContext(
                                securityContext ->
                                        securityContext
                                                .securityContextRepository(
                                                        securityContextRepository)
                                                .requireExplicitSave(false))
                        .anonymous(AnonymousConfigurer::disable)
                        .csrf(CsrfConfigurer::disable)
                        .sessionManagement(
                                session -> {
                                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                                    // 如果配置了最大会话数且不为-1，则启用会话并发控制
                                    if (securityProperties.getSession().getMaximumSessions() > 0) {
                                        session.maximumSessions(
                                                        securityProperties
                                                                .getSession()
                                                                .getMaximumSessions())
                                                .maxSessionsPreventsLogin(
                                                        securityProperties
                                                                .getSession()
                                                                .isMaxSessionsPreventsLogin());
                                    }
                                })
                        .logout(
                                logout ->
                                        logout.logoutUrl("/logout")
                                                .logoutSuccessHandler(securityHandler)
                                                .invalidateHttpSession(true)
                                                .clearAuthentication(true)
                                                .permitAll())
                        .exceptionHandling(
                                exceptions ->
                                        exceptions
                                                .accessDeniedHandler(securityHandler)
                                                .authenticationEntryPoint(securityHandler))

                        // 授权规则配置 - 从配置文件读取
                        .authorizeHttpRequests(
                                auth -> {
                                    // 配置公开访问路径
                                    auth.requestMatchers(securityProperties.getPublicPaths())
                                            .permitAll();

                                    // 配置管理员访问路径
                                    auth.requestMatchers(securityProperties.getAdminPaths())
                                            .hasRole(BuiltInRoleEnum.ROOT.getCode());

                                    // 静态资源允许访问
                                    auth.requestMatchers(
                                                    PathRequest.toStaticResources()
                                                            .atCommonLocations())
                                            .permitAll();

                                    // 其他请求需要认证
                                    auth.anyRequest().authenticated();
                                })
                        .addFilterAt(
                                customFormAuthenticationFilter,
                                UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
