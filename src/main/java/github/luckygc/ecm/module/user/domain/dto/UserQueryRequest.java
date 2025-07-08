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

import github.luckygc.ecm.module.user.domain.enums.UserStatus;

import lombok.Data;

/** 用户查询请求DTO */
@Data
public class UserQueryRequest {

    /** 用户名（模糊查询） */
    private String username;

    /** 姓名（模糊查询） */
    private String fullName;

    /** 邮箱（模糊查询） */
    private String email;

    /** 手机号（精确查询） */
    private String mobile;

    /** 用户状态 */
    private UserStatus status;

    /** 页码（从0开始） */
    private int page = 0;

    /** 每页大小 */
    private int size = 20;
}
