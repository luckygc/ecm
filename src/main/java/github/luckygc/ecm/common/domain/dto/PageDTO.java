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

package github.luckygc.ecm.common.domain.dto;

import jakarta.data.page.Page;
import java.util.List;
import lombok.Data;

@Data
public class PageDTO<T> {

    List<T> content;

    private long totalElements;

    private long totalPages;

    public PageDTO() {
        // 无参构造器
    }

    public PageDTO(Page<T> pageResponse) {
        this.content = pageResponse.content();

        if (pageResponse.hasTotals()) {
            this.totalElements = pageResponse.totalElements();
            this.totalPages = pageResponse.totalPages();
        }
    }

    public static <T> PageDTO<T> of(Page<T> pageResponse) {
        return new PageDTO<>(pageResponse);
    }
}
