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

package github.luckygc.ecm.util.id;

import github.luckygc.ecm.util.spring.ApplicationContextHolder;
import java.io.Serial;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class IdentifierGeneratorImpl implements IdentifierGenerator {

    @Serial
    private static final long serialVersionUID = 1L;

    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public Object generate(
            SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        return getGeneratorInstance().nextId();
    }

    private SnowflakeIdGenerator getGeneratorInstance() {
        SnowflakeIdGenerator generator = this.snowflakeIdGenerator;
        if (generator == null) {
            generator = ApplicationContextHolder.getBean(SnowflakeIdGenerator.class);
            this.snowflakeIdGenerator = generator;
        }

        return generator;
    }
}
