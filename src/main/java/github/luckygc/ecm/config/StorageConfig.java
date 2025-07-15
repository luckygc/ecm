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

package github.luckygc.ecm.config;

import github.luckygc.ecm.config.property.StorageProperties;
import github.luckygc.ecm.module.storage.service.StorageManager;
import github.luckygc.ecm.module.storage.service.impl.LocalStorageManager;
import github.luckygc.ecm.module.storage.service.impl.S3StorageManager;
import github.luckygc.ecm.util.id.SnowflakeIdGenerator;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * 存储配置类 根据配置属性条件性地创建不同的存储服务
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class StorageConfig {

    private final StorageProperties storageProperties;

    /**
     * 本地存储服务 当storage.type=local或未配置时启用（默认）.
     */
    @Bean
    @ConditionalOnProperty(name = "app.storage.type", havingValue = "local", matchIfMissing = true)
    public StorageManager localStorageService(SnowflakeIdGenerator snowflakeIdGenerator) {
        log.info("启用本地存储服务，存储路径: {}", storageProperties.getLocal().getBasePath());
        return new LocalStorageManager(storageProperties.getLocal(), snowflakeIdGenerator);
    }

    /**
     * S3存储服务 当storage.type=s3时启用.
     */
    @Bean
    @ConditionalOnProperty(name = "app.storage.type", havingValue = "s3")
    public StorageManager s3StorageService(
            S3Client s3Client, S3Presigner s3Presigner, SnowflakeIdGenerator snowflakeIdGenerator) {
        log.info("启用S3存储服务，区域: {}", storageProperties.getS3().getRegion());
        return new S3StorageManager(s3Client, storageProperties.getS3(), snowflakeIdGenerator);
    }

    /**
     * S3客户端 (SDK v2) 当storage.type=s3时创建.
     */
    @Bean
    @ConditionalOnProperty(name = "app.storage.type", havingValue = "s3")
    public S3Client s3Client() {
        StorageProperties.S3 s3Config = storageProperties.getS3();

        AwsBasicCredentials credentials =
                AwsBasicCredentials.create(
                        s3Config.getAccessKeyId(), s3Config.getSecretAccessKey());

        var builder =
                S3Client.builder()
                        .credentialsProvider(StaticCredentialsProvider.create(credentials))
                        .region(Region.of(s3Config.getRegion()));

        // 配置路径样式访问
        if (s3Config.isPathStyleAccess()) {
            builder.serviceConfiguration(
                    S3Configuration.builder().pathStyleAccessEnabled(true).build());
        }

        // 如果配置了自定义端点（用于兼容S3的服务）
        if (StringUtils.isNotEmpty(s3Config.getEndpoint())) {
            builder.endpointOverride(URI.create(s3Config.getEndpoint()));
        }

        return builder.build();
    }

    /**
     * S3预签名器 (SDK v2) 当storage.type=s3时创建.
     */
    @Bean
    @ConditionalOnProperty(name = "app.storage.type", havingValue = "s3")
    public S3Presigner s3Presigner() {
        StorageProperties.S3 s3Config = storageProperties.getS3();

        AwsBasicCredentials credentials =
                AwsBasicCredentials.create(
                        s3Config.getAccessKeyId(), s3Config.getSecretAccessKey());

        var builder =
                S3Presigner.builder()
                        .credentialsProvider(StaticCredentialsProvider.create(credentials))
                        .region(Region.of(s3Config.getRegion()));

        // 如果配置了自定义端点（用于兼容S3的服务）
        if (StringUtils.isNotEmpty(s3Config.getEndpoint())) {
            builder.endpointOverride(URI.create(s3Config.getEndpoint()));
        }

        return builder.build();
    }
}
