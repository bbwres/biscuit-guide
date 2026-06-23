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
import java.util.Map;

/**
 * 定时任务明细 响应 VO
 *
 * @author zhanglinfeng
 */
@Schema(description = "定时任务明细 响应 VO")
@Data
public class JobDetailRespVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

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
     * 任务执行类全限定名
     */
    @Schema(description = "任务执行类全限定名")
    private String jobClassName;

    /**
     * 是否持久化
     */
    @Schema(description = "是否持久化（true:是，false:否）")
    private Boolean durable;

    /**
     * 是否请求恢复（应用重启后是否重新执行）
     */
    @Schema(description = "是否请求恢复（应用重启后是否重新执行）")
    private Boolean requestsRecovery;

    /**
     * 任务数据
     */
    @Schema(description = "任务数据")
    private Map<String, Object> jobDataMap;

}
