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
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改定时任务 cron 表达式 请求 VO
 *
 * @author zhanglinfeng
 */
@Schema(description = "修改定时任务 cron 表达式 请求 VO")
@Data
public class TriggerCronUpdateReqVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 触发器名称
     */
    @Schema(description = "触发器名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String triggerName;

    /**
     * 触发器分组
     */
    @Schema(description = "触发器分组", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String triggerGroup;

    /**
     * cron 表达式
     */
    @Schema(description = "cron 表达式", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String cron;

}
