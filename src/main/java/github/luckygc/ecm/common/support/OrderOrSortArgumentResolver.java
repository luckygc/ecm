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

import com.google.common.collect.Lists;

import jakarta.data.Order;
import jakarta.data.Sort;

import org.apache.commons.collections4.ListUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

/** Jakarta Data Order或Sort参数解析器 */
public class OrderOrSortArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String SORT_PARAMETER_NAME = "sort";

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        return isOrder(parameter) || isSort(parameter);
    }

    @Override
    public Object resolveArgument(
            @NonNull MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            @NonNull NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {

        if (isOrder(parameter)) {
            return parseOrder(webRequest);
        }

        return parseSort(webRequest);
    }

    private boolean isOrder(MethodParameter parameter) {
        return Order.class.equals(parameter.getParameterType());
    }

    private boolean isSort(MethodParameter parameter) {
        return Sort.class.equals(parameter.getParameterType());
    }

    @SuppressWarnings("unchecked")
    private Order<?> parseOrder(NativeWebRequest webRequest) {
        List<Sort<?>> sorts = parseSorts(webRequest);

        if (sorts.isEmpty()) {
            return Order.by();
        }

        return Order.by(sorts.toArray(new Sort[0]));
    }

    private Sort<?> parseSort(NativeWebRequest webRequest) {
        List<Sort<?>> sorts = parseSorts(webRequest);

        if (sorts.isEmpty()) {
            return null;
        }

        return ListUtils.getFirst(sorts);
    }

    @NonNull
    private List<Sort<?>> parseSorts(NativeWebRequest webRequest) {
        String[] sortParameters = webRequest.getParameterValues(SORT_PARAMETER_NAME);

        if (sortParameters == null || sortParameters.length == 0) {
            return List.of();
        }

        List<Sort<?>> sorts = Lists.newArrayList();

        for (String sortParameter : sortParameters) {
            if (!StringUtils.hasText(sortParameter)) {
                continue;
            }

            // 解析排序参数，格式：property,direction 或 property
            String[] parts = sortParameter.split(",");
            String property = parts[0].trim();

            if (!StringUtils.hasText(property)) {
                continue;
            }

            boolean ascending = true;
            if (parts.length > 1 && StringUtils.hasText(parts[1])) {
                String direction = parts[1].trim();
                ascending =
                        !"desc".equalsIgnoreCase(direction)
                                && !"descending".equalsIgnoreCase(direction);
            }

            if (ascending) {
                sorts.add(Sort.asc(property));
            } else {
                sorts.add(Sort.desc(property));
            }
        }

        return sorts;
    }
}
