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

import github.luckygc.cap.model.CapToken;
import github.luckygc.cap.model.ChallengeData;
import github.luckygc.ecm.module.security.captcha.domain.entity.CapTokenEntity;
import github.luckygc.ecm.module.security.captcha.domain.entity.ChallengeDataEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CapConvertor {

    ChallengeDataEntity toEntity(ChallengeData challengeData);

    CapTokenEntity toEntity(CapToken capToken);

    ChallengeData toDto(ChallengeDataEntity challengeDataEntity);

    CapToken toDto(CapTokenEntity capTokenEntity);
}
