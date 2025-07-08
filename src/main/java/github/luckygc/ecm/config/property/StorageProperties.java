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

package github.luckygc.ecm.config.property;

import github.luckygc.ecm.common.enums.StorageType;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.storage")
public class StorageProperties {

    /** 存储类型：local、s3 */
    private StorageType type = StorageType.LOCAL;

    /** 本地存储配置 */
    private Local local = new Local();

    /** S3存储配置 */
    private S3 s3 = new S3();

    @Data
    public static class Local {

        /** 本地存储基础路径 */
        private String basePath = "./storage";
    }

    @Data
    public static class S3 {

        /** AWS访问密钥ID */
        private String accessKeyId;

        /** AWS秘密访问密钥 */
        private String secretAccessKey;

        /** AWS区域 */
        private String region = "us-east-1";

        /** S3端点URL（可选，用于兼容S3的服务） */
        private String endpoint;

        /** 默认存储桶名称 */
        private String bucket = "files";

        /** 是否启用路径样式访问 */
        private boolean pathStyleAccess = true;
    }

    public String getFinalBucketName() {
        return switch (type) {
            case S3 -> s3.getBucket();
            case LOCAL -> local.getBasePath();
            default -> null;
        };
    }
}
