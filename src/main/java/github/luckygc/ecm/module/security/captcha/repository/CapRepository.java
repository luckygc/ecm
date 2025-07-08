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

package github.luckygc.ecm.module.security.captcha.repository;

import github.luckygc.ecm.module.security.captcha.domain.entity.CapToken;
import github.luckygc.ecm.module.security.captcha.domain.entity.ChallengeData;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.validation.constraints.NotNull;

import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CapRepository {

    @Insert
    void insertChallengeData(ChallengeData challengeData);

    @Find
    @Nullable
    ChallengeData findChallengeData(@NotNull String token);

    @Query("delete from ChallengeData where expires < :timeMillis")
    void removeExpiredChallengeData(long timeMillis);

    @Delete
    void deleteChallengeData(ChallengeData challengeData);

    @Insert
    void insertCapToken(CapToken capToken);

    @Find
    @Nullable
    CapToken findCapToken(@Nonnull String token);

    @Query("delete from CapToken where expires < :timeMillis")
    void removeExpiredCapToken(long timeMillis);

    default void cleanExpiredTokens() {
        long currentTimeMillis = System.currentTimeMillis();
        removeExpiredChallengeData(currentTimeMillis);
        removeExpiredCapToken(currentTimeMillis);
    }
}
