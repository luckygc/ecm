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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

/** 会话配置类 配置Spring Session相关设置，使用Token认证而不是Cookie */
@Configuration
@EnableJdbcHttpSession
public class SessionConfig {

    /**
     * 配置Session ID解析器，优先使用Header中的Token 支持以下Header： - X-Auth-Token - Authorization (Bearer token格式)
     */
    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        // 只使用Header方式传递Session ID，不使用Cookie
        return HeaderHttpSessionIdResolver.xAuthToken();
    }
}
