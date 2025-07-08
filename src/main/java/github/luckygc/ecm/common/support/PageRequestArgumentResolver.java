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

package github.luckygc.ecm.common.support;

import jakarta.data.page.PageRequest;

import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/** Jakarta Data PageRequest参数解析器 */
public class PageRequestArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String PAGE_PARAMETER_NAME = "page";
    private static final String SIZE_PARAMETER_NAME = "size";
    private static final int DEFAULT_PAGE_SIZE = 30;
    private static final int MAX_PAGE_SIZE = 2000;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return PageRequest.class.equals(parameter.getParameterType());
    }

    @Override
    public PageRequest resolveArgument(
            @NonNull MethodParameter methodParameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {

        String pageString = webRequest.getParameter(PAGE_PARAMETER_NAME);
        String sizeString = webRequest.getParameter(SIZE_PARAMETER_NAME);

        // 解析页码（从1开始）
        int page = parsePageNumber(pageString);

        // 解析页大小
        int size = parsePageSize(sizeString);

        // 限制页大小
        size = Math.min(size, MAX_PAGE_SIZE);

        // 创建JakartaPageable
        return PageRequest.ofPage(page, size, true);
    }

    /**
     * 解析页码（从1开始）
     *
     * @param pageString 页码字符串
     * @return 页码
     */
    private int parsePageNumber(String pageString) {
        if (!StringUtils.hasText(pageString)) {
            return 1; // 默认第1页
        }

        try {
            int page = Integer.parseInt(pageString);
            return Math.max(1, page); // 最小为第1页
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    /**
     * 解析页大小
     *
     * @param sizeString 页大小字符串
     * @return 页大小
     */
    private int parsePageSize(String sizeString) {
        if (!StringUtils.hasText(sizeString)) {
            return DEFAULT_PAGE_SIZE;
        }

        try {
            int size = Integer.parseInt(sizeString);
            return Math.max(1, size);
        } catch (NumberFormatException e) {
            return DEFAULT_PAGE_SIZE;
        }
    }
}
