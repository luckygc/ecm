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

package github.luckygc.ecm.module.security.captcha.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.security.cap")
public class CapProperties {

    /** 生成的挑战数量 */
    private int challengeCount = 50;

    /** 每条挑战的长度 */
    private int challengeSize = 32;

    /** 挑战难度 */
    private int challengeDifficulty = 4;

    /** 挑战过期时间，秒 */
    private long challengeExpireMs = 5 * 60 * 1000;

    /** token过期时间，秒 */
    private long tokenExpireMs = 2 * 60 * 1000;
}
