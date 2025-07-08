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

package github.luckygc.ecm.util;

import java.security.SecureRandom;

/** 短链工具类 */
public class ShortLinkUtils {

    /** Base62字符集（0-9, a-z, A-Z） */
    private static final String BASE62_CHARS =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /** 默认短链长度 */
    private static final int DEFAULT_LENGTH = 6;

    /**
     * 生成短链码
     *
     * @return 短链码
     */
    public static String generateShortCode() {
        return generateShortCode(DEFAULT_LENGTH);
    }

    /**
     * 生成指定长度的短链码
     *
     * @param length 长度
     * @return 短链码
     */
    public static String generateShortCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("短链长度必须大于0");
        }

        StringBuilder sb = new StringBuilder(length);
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(BASE62_CHARS.length());
            sb.append(BASE62_CHARS.charAt(index));
        }

        return sb.toString();
    }

    /**
     * 验证短链码格式
     *
     * @param shortCode 短链码
     * @return 是否有效
     */
    public static boolean isValidShortCode(String shortCode) {
        if (shortCode == null || shortCode.isEmpty()) {
            return false;
        }

        // 检查长度（通常在3-10位之间）
        if (shortCode.length() < 3 || shortCode.length() > 10) {
            return false;
        }

        // 检查字符是否都在Base62字符集中
        for (char c : shortCode.toCharArray()) {
            if (BASE62_CHARS.indexOf(c) == -1) {
                return false;
            }
        }

        return true;
    }
}
