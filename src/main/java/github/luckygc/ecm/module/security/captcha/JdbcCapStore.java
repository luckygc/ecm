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

package github.luckygc.ecm.module.security.captcha;

import github.luckygc.cap.CapStore;
import github.luckygc.cap.model.CapToken;
import github.luckygc.cap.model.ChallengeData;
import github.luckygc.ecm.module.security.captcha.repository.CapRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JdbcCapStore implements CapStore {

    private final CapRepository capRepository;

    private final CapConvertor capConvertor;

    @Override
    public void cleanExpiredTokens() {
        long currentTimeMillis = System.currentTimeMillis();
        capRepository.removeExpiredChallengeData(currentTimeMillis);
        capRepository.removeExpiredCapToken(currentTimeMillis);
    }

    @Override
    public void saveChallengeData(ChallengeData challengeData) {
        capRepository.insertChallengeData(capConvertor.toEntity(challengeData));
    }

    @Override
    public Optional<ChallengeData> findChallengeData(String token) {
        return capRepository.findChallengeDataByToken(token).map(capConvertor::toDto);
    }

    @Override
    public void deleteChallengeData(ChallengeData challengeData) {
        capRepository.deleteChallengeDataByToken(challengeData.token());
    }

    @Override
    public void saveCapToken(CapToken capToken) {
        capRepository.insertCapToken(capConvertor.toEntity(capToken));
    }

    @Override
    public Optional<CapToken> findCapToken(String token) {
        return capRepository.findCapTokenByToken(token).map(capConvertor::toDto);
    }
}
