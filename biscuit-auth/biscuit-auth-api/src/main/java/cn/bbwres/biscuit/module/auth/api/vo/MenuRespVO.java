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

package cn.bbwres.biscuit.module.auth.api.vo;

import cn.bbwres.biscuit.enums.DataStatusEnum;
import cn.bbwres.biscuit.module.auth.enums.MenuTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 菜单权限表 Response VO
 * </p>
 *
 * @author zlf
 * @Date 2025-10-25
 */
@Schema(description = " 菜单权限表 Response VO")
@Data
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class MenuRespVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8431869249587109346L;
    /**
     * 菜单id
     */
    @Schema(description = "菜单id")
    private String id;

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    private String name;

    /**
     * 菜单类型，目录、菜单、按钮
     */
    @Schema(description = "菜单类型，目录、菜单、按钮")
    private MenuTypeEnum menuType;


    /**
     * 显示顺序
     */
    @Schema(description = "显示顺序")
    private Integer menuSort;

    /**
     * 父菜单ID
     */
    @Schema(description = "父菜单ID")
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

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String creator;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updater;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 租户编码
     */
    @Schema(description = "租户编码")
    private String tenantId;


    /**
     * 树形路径
     */
    @Schema(description = "树形路径")
    private String treePath;

}
