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

package github.luckygc.ecm.module.user.domain.entity;

import github.luckygc.ecm.common.domain.entity.BaseEntity;
import github.luckygc.ecm.module.user.domain.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Entity(name = "user")
@Table(name = "t_user")
public class UserEntity extends BaseEntity {

    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 50, message = "用户名长度必须在2-50个字符之间")
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** 姓名 */
    @Column(nullable = false, length = 50)
    private String fullName;

    /** 密码 */
    @Column(nullable = false, length = 128)
    private String encryptedPassword;

    /** 邮箱 */
    @Column(length = 200)
    private String email;

    /** 手机号 */
    @Column(length = 20)
    private String mobile;

    /** 用户状态 */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;

    /** 最后登录IP */
    @Column(length = 50)
    private String lastLoginIp;
}
