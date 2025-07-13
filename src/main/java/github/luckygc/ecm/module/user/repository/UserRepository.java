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

package github.luckygc.ecm.module.user.repository;

import github.luckygc.ecm.module.user.domain.entity.UserEntity;
import github.luckygc.ecm.module.user.domain.enums.UserStatus;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Find;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import java.util.Optional;

/** 用户仓库接口 */
@Repository(provider = "hibernate")
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     */
    @Find
    Optional<UserEntity> findByUsername(String username);

    @Query("update user set status = :status where id = :userId")
    void updateStatus(Long userId, UserStatus status);
}
