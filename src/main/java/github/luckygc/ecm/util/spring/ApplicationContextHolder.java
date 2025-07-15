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

package github.luckygc.ecm.util.spring;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * Spring上下文工具类，用于在非Spring管理的类中获取Bean
 */
@Component
public class ApplicationContextHolder implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(ApplicationContextHolder.class);

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext)
            throws BeansException {
        ApplicationContextHolder.applicationContext = applicationContext;
        log.info("SpringContextHolder初始化完成");
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        assertApplicationContext();
        return applicationContext;
    }

    /**
     * 根据Bean名称获取Bean
     *
     * @param name Bean名称
     * @param <T>  Bean类型
     * @return Bean实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        assertApplicationContext();
        return (T) applicationContext.getBean(name);
    }

    /**
     * 根据Bean类型获取Bean
     *
     * @param clazz Bean类型
     * @param <T>   Bean类型
     * @return Bean实例
     */
    public static <T> T getBean(Class<T> clazz) {
        assertApplicationContext();
        return applicationContext.getBean(clazz);
    }

    /**
     * 根据Bean名称和类型获取Bean
     *
     * @param name  Bean名称
     * @param clazz Bean类型
     * @param <T>   Bean类型
     * @return Bean实例
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        assertApplicationContext();
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 获取指定类型的所有Bean
     *
     * @param clazz Bean类型
     * @param <T>   Bean类型
     * @return 所有匹配的Bean，key为Bean名称，value为Bean实例
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        assertApplicationContext();
        return applicationContext.getBeansOfType(clazz);
    }

    /**
     * 检查ApplicationContext是否已初始化
     */
    private static void assertApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext未初始化，请在启动Spring容器后再调用此方法");
        }
    }
}
