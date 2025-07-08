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
import github.luckygc.ecm.module.security.captcha.domain.Challenge;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Table(
        name = "cap_challenge_data",
        indexes = {
            @Index(name = "uk_cap_challenge_data_token", columnList = "token", unique = true),
            @Index(name = "idx_cap_challenge_data_expire_time", columnList = "expire_time")
        })
@Entity
@Accessors(chain = true)
@Data
public class ChallengeData extends BaseEntity {

    @Column(length = 50, comment = "挑战唯一标识")
    private String token;

    @JdbcTypeCode(SqlTypes.JSON)
    private Challenge challenge;

    private Long expires;
}
