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

import github.luckygc.ecm.module.user.domain.entity.UserRoleMappingEntity;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Repository;

import java.util.List;

import org.hibernate.StatelessSession;
import org.springframework.stereotype.Component;

/** 用户角色关联仓库接口 */
@Repository
@Component
public interface UserRoleMappingRepository extends CrudRepository<UserRoleMappingEntity, Long> {

    StatelessSession session();

    /**
     * 根据用户ID查找用户角色关联
     *
     * @param userId 用户ID
     * @return 用户角色关联列表
     */
    @Find
    List<UserRoleMappingEntity> findByUserId(Long userId);

    /**
     * 根据角色ID查找用户角色关联
     *
     * @param roleId 角色ID
     * @return 用户角色关联列表
     */
    @Find
    List<UserRoleMappingEntity> findByRoleId(Long roleId);

    /**
     * 根据用户ID删除用户角色关联
     *
     * @param userId 用户ID
     * @return 删除的记录数
     */
    @Delete
    long deleteByUserId(Long userId);

    /**
     * 根据角色ID删除用户角色关联
     *
     * @param roleId 角色ID
     * @return 删除的记录数
     */
    @Delete
    long deleteByRoleId(Long roleId);

    /**
     * 根据用户ID和角色ID删除用户角色关联
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 删除的记录数
     */
    @Delete
    long deleteByUserIdAndRoleId(Long userId, Long roleId);
}
