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

import java.util.function.Function;

public final class EnumUtils {

    private EnumUtils() {
    }

    public static <T extends Enum<T>, C> T fromCode(
            C code, Class<T> enumClass, Function<T, C> codeExtractor) {
        T[] enumConstants = enumClass.getEnumConstants();
        for (T enumConstant : enumConstants) {
            C enumCode = codeExtractor.apply(enumConstant);
            if (enumCode.equals(code)) {
                return enumConstant;
            }
        }
        return null;
    }
}
