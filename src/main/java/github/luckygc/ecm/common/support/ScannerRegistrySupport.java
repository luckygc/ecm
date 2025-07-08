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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

public abstract class ScannerRegistrySupport implements ImportBeanDefinitionRegistrar {

    public abstract String getAnnotationName();

    public abstract Class<?> getConfigurerClass();

    @Override
    public void registerBeanDefinitions(
            AnnotationMetadata importingClassMetadata, @NonNull BeanDefinitionRegistry registry) {
        var entityScanAttrs =
                AnnotationAttributes.fromMap(
                        importingClassMetadata.getAnnotationAttributes(getAnnotationName()));
        if (entityScanAttrs != null) {
            registerBeanDefinitions(
                    importingClassMetadata,
                    entityScanAttrs,
                    registry,
                    generateBaseBeanName(importingClassMetadata));
        }
    }

    void registerBeanDefinitions(
            AnnotationMetadata annoMeta,
            AnnotationAttributes annoAttrs,
            BeanDefinitionRegistry registry,
            String beanName) {

        var builder = BeanDefinitionBuilder.genericBeanDefinition(getConfigurerClass());

        List<String> basePackages =
                Arrays.stream(annoAttrs.getStringArray("basePackages"))
                        .filter(StringUtils::hasText)
                        .collect(Collectors.toList());

        if (basePackages.isEmpty()) {
            basePackages.add(getDefaultBasePackage(annoMeta));
        }

        builder.addPropertyValue(
                "basePackages", StringUtils.collectionToCommaDelimitedString(basePackages));

        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
    }

    private String generateBaseBeanName(AnnotationMetadata importingClassMetadata) {
        return importingClassMetadata.getClassName() + "#" + getClass().getSimpleName();
    }

    private String getDefaultBasePackage(AnnotationMetadata importingClassMetadata) {
        String className = importingClassMetadata.getClassName();
        return className.substring(0, className.lastIndexOf('.'));
    }
}
