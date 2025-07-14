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

package github.luckygc.ecm.module.user.mapper;

import github.luckygc.ecm.module.user.domain.dto.UserDetailDTO;
import github.luckygc.ecm.module.user.domain.entity.UserEntity;
import github.luckygc.ecm.module.user.domain.request.CreateUserRequest;
import github.luckygc.ecm.module.user.domain.request.UpdateUserRequest;

import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/** 用户对象映射器 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserConverter {

    /**
     * 用户实体转换为响应DTO
     *
     * @param userEntity 用户实体
     * @return 用户响应DTO
     */
    UserDetailDTO toUserDetailDTO(UserEntity userEntity);

    /**
     * 创建用户请求DTO转换为用户实体 注意：密码字段需要特殊处理，在Service中单独处理
     *
     * @param createUserRequest 创建用户请求DTO
     * @return 用户实体
     */
    UserEntity toEntity(CreateUserRequest createUserRequest);

    /**
     * 更新用户请求DTO的属性复制到用户实体 只更新非空字段
     *
     * @param updateUserRequest 更新用户请求DTO
     * @param userEntity 目标用户实体
     */
    void updateEntity(UpdateUserRequest updateUserRequest, @MappingTarget UserEntity userEntity);

    @Nullable List<UserDetailDTO> toDto(List<UserEntity> userEntities);
}
