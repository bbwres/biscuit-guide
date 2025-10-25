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

import cn.bbwres.biscuit.enums.DataStatusEnum;
import cn.bbwres.biscuit.validate.ValidateAddGroup;
import cn.bbwres.biscuit.validate.ValidateEditGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 新增角色请求参数
 *
 * @author zhanglinfeng
 */
@Schema(description = "新增角色请求参数 Request VO")
@Data
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class RoleAddReqVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6851893172289414748L;

    public interface ValidateEditStatus {
    }

    /**
     * 角色id
     */
    @Schema(description = "角色id")
    @NotBlank(groups = {ValidateEditGroup.class, ValidateEditStatus.class})
    private String id;

    /**
     * 角色编码
     */
    @Schema(description = "角色编码")
    @NotBlank(groups = {ValidateAddGroup.class})
    private String roleCode;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    @NotBlank(groups = {ValidateAddGroup.class, ValidateEditGroup.class})
    private String roleName;


    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
    

    /**
     * 角色所属客户端应用
     */
    @Schema(description = "角色所属客户端应用")
    @NotBlank(groups = {ValidateAddGroup.class, ValidateEditGroup.class})
    private String clientId;


    /**
     * 角色状态
     */
    @Schema(description = "角色状态")
    @NotBlank(groups = {ValidateEditStatus.class})
    private DataStatusEnum status;
}
