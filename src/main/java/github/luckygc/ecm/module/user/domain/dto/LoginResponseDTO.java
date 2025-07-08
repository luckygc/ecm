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

package github.luckygc.ecm.module.user.domain.dto;

import lombok.Data;

/** 登录响应DTO */
@Data
public class LoginResponseDTO {

    /** 是否成功 */
    private boolean success;

    /** 响应消息 */
    private String message;

    /** 用户ID */
    private String userId;

    /** 用户名 */
    private String username;

    /** 用户全名 */
    private String fullName;

    /** 会话ID */
    private String sessionId;
}
