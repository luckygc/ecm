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

import github.luckygc.ecm.common.exception.BusinessException;
import github.luckygc.ecm.module.user.constant.UserErrorCode;
import github.luckygc.ecm.module.user.domain.dto.UserDetailDTO;
import github.luckygc.ecm.module.user.domain.entity.UserEntity;
import github.luckygc.ecm.module.user.domain.enums.UserStatus;
import github.luckygc.ecm.module.user.domain.request.UpdateUserRequest;
import github.luckygc.ecm.module.user.mapper.UserConverter;
import github.luckygc.ecm.module.user.repository.UserRepository;
import github.luckygc.ecm.util.EnumUtils;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/** 用户管理器实现类 负责处理需要事务的复杂业务逻辑 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserManagerImpl implements UserManager {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserConverter userConverter;

    @Override
    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.READ_COMMITTED)
    public UserDetailDTO updateUser(Long id, UpdateUserRequest updateUserRequest) {
        log.info("更新用户信息: {}", id);

        // 查找用户
        Optional<UserEntity> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在: " + id);
        }

        UserEntity user = userOpt.get();

        // 如果要更新用户名，检查是否已存在
        if (updateUserRequest.getUsername() != null
                && !updateUserRequest.getUsername().equals(user.getUsername())) {
            Optional<UserEntity> existingUser =
                    userRepository.findByUsername(updateUserRequest.getUsername());
            if (existingUser.isPresent()) {
                throw new BusinessException(
                        "USER_ALREADY_EXISTS", "用户名已存在: " + updateUserRequest.getUsername());
            }
        }

        // 使用MapStruct更新实体
        userConverter.updateEntity(updateUserRequest, user);

        // 特殊处理密码字段
        if (updateUserRequest.getPassword() != null) {
            user.setEncryptedPassword(passwordEncoder.encode(updateUserRequest.getPassword()));
        }

        // 保存更新
        UserEntity updatedUser = userRepository.save(user);
        log.info("用户信息更新成功: {}", updatedUser.getUsername());

        return userConverter.toUserDetailDTO(updatedUser);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.READ_COMMITTED)
    public UserDetailDTO updateUserStatus(Long id, String status) {
        log.info("更新用户状态: {}, 状态: {}", id, status);

        // 查找用户
        Optional<UserEntity> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new BusinessException(UserErrorCode.USER_NOT_FOUND, "用户不存在: " + id);
        }

        UserEntity user = userOpt.get();
        user.setStatus(EnumUtils.fromCode(status, UserStatus.class, UserStatus::getCode));

        // 保存更新
        UserEntity updatedUser = userRepository.save(user);
        log.info("用户状态更新成功: {}, 状态: {}", updatedUser.getUsername(), updatedUser.getStatus());

        return userConverter.toUserDetailDTO(updatedUser);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.READ_COMMITTED)
    public void deleteUser(Long id) {
        log.info("删除用户: {}", id);

        // 查找用户
        Optional<UserEntity> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在: " + id);
        }

        UserEntity user = userOpt.get();

        // 检查是否可以删除（例如：不能删除管理员用户）
        if ("admin".equals(user.getUsername())) {
            throw new BusinessException("CANNOT_DELETE_ADMIN", "不能删除管理员用户");
        }

        // 删除用户
        userRepository.delete(user);
        log.info("用户删除成功: {}", user.getUsername());
    }
}
