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

package github.luckygc.ecm.module.user.controller;

import github.luckygc.ecm.common.domain.Result;
import github.luckygc.ecm.common.domain.dto.PageDTO;
import github.luckygc.ecm.module.user.domain.dto.UserDetailDTO;
import github.luckygc.ecm.module.user.domain.enums.UserStatus;
import github.luckygc.ecm.module.user.domain.request.CreateUserRequest;
import github.luckygc.ecm.module.user.domain.request.UpdateUserRequest;
import github.luckygc.ecm.module.user.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    /**
     * 创建用户
     *
     * @param createUserRequest 创建用户请求DTO
     * @return 用户响应DTO
     */
    @PostMapping
    public Result<Void> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        log.info("创建新用户: {}", createUserRequest.getUsername());
        return userService.createUser(createUserRequest);
    }

    /**
     * 根据ID获取用户详情
     *
     * @param id 用户ID
     * @return 用户响应DTO
     */
    @GetMapping("/{id}")
    public Result<UserDetailDTO> getUserById(@PathVariable Long id) {
        return userService.getUserDetailById(id);
    }

    /**
     * 根据用户名获取用户详情
     *
     * @param username 用户名
     * @return 用户响应DTO
     */
    @GetMapping("/username/{username}")
    public Result<UserDetailDTO> getUserByUsername(@PathVariable String username) {
        log.info("根据用户名获取用户详情: {}", username);
        UserDetailDTO userDetailDTO = userService.getUserDetailByUsername(username);
        return Result.ok(userDetailDTO);
    }

    /**
     * 获取用户列表
     *
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param username 用户名（模糊查询）
     * @param fullName 姓名（模糊查询）
     * @param email 邮箱（模糊查询）
     * @param status 用户状态
     * @return 用户列表
     */
    @GetMapping
    public Result<PageDTO<UserDetailDTO>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) UserStatus status) {

        log.info(
                "获取用户列表: page={}, size={}, username={}, fullName={}, email={}, status={}",
                page,
                size,
                username,
                fullName,
                email,
                status);

        PageDTO<UserDetailDTO> pageDTO =
                userService.getUsers(page, size, username, fullName, email, status);
        return Result.ok(pageDTO);
    }

    /**
     * 更新用户信息
     *
     * @param id 用户ID
     * @param updateUserRequest 更新用户请求DTO
     * @return 用户响应DTO
     */
    @PutMapping("/{id}")
    public Result<UserDetailDTO> updateUser(
            @PathVariable Long id, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        log.info("更新用户信息: {}", id);
        UserDetailDTO userDetailDTO = userService.updateUser(id, updateUserRequest);
        return Result.ok(userDetailDTO);
    }

    /**
     * 更新用户状态
     *
     * @param id 用户ID
     * @param status 新状态
     * @return 用户响应DTO
     */
    @PatchMapping("/{id}/status")
    public Result<UserDetailDTO> updateUserStatus(
            @PathVariable Long id, @RequestParam @NotBlank(message = "状态不能为空") String status) {
        log.info("更新用户状态: {}, 状态: {}", id, status);
        UserDetailDTO userDetailDTO = userService.updateUserStatus(id, status);
        return Result.ok(userDetailDTO);
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        log.info("删除用户: {}", id);
        userService.deleteUser(id);
        return Result.ok();
    }
}
