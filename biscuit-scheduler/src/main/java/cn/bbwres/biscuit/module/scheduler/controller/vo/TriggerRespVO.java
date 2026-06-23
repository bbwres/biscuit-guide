/*
 *
 *  * Copyright 2024 bbwres
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package cn.bbwres.biscuit.module.scheduler.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 定时任务触发器信息 响应 VO
 *
 * @author zhanglinfeng
 */
@Schema(description = "定时任务触发器信息 响应 VO")
@Data
public class TriggerRespVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 触发器名称
     */
    @Schema(description = "触发器名称")
    private String name;

    /**
     * 触发器分组
     */
    @Schema(description = "触发器分组")
    private String group;

    /**
     * 任务名称
     */
    @Schema(description = "任务名称")
    private String jobName;

    /**
     * 任务分组
     */
    @Schema(description = "任务分组")
    private String jobGroup;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;

    /**
     * cron 表达式
     */
    @Schema(description = "cron 表达式")
    private String cronExpression;

    /**
     * 触发器状态
     */
    @Schema(description = "触发器状态")
    private String status;

    /**
     * 上次执行时间
     */
    @Schema(description = "上次执行时间")
    private String previousFireTime;

    /**
     * 下次执行时间
     */
    @Schema(description = "下次执行时间")
    private String nextFireTime;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String endTime;

}
