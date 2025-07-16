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

package github.luckygc.ecm.module.security.captcha.controller;

import github.luckygc.cap.CapManager;
import github.luckygc.cap.model.ChallengeData;
import github.luckygc.cap.model.RedeemChallengeRequest;
import github.luckygc.cap.model.RedeemChallengeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cap")
public class CapController {

    private final CapManager capManager;

    @PostMapping("challenge")
    public ChallengeData createChallenge() {
        return capManager.createChallenge();
    }

    @PostMapping("redeem")
    public RedeemChallengeResponse redeemChallenge(@RequestBody RedeemChallengeRequest redeemChallengeRequest) {
        return capManager.redeemChallenge(redeemChallengeRequest);
    }
}
