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

package github.luckygc.ecm.module.user.manager;

import github.luckygc.ecm.module.user.domain.dto.UserDetailDTO;
import github.luckygc.ecm.module.user.domain.request.UpdateUserRequest;

/** 用户管理器接口 负责处理需要事务的复杂业务逻辑 */
public interface UserManager {

    /**
     * 更新用户信息（事务操作）
     *
     * @param id 用户ID
     * @param updateUserRequest 更新用户请求
     * @return 更新后的用户DTO
     */
    UserDetailDTO updateUser(Long id, UpdateUserRequest updateUserRequest);

    /**
     * 更新用户状态（事务操作）
     *
     * @param id 用户ID
     * @param status 新状态
     * @return 更新后的用户DTO
     */
    UserDetailDTO updateUserStatus(Long id, String status);

    /**
     * 删除用户（事务操作）
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);
}
