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
import github.luckygc.ecm.common.exception.BusinessException;
import github.luckygc.ecm.module.user.constant.UserErrorCode;
import github.luckygc.ecm.module.user.domain.dto.UserDetailDTO;
import github.luckygc.ecm.module.user.domain.entity.UserEntity;
import github.luckygc.ecm.module.user.domain.enums.UserStatus;
import github.luckygc.ecm.module.user.domain.request.CreateUserRequest;
import github.luckygc.ecm.module.user.domain.request.UpdateUserRequest;
import github.luckygc.ecm.module.user.manager.UserManager;
import github.luckygc.ecm.module.user.mapper.UserConverter;
import github.luckygc.ecm.module.user.repository.UserRepository;

import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** 用户服务实现类 负责处理用户相关的业务逻辑 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserManager userManager;
    private final UserRepository userRepository;
    private final UserConverter userConverter;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Result<UserDetailDTO> getUserDetailById(Long id) {
        Optional<UserEntity> userOpt = userRepository.findById(id);
        return userOpt.map(userEntity -> Result.ok(userConverter.toUserDetailDTO(userEntity)))
                .orElseGet(() -> Result.error(UserErrorCode.USER_NOT_FOUND, "用户不存在"));
    }

    @Override
    public UserDetailDTO getUserDetailByUsername(String username) {
        log.info("根据用户名获取用户详情: {}", username);

        Optional<UserEntity> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在: " + username);
        }
        return userConverter.toUserDetailDTO(userOpt.get());
    }

    @Override
    public PageDTO<UserDetailDTO> getUsers(
            int page, int size, String username, String fullName, String email, UserStatus status) {
        log.info(
                "获取用户列表: page={}, size={}, username={}, fullName={}, email={}, status={}",
                page,
                size,
                username,
                fullName,
                email,
                status);

        // 创建分页请求（从1开始）
        PageRequest pageRequest = PageRequest.ofPage(page + 1, size, true);

        // 这里简化处理，实际项目中可能需要动态查询
        Page<UserEntity> userPage =
                userRepository.findAll(pageRequest, Order.by(Sort.desc("createTime")));

        // 转换为响应DTO
        List<UserDetailDTO> userRespons =
                userPage.content().stream().map(userConverter::toUserDetailDTO).toList();

        // 手动创建PageDTO
        PageDTO<UserDetailDTO> pageDTO = new PageDTO<>();
        pageDTO.setContent(userRespons);
        if (userPage.hasTotals()) {
            pageDTO.setTotalElements(userPage.totalElements());
            pageDTO.setTotalPages(userPage.totalPages());
        }

        return pageDTO;
    }

    @Override
    public Result<Void> createUser(CreateUserRequest createUserRequest) {
        // 检查用户名是否已存在
        Optional<UserEntity> existingUser =
                userRepository.findByUsername(createUserRequest.getUsername());
        if (existingUser.isPresent()) {
            return Result.error(UserErrorCode.USER_ALREADY_EXISTS, "用户名已存在");
        }

        UserEntity user = userConverter.toEntity(createUserRequest);
        user.setEncryptedPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        user.setStatus(UserStatus.ENABLED);

        userRepository.save(user);

        return Result.ok();
    }

    @Override
    public UserDetailDTO updateUser(Long id, UpdateUserRequest updateUserRequest) {
        return userManager.updateUser(id, updateUserRequest);
    }

    @Override
    public UserDetailDTO updateUserStatus(Long id, String status) {
        return userManager.updateUserStatus(id, status);
    }

    @Override
    public void deleteUser(Long id) {
        userManager.deleteUser(id);
    }
}
