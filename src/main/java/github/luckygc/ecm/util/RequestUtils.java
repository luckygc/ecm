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

import jakarta.servlet.http.HttpServletRequest;

import java.util.regex.Pattern;

/** HTTP请求工具类 */
public class RequestUtils {

    /** IPv4地址正则表达式 */
    private static final Pattern IPV4_PATTERN =
            Pattern.compile(
                    "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

    /** IPv6地址正则表达式（简化版） */
    private static final Pattern IPV6_PATTERN =
            Pattern.compile("^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$|^::1$|^::$");

    /** 本地回环地址 */
    private static final String LOCALHOST_IPV4 = "127.0.0.1";

    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    private static final String LOCALHOST_IPV6_SHORT = "::1";

    /** 未知IP标识 */
    private static final String UNKNOWN = "unknown";

    /**
     * 获取客户端真实IP地址
     *
     * @param request HTTP请求对象
     * @return 客户端IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return UNKNOWN;
        }

        // 按优先级检查各种代理头
        String[] headers = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR",
            "X-Cluster-Client-IP"
        };

        for (String header : headers) {
            String ip = getIpFromHeader(request, header);
            if (isValidIp(ip)) {
                return ip;
            }
        }

        // 最后使用 request.getRemoteAddr()
        String remoteAddr = request.getRemoteAddr();
        return isValidIp(remoteAddr) ? remoteAddr : UNKNOWN;
    }

    /**
     * 从指定请求头获取IP地址
     *
     * @param request HTTP请求对象
     * @param headerName 请求头名称
     * @return IP地址，如果无效则返回null
     */
    private static String getIpFromHeader(HttpServletRequest request, String headerName) {
        String ip = request.getHeader(headerName);

        if (ip == null || ip.trim().isEmpty() || UNKNOWN.equalsIgnoreCase(ip.trim())) {
            return null;
        }

        // 处理多个IP的情况（通常以逗号分隔）
        if (ip.contains(",")) {
            String[] ips = ip.split(",");
            for (String singleIp : ips) {
                String trimmedIp = singleIp.trim();
                if (isValidIp(trimmedIp)) {
                    return trimmedIp;
                }
            }
        }

        return ip.trim();
    }

    /**
     * 验证IP地址是否有效
     *
     * @param ip IP地址字符串
     * @return 是否为有效IP
     */
    private static boolean isValidIp(String ip) {
        if (ip == null || ip.trim().isEmpty() || UNKNOWN.equalsIgnoreCase(ip.trim())) {
            return false;
        }

        String trimmedIp = ip.trim();

        // 检查是否为有效的IPv4或IPv6地址
        return isValidIPv4(trimmedIp) || isValidIPv6(trimmedIp);
    }

    /**
     * 验证IPv4地址
     *
     * @param ip IP地址字符串
     * @return 是否为有效IPv4地址
     */
    private static boolean isValidIPv4(String ip) {
        return IPV4_PATTERN.matcher(ip).matches();
    }

    /**
     * 验证IPv6地址
     *
     * @param ip IP地址字符串
     * @return 是否为有效IPv6地址
     */
    private static boolean isValidIPv6(String ip) {
        // 处理IPv6的各种格式
        if (ip.equals("::1") || ip.equals("::")) {
            return true;
        }

        // 简化的IPv6验证，实际项目中可以使用更完善的验证
        return IPV6_PATTERN.matcher(ip).matches()
                || ip.contains(":") && ip.length() >= 2 && ip.length() <= 39;
    }

    /**
     * 判断是否为本地地址
     *
     * @param ip IP地址
     * @return 是否为本地地址
     */
    public static boolean isLocalhost(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return false;
        }

        String trimmedIp = ip.trim();
        return LOCALHOST_IPV4.equals(trimmedIp)
                || LOCALHOST_IPV6.equals(trimmedIp)
                || LOCALHOST_IPV6_SHORT.equals(trimmedIp);
    }

    /**
     * 判断是否为内网IP地址
     *
     * @param ip IP地址
     * @return 是否为内网IP
     */
    public static boolean isInternalIp(String ip) {
        if (!isValidIPv4(ip)) {
            return false;
        }

        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }

        try {
            int first = Integer.parseInt(parts[0]);
            int second = Integer.parseInt(parts[1]);

            // 10.0.0.0 - 10.255.255.255
            if (first == 10) {
                return true;
            }

            // 172.16.0.0 - 172.31.255.255
            if (first == 172 && second >= 16 && second <= 31) {
                return true;
            }

            // 192.168.0.0 - 192.168.255.255
            if (first == 192 && second == 168) {
                return true;
            }

            // 127.0.0.0 - 127.255.255.255 (回环地址)
            if (first == 127) {
                return true;
            }

        } catch (NumberFormatException e) {
            return false;
        }

        return false;
    }
}
