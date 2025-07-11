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

package github.luckygc.ecm.module.security.captcha.domain.entity;

import github.luckygc.ecm.common.domain.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.experimental.Accessors;

@Table(
        name = "cap_token",
        indexes = {
            @Index(name = "uk_cap_token_token", columnList = "token", unique = true),
            @Index(name = "idx_cap_token_expire_time", columnList = "expire_time")
        })
@Entity
@Data
@Accessors(chain = true)
public class CapToken extends BaseEntity {

    private String token;
    private Long expires;
}
