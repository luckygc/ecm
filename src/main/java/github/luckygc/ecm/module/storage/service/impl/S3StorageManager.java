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

package github.luckygc.ecm.module.storage.service.impl;

import github.luckygc.ecm.config.property.StorageProperties;
import github.luckygc.ecm.module.storage.service.StorageManager;
import github.luckygc.ecm.util.StorageUtils;
import github.luckygc.ecm.util.id.SnowflakeIdGenerator;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

/**
 * S3存储服务实现 使用AWS SDK S3 v2
 */
@Slf4j
@RequiredArgsConstructor
public class S3StorageManager implements StorageManager {

    private final S3Client s3Client;
    private final StorageProperties.S3 properties;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public String uploadFile(Path filePath) throws IOException {
        // 获取文件大小
        long fileSize = Files.size(filePath);

        String objectName =
                StorageUtils.generateObjectName(
                        snowflakeIdGenerator.nextId(), filePath.getFileName().toString());

        // 尝试检测MIME类型
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream"; // 默认类型
        }

        PutObjectRequest.Builder requestBuilder =
                PutObjectRequest.builder()
                        .bucket(properties.getBucket())
                        .key(objectName)
                        .contentLength(fileSize)
                        .contentType(contentType);

        PutObjectRequest request = requestBuilder.build();

        s3Client.putObject(request, RequestBody.fromFile(filePath));

        return objectName;
    }

    @Override
    public InputStream downloadFile(String objectName) throws IOException {
        GetObjectRequest request =
                GetObjectRequest.builder().bucket(properties.getBucket()).key(objectName).build();

        return s3Client.getObject(request);
    }

    @Override
    public void deleteFile(String objectName) throws IOException {
        DeleteObjectRequest request =
                DeleteObjectRequest.builder()
                        .bucket(properties.getBucket())
                        .key(objectName)
                        .build();

        s3Client.deleteObject(request);
        log.info("S3文件删除成功: {}/{}", properties.getBucket(), objectName);
    }

    @Override
    public boolean fileExists(String objectName) throws IOException {
        try {
            HeadObjectRequest request =
                    HeadObjectRequest.builder()
                            .bucket(properties.getBucket())
                            .key(objectName)
                            .build();

            s3Client.headObject(request);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    @Override
    public List<String> listFiles(String prefix) throws IOException {
        ListObjectsV2Request.Builder requestBuilder =
                ListObjectsV2Request.builder().bucket(properties.getBucket());

        if (prefix != null && !prefix.isEmpty()) {
            requestBuilder.prefix(prefix);
        }

        ListObjectsV2Request request = requestBuilder.build();
        ListObjectsV2Response response = s3Client.listObjectsV2(request);

        return response.contents().stream().map(S3Object::key).collect(Collectors.toList());
    }
}
