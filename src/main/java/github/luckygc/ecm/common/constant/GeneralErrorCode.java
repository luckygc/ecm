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

package github.luckygc.ecm.common.constant;

public interface GeneralErrorCode {

    /** 未知错误 */
    String UNKNOWN_ERROR = "unknown_error";

    /** 资源未找到 */
    String RESOURCE_NOT_FOUND = "resource_not_found";

    /** 参数校验失败 */
    String ARGUMENT_NOT_VALID = "argument_not_valid";

    /** 请求方法不支持 */
    String HTTP_METHOD_NOT_ALLOWED = "http_method_not_allowed";

    /** 服务调用失败 */
    String BUSINESS_ERROR = "business_error";
}
