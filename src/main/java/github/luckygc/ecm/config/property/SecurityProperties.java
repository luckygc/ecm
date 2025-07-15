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

package github.luckygc.ecm.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Spring Security配置属性类 用于从配置文件中读取安全相关的配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    /**
     * 公开访问路径配置
     */
    private String[] publicPaths = {"/login", "/logout", "/actuator/health", "/actuator/info"};

    /**
     * 管理员访问路径配置
     */
    private String[] adminPaths = {"/actuator/**"};

    /**
     * 会话管理配置
     */
    private SessionProperties session = new SessionProperties();

    /**
     * 会话配置内部类
     */
    @Data
    public static class SessionProperties {

        /**
         * 是否启用会话固定保护
         */
        private boolean sessionFixationProtection = true;

        /**
         * 最大会话数量（-1表示无限制）
         */
        private int maximumSessions = 3;

        /**
         * 当达到最大会话数时是否阻止新登录
         */
        private boolean maxSessionsPreventsLogin;
    }
}
