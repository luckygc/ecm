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

package github.luckygc.ecm.module.support.queue.db.domain.entity;

import github.luckygc.ecm.common.domain.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** 数据库队列实体 用于存储需要异步处理的任务 */
@Entity
@Table(name = "db_queue")
@Data
@EqualsAndHashCode(callSuper = true)
public class DBQueue extends BaseEntity {

    /** 队列名称 */
    @Column(name = "queue_name", nullable = false, length = 100)
    private String queueName;

    /** 任务类型 */
    @Column(name = "task_type", nullable = false, length = 50)
    private String taskType;

    /** 任务状态 PENDING: 待处理 PROCESSING: 处理中 COMPLETED: 已完成 FAILED: 失败 CANCELLED: 已取消 */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private QueueStatus status = QueueStatus.PENDING;

    /** 优先级 (1-10, 1为最高优先级) */
    @Column(name = "priority", nullable = false)
    private Integer priority = 5;

    /** 任务数据 (JSON格式) */
    @Column(name = "task_data", columnDefinition = "TEXT")
    private String taskData;

    /** 处理结果 */
    @Column(name = "result", columnDefinition = "TEXT")
    private String result;

    /** 错误信息 */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /** 重试次数 */
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    /** 最大重试次数 */
    @Column(name = "max_retry_count", nullable = false)
    private Integer maxRetryCount = 3;

    /** 下次重试时间 */
    @Column(name = "next_retry_time")
    private LocalDateTime nextRetryTime;

    /** 开始处理时间 */
    @Column(name = "process_start_time")
    private LocalDateTime processStartTime;

    /** 完成时间 */
    @Column(name = "completed_time")
    private LocalDateTime completedTime;

    /** 处理耗时(毫秒) */
    @Column(name = "process_duration")
    private Long processDuration;

    /** 创建者ID */
    @Column(name = "creator_id")
    private Long creatorId;

    /** 处理者ID */
    @Column(name = "processor_id")
    private Long processorId;

    /** 备注 */
    @Column(name = "remark", length = 500)
    private String remark;

    /** 队列状态枚举 */
    public enum QueueStatus {
        PENDING, // 待处理
        PROCESSING, // 处理中
        COMPLETED, // 已完成
        FAILED, // 失败
        CANCELLED // 已取消
    }
}
