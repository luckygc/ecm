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

import github.luckygc.ecm.module.security.captcha.config.CapProperties;
import github.luckygc.ecm.module.security.captcha.domain.Challenge;
import github.luckygc.ecm.module.security.captcha.domain.dto.CapTokenDTO;
import github.luckygc.ecm.module.security.captcha.domain.dto.ChallengeDataDTO;
import github.luckygc.ecm.module.security.captcha.domain.dto.request.RedeemChallengeRequest;
import github.luckygc.ecm.module.security.captcha.domain.entity.CapToken;
import github.luckygc.ecm.module.security.captcha.domain.entity.ChallengeData;
import github.luckygc.ecm.module.security.captcha.repository.CapRepository;

import java.util.List;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/** <a href="https://capjs.js.org">capjs</a> 服务端接口定义 */
@Component
@RequiredArgsConstructor
public class Cap {

    private static final int CHALLENGE_TOKEN_BYTES_SIZE = 25;
    private static final int CAP_TOKEN_VER_TOKEN_BYTES_SIZE = 15;
    private static final int CAP_TOKEN_ID_BYTES_SIZE = 8;
    private static final String CAP_TOKEN_SEPARATOR = ":";

    private final CapRepository capRepository;

    private final CapProperties capConfig;

    public ChallengeDataDTO createChallenge() {
        capRepository.cleanExpiredTokens();

        String token =
                Hex.encodeHexString(
                        RandomUtils.secureStrong().randomBytes(CHALLENGE_TOKEN_BYTES_SIZE));
        long expires = System.currentTimeMillis() + capConfig.getChallengeExpireMs();

        ChallengeData challengeData = new ChallengeData();
        challengeData.setToken(token);
        Challenge challenge =
                new Challenge(
                        capConfig.getChallengeCount(),
                        capConfig.getChallengeSize(),
                        capConfig.getChallengeDifficulty());
        challengeData.setChallenge(challenge);
        challengeData.setExpires(expires);

        capRepository.insertChallengeData(challengeData);

        return ChallengeDataDTO.of(challengeData);
    }

    public CapTokenDTO redeemChallenge(RedeemChallengeRequest redeemChallengeRequest) {
        String challengeToken = redeemChallengeRequest.token();
        List<Integer> solutions = redeemChallengeRequest.solutions();
        if (StringUtils.isEmpty(challengeToken) || CollectionUtils.isEmpty(solutions)) {
            return CapTokenDTO.error("Invalid body");
        }

        capRepository.cleanExpiredTokens();

        ChallengeData challengeData =
                capRepository.findChallengeData(redeemChallengeRequest.token());
        if (challengeData == null) {
            return CapTokenDTO.error("Challenge expired");
        }

        if (challengeData.getExpires() < System.currentTimeMillis()) {
            capRepository.deleteChallengeData(challengeData);
            return CapTokenDTO.error("Challenge expired");
        }

        capRepository.deleteChallengeData(challengeData);

        Challenge challenge = challengeData.getChallenge();
        boolean isValid =
                IntStream.range(0, challenge.c())
                        .allMatch(
                                i -> {
                                    String salt =
                                            CapTool.prng(
                                                    "%s%d".formatted(challengeToken, i + 1),
                                                    challenge.s());
                                    String target =
                                            CapTool.prng(
                                                    "%s%dd".formatted(challengeToken, i + 1),
                                                    challenge.d());
                                    int solution = solutions.get(i);
                                    return DigestUtils.sha256Hex(salt + solution)
                                            .startsWith(target);
                                });

        if (!isValid) {
            return CapTokenDTO.error("Invalid solution");
        }

        String vertoken =
                Hex.encodeHexString(
                        RandomUtils.secureStrong().randomBytes(CAP_TOKEN_VER_TOKEN_BYTES_SIZE));
        long expires = System.currentTimeMillis() + capConfig.getTokenExpireMs();
        String hash = DigestUtils.sha256Hex(vertoken);
        String id =
                Hex.encodeHexString(
                        RandomUtils.secureStrong().randomBytes(CAP_TOKEN_ID_BYTES_SIZE));

        String actualToken = buildCapToken(id, hash);

        CapToken capToken = new CapToken();
        capToken.setToken(actualToken);
        capToken.setExpires(expires);

        capRepository.insertCapToken(capToken);

        String originalToken = buildCapToken(id, vertoken);

        return CapTokenDTO.ok(originalToken, expires);
    }

    public boolean validateCapToken(String capToken) {
        capRepository.cleanExpiredTokens();

        String[] idAndVertoken = parseCapTokenString(capToken);
        if (idAndVertoken.length != 2) {
            throw new IllegalArgumentException("人机验证失败");
        }

        String id = idAndVertoken[0];
        String hash = DigestUtils.sha256Hex(idAndVertoken[1]);
        String actualToken = buildCapToken(id, hash);
        return capRepository.findCapToken(actualToken) != null;
    }

    private String buildCapToken(String id, String hashOrVertoken) {
        return "%s%s%s".formatted(id, CAP_TOKEN_SEPARATOR, hashOrVertoken);
    }

    private String[] parseCapTokenString(String capToken) {
        return StringUtils.split(capToken, CAP_TOKEN_SEPARATOR);
    }
}
