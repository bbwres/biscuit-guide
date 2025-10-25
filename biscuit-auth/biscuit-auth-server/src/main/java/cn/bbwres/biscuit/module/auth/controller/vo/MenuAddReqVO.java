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
import cn.bbwres.biscuit.module.auth.enums.MenuTypeEnum;
import cn.bbwres.biscuit.validate.ValidateAddGroup;
import cn.bbwres.biscuit.validate.ValidateEditGroup;
import cn.bbwres.biscuit.validate.ValidateEditStatusGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 新增目录请求参数
 *
 * @author zhanglinfeng
 */
@Data
@Schema(description = "新增目录请求参数")
public class MenuAddReqVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -4750877766481634635L;

    /**
     * 菜单id
     */
    @Schema(description = "菜单id,修改时必填")
    @NotBlank(groups = {ValidateEditGroup.class, ValidateEditStatusGroup.class})
    private String id;


    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    @NotBlank(groups = {ValidateAddGroup.class})
    private String name;

    /**
     * 菜单类型，目录、菜单、按钮
     */
    @Schema(description = "菜单类型，目录、菜单、按钮")
    @NotNull(groups = {ValidateAddGroup.class})
    private MenuTypeEnum menuType;

    /**
     * 显示顺序
     */
    @Schema(description = "显示顺序")
    @NotNull(groups = {ValidateAddGroup.class})
    private Integer menuSort;

    /**
     * 父菜单ID
     */
    @Schema(description = "父菜单ID,如果为空，则为顶级菜单。修改是如果为空则认为其被移动到最顶级")
    private String parentId;

    /**
     * 菜单图标
     */
    @Schema(description = "菜单图标")
    private String icon;

    /**
     * 组件路径
     */
    @Schema(description = "组件路径")
    private String component;

    /**
     * 组件名
     */
    @Schema(description = "组件名")
    private String componentName;

    /**
     * 菜单状态
     */
    @Schema(description = "菜单状态")
    @NotNull(groups = {ValidateEditStatusGroup.class})
    private DataStatusEnum status;

    /**
     * 是否可见（true:是，false:否）
     */
    @Schema(description = "是否可见（true:是，false:否）")
    private Boolean visible;

    /**
     * 是否缓存（true:是，false:否）
     */
    @Schema(description = "是否缓存（true:是，false:否）")
    private Boolean keepAlive;

    /**
     * 是否总是显示（true:是，false:否）
     */
    @Schema(description = "是否总是显示（true:是，false:否）")
    private Boolean alwaysShow;

    /**
     * 请求接口方法
     */
    @Schema(description = "请求接口方法")
    private String apiUrlMethod;

    /**
     * 请求接口地址
     */
    @Schema(description = "请求接口地址")
    private String apiUrl;


}
