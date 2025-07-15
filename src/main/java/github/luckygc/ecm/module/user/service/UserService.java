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

package github.luckygc.ecm.module.user.service;

import github.luckygc.ecm.common.domain.Result;
import github.luckygc.ecm.module.user.domain.dto.UserDetailDTO;
import github.luckygc.ecm.module.user.domain.request.CreateUserRequest;

/**
 * 用户服务接口 定义用户相关的业务操作
 */
public interface UserService {

    /**
     * 根据用户名获取用户详情
     *
     * @param username 用户名
     * @return 用户DTO
     */
    UserDetailDTO getDetailByUsername(String username);

    /**
     * 创建用户
     *
     * @param createUserRequest 创建用户请求
     * @return 创建的用户DTO
     */
    Result<Void> createUser(CreateUserRequest createUserRequest);
}
