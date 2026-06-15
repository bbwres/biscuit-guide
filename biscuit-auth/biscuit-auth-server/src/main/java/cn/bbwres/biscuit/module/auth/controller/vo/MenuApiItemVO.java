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

package cn.bbwres.biscuit.module.auth.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 菜单接口请求项（用于新增/修改菜单时的请求参数）
 *
 * @author zlf
 */
@Data
@Schema(description = "菜单接口请求项")
public class MenuApiItemVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 接口关联记录主键（编辑时可传，新增不传）
     */
    @Schema(description = "接口关联记录主键，编辑时可传，新增不传")
    private String id;

    /**
     * 请求接口地址
     */
    @Schema(description = "请求接口地址")
    @NotBlank
    private String apiUrl;

    /**
     * 请求接口方法
     */
    @Schema(description = "请求接口方法")
    private String apiUrlMethod;

}
