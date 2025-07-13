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
import github.luckygc.ecm.common.exception.BusinessException;
import github.luckygc.ecm.module.user.constant.UserErrorCode;
import github.luckygc.ecm.module.user.domain.dto.UserDetailDTO;
import github.luckygc.ecm.module.user.domain.entity.UserEntity;
import github.luckygc.ecm.module.user.domain.enums.UserStatus;
import github.luckygc.ecm.module.user.domain.request.CreateUserRequest;
import github.luckygc.ecm.module.user.manager.UserManager;
import github.luckygc.ecm.module.user.mapper.UserConverter;
import github.luckygc.ecm.module.user.repository.UserRepository;

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
    public UserDetailDTO getDetailByUsername(String username) {
        UserEntity user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(
                                BusinessException.ofSupplier(
                                        UserErrorCode.USER_NOT_FOUND, "用户不存在: " + username));

        return userConverter.toUserDetailDTO(user);
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
}
