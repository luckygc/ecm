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

import github.luckygc.ecm.module.user.domain.entity.UserEntity;
import github.luckygc.ecm.module.user.domain.enums.UserStatus;
import github.luckygc.ecm.module.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/** 自定义用户详情服务 实现Spring Security的UserDetailsService接口 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Throwable.class, isolation = Isolation.READ_COMMITTED)
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 根据用户名加载用户详情
     *
     * @param username 用户名
     * @return UserDetails
     * @throws UsernameNotFoundException 用户不存在异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        log.debug("Found user: {}, status: {}", user.getUsername(), user.getStatus());

        // 检查用户状态
        boolean enabled = user.getStatus() == UserStatus.ENABLED;
        boolean accountNonExpired = true; // 可以根据业务需求实现
        boolean credentialsNonExpired = true; // 可以根据业务需求实现
        boolean accountNonLocked = user.getStatus() != UserStatus.LOCKED;

        // 获取用户权限
        Collection<? extends GrantedAuthority> authorities = getUserAuthorities(user);

        return new User(
                user.getUsername(),
                user.getEncryptedPassword(),
                enabled,
                accountNonExpired,
                credentialsNonExpired,
                accountNonLocked,
                authorities);
    }

    /**
     * 获取用户权限 这里可以根据实际业务需求实现权限获取逻辑
     *
     * @param user 用户实体
     * @return 权限集合
     */
    private Collection<? extends GrantedAuthority> getUserAuthorities(UserEntity user) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        // 添加默认用户角色
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        // 这里可以根据用户角色或权限添加更多权限
        // 例如：从用户角色映射表中获取角色，然后转换为权限

        log.debug("User {} has authorities: {}", user.getUsername(), authorities);
        return authorities;
    }
}
