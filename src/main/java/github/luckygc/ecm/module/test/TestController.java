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

package github.luckygc.ecm.module.test;

import github.luckygc.ecm.common.domain.Result;
import github.luckygc.ecm.module.user.repository.UserRepository;
import github.luckygc.ecm.util.spring.ApplicationContextHolder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/** 测试控制器 用于测试自定义登录过滤器的功能 */
@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    /** 获取当前认证用户信息 */
    @GetMapping("/current-user")
    public Result<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> userInfo = new HashMap<>();
        if (authentication != null && authentication.isAuthenticated()) {
            userInfo.put("username", authentication.getName());
            userInfo.put("authorities", authentication.getAuthorities());
            userInfo.put("authenticated", authentication.isAuthenticated());
            userInfo.put("principal", authentication.getPrincipal().getClass().getSimpleName());
            userInfo.put("timestamp", LocalDateTime.now());
        } else {
            userInfo.put("message", "用户未认证");
            userInfo.put("timestamp", LocalDateTime.now());
        }

        return Result.ok(userInfo);
    }

    /** 受保护的资源测试 */
    @GetMapping("/protected")
    public Result<Map<String, Object>> protectedResource() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "这是一个受保护的资源");
        response.put("accessTime", LocalDateTime.now());
        response.put("accessedBy", authentication.getName());

        log.info("用户 {} 访问了受保护的资源", authentication.getName());

        return Result.ok(response);
    }

    /** 公开资源测试 */
    @GetMapping("/public")
    public Result<Map<String, Object>> publicResource() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "这是一个公开资源");
        response.put("accessTime", LocalDateTime.now());
        response.put("note", "无需认证即可访问");

        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();

        UserRepository bean = applicationContext.getBean(UserRepository.class);
        TransactionTemplate transactionTemplate =
                applicationContext.getBean(TransactionTemplate.class);
        transactionTemplate.executeWithoutResult(
                status -> response.put("test_user", bean.findAll().toList()));

        return Result.ok(response);
    }
}
