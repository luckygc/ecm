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

package github.luckygc.ecm.module.security.authentication.domain.constant;

public interface AuthenticationErrorCode {
    String AUTHENTICATION_FAILED = "authentication_failed";
    String INVALID_CREDENTIALS = "invalid_credentials";
    String ACCOUNT_DISABLED = "account_disabled";
    String ACCOUNT_LOCKED = "account_locked";
    String ACCOUNT_EXPIRED = "account_expired";
    String CREDENTIALS_EXPIRED = "credentials_expired";
    String LOGIN_REQUIRED = "login_required";
    String INVALID_REQUEST_FORMAT = "invalid_request_format";
}
