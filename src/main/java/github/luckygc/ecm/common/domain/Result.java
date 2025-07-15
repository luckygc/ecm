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

package github.luckygc.ecm.common.domain;

import github.luckygc.ecm.common.constant.GeneralErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Result<T> {

    private boolean success;
    private T data;
    private Error error;

    @Data
    @AllArgsConstructor
    public static class Error {

        private String code;
        private String message;
        private Object detail;
    }

    public static <T> Result<T> ok() {
        return new Result<>(true, null, null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(true, data, null);
    }

    public static <T> Result<T> error() {
        return new Result<>(false, null, new Error(GeneralErrorCode.UNKNOWN_ERROR, null, null));
    }

    public static <T> Result<T> error(String code) {
        return new Result<>(false, null, new Error(code, null, null));
    }

    public static <T> Result<T> error(String code, String message) {
        return new Result<>(false, null, new Error(code, message, null));
    }

    public static <T> Result<T> error(String code, String message, Object detail) {
        return new Result<>(false, null, new Error(code, message, detail));
    }
}
