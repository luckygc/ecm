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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Getter;
import lombok.ToString;

public class SnowflakeIdGenerator {

    private final Lock lock = new ReentrantLock();

    private static SnowflakeIdGenerator instance;

    private static final long epoch = 1735689600000L; // 2025-01-01 00:00:00 UTC
    private static final long workerIdBits = 5L;
    private static final long sequenceBits = 7L;

    private static final long maxWorkerId = ~(-1L << workerIdBits); // 最大支持32台机器,0-31
    private static final long maxSequence = ~(-1L << sequenceBits); // 每毫秒最大生成128个ID

    private static final long workerIdShift = sequenceBits;
    private static final long timestampLeftShift = sequenceBits + workerIdBits;

    private final long workerId;
    private static long sequence = 0L;
    private static long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long workerId) {
        if (workerId < 0 || workerId > maxWorkerId) {
            throw new IllegalArgumentException("workerId 必须在 0 和 " + maxWorkerId + " 之间");
        }
        this.workerId = workerId;
    }

    public long nextId() {
        try {
            lock.lock();
            long timestamp = currentTime();

            if (timestamp < lastTimestamp) {
                throw new RuntimeException("系统时钟回拨，拒绝生成 ID");
            }

            if (timestamp == lastTimestamp) {
                sequence = (sequence + 1) & maxSequence;
                if (sequence == 0) {
                    timestamp = waitUntilNextMillis(lastTimestamp);
                }
            } else {
                sequence = 0L;
            }

            lastTimestamp = timestamp;

            return ((timestamp - epoch) << timestampLeftShift)
                    | (workerId << workerIdShift)
                    | sequence;
        } finally {
            lock.unlock();
        }
    }

    private long waitUntilNextMillis(long lastTimestamp) {
        long timestamp = currentTime();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTime();
        }
        return timestamp;
    }

    private long currentTime() {
        return System.currentTimeMillis();
    }

    /**
     * ID组成部分的实体类
     */
    @Getter
    @ToString
    public static class IdMetadata {

        private final long id;
        private final long timestamp;
        private final Date createTime;
        private final LocalDateTime createDateTime;
        private final long workerId;
        private final long sequence;

        public IdMetadata(long id, long timestamp, long workerId, long sequence) {
            this.id = id;
            this.timestamp = timestamp;
            this.createTime = new Date(timestamp);
            this.createDateTime =
                    LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
            this.workerId = workerId;
            this.sequence = sequence;
        }
    }

    /**
     * 从生成的ID中解析出各个组成部分
     *
     * @param id 雪花算法生成的ID
     * @return ID的元数据信息
     */
    public static IdMetadata parseId(long id) {
        // 提取时间戳
        long timestamp = ((id >> timestampLeftShift)) + epoch;
        // 提取工作机器ID
        long workerId = (id >> workerIdShift) & maxWorkerId;
        // 提取序列号
        long sequence = id & maxSequence;

        return new IdMetadata(id, timestamp, workerId, sequence);
    }

    /**
     * 从生成的ID中解析出生成时间
     *
     * @param id 雪花算法生成的ID
     * @return 生成时间
     */
    public static LocalDateTime parseIdToDateTime(long id) {
        long timestamp = ((id >> timestampLeftShift)) + epoch;
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    /**
     * 从生成的ID中解析出工作机器ID
     *
     * @param id 雪花算法生成的ID
     * @return 工作机器ID
     */
    public static long parseIdToWorkerId(long id) {
        return (id >> workerIdShift) & maxWorkerId;
    }

    /**
     * 从生成的ID中解析出序列号
     *
     * @param id 雪花算法生成的ID
     * @return 序列号
     */
    public static long parseIdToSequence(long id) {
        return id & maxSequence;
    }

    public static void setInstance(SnowflakeIdGenerator instance) {
        SnowflakeIdGenerator.instance = instance;
    }

    public static long getNextId() {
        if (instance == null) {
            throw new RuntimeException("SnowflakeIdGenerator未初始化");
        }

        return instance.nextId();
    }
}
