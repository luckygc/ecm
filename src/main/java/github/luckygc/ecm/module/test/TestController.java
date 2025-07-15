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
import github.luckygc.ecm.module.user.domain.entity.UserEntity;
import github.luckygc.ecm.module.user.domain.entity.UserEntity_;
import github.luckygc.ecm.module.user.repository.UserRepository;
import jakarta.data.Order;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.restriction.Restriction;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器 用于测试自定义登录过滤器的功能
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestController {

    private final BeanFactory beanFactory;

    /**
     * 获取当前认证用户信息
     */
    @GetMapping("/public/test")
    public Result<Object> test(PageRequest pageRequest, Order<UserEntity> order) {
        UserRepository userRepository = beanFactory.getBean(UserRepository.class);

        Restriction<UserEntity> restriction =
                idGreaterThan(0L).and(Restriction.notNull(UserEntity_.username));

        Page<UserEntity> userEntityPage = userRepository.findAll(restriction, pageRequest, order);
        return Result.ok(userEntityPage);
    }

    private static Restriction<UserEntity> idGreaterThan(long id) {
        return Restriction.greaterThan(UserEntity_.id, id);
    }
}
