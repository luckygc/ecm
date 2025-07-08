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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** 本地存储服务实现 */
@Slf4j
@RequiredArgsConstructor
public class LocalStorageManager implements StorageManager {

    private final StorageProperties.Local properties;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public String uploadFile(Path filePath) throws IOException {
        Path basePath = Paths.get(properties.getBasePath());
        Files.createDirectories(basePath);

        String objectName =
                StorageUtils.generateObjectName(
                        snowflakeIdGenerator.nextId(), filePath.getFileName().toString());
        Path targetPath = basePath.resolve(objectName);
        Files.createDirectories(targetPath.getParent());

        // 直接复制文件，效率最高
        Files.copy(filePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

        return objectName;
    }

    @Override
    public InputStream downloadFile(String objectName) throws IOException {
        try {
            Path filePath = Paths.get(properties.getBasePath(), objectName);
            if (!Files.exists(filePath)) {
                throw new FileNotFoundException("文件不存在: " + filePath);
            }
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            log.error("文件下载失败: {}", objectName, e);
            throw new RuntimeException("文件下载失败", e);
        }
    }

    @Override
    public void deleteFile(String objectName) throws IOException {
        try {
            Path filePath = Paths.get(properties.getBasePath(), objectName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("文件删除失败: {}", objectName, e);
        }
    }

    @Override
    public boolean fileExists(String objectName) throws IOException {
        Path filePath = Paths.get(properties.getBasePath(), objectName);
        return Files.exists(filePath);
    }

    @Override
    public List<String> listFiles(String prefix) throws IOException {
        try {
            Path basePath = Paths.get(properties.getBasePath());
            Path targetPath = Paths.get(properties.getBasePath(), prefix);
            if (!Files.exists(basePath)) {
                return List.of();
            }

            if (Files.isRegularFile(basePath)) {
                return List.of(basePath.relativize(targetPath).toString());
            }

            try (var stream = Files.walk(targetPath)) {
                return stream.filter(Files::isRegularFile)
                        .map(path -> basePath.relativize(path).toString())
                        .filter(name -> prefix == null || name.startsWith(prefix))
                        .collect(Collectors.toList());
            }
        } catch (IOException e) {
            log.error("列出文件失败: {}", prefix, e);
            return List.of();
        }
    }
}
