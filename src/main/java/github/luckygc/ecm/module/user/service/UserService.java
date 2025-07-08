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
import github.luckygc.ecm.common.domain.dto.PageDTO;
import github.luckygc.ecm.module.user.domain.dto.UserDetailDTO;
import github.luckygc.ecm.module.user.domain.enums.UserStatus;
import github.luckygc.ecm.module.user.domain.request.CreateUserRequest;
import github.luckygc.ecm.module.user.domain.request.UpdateUserRequest;

/** 用户服务接口 定义用户相关的业务操作 */
public interface UserService {

    /**
     * 根据ID获取用户详情
     *
     * @param id 用户ID
     * @return 用户DTO
     */
    Result<UserDetailDTO> getUserDetailById(Long id);

    /**
     * 根据用户名获取用户详情
     *
     * @param username 用户名
     * @return 用户DTO
     */
    UserDetailDTO getUserDetailByUsername(String username);

    /**
     * 获取用户列表（分页）
     *
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param username 用户名（模糊查询）
     * @param fullName 姓名（模糊查询）
     * @param email 邮箱（模糊查询）
     * @param status 用户状态
     * @return 分页用户列表
     */
    PageDTO<UserDetailDTO> getUsers(
            int page, int size, String username, String fullName, String email, UserStatus status);

    /**
     * 创建用户
     *
     * @param createUserRequest 创建用户请求
     * @return 创建的用户DTO
     */
    Result<Void> createUser(CreateUserRequest createUserRequest);

    /**
     * 更新用户信息
     *
     * @param id 用户ID
     * @param updateUserRequest 更新用户请求
     * @return 更新后的用户DTO
     */
    UserDetailDTO updateUser(Long id, UpdateUserRequest updateUserRequest);

    /**
     * 更新用户状态
     *
     * @param id 用户ID
     * @param status 新状态
     * @return 更新后的用户DTO
     */
    UserDetailDTO updateUserStatus(Long id, String status);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);
}
