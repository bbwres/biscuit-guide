package cn.bbwres.biscuit.module.auth.controller.vo;

import lombok.*;
import lombok.experimental.Accessors;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
* <p>
* 菜单权限表 Response VO
* </p>
*
* @author zlf
* @Date 2025-08-19
*/
@Schema(description = " 菜单权限表 Response VO")
@Data
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class MenuRespVO implements Serializable {

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
    private String menuType;

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
    * 路由地址
    */
    @Schema(description = "路由地址")
    private String path;

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
    private String status;

    /**
    * 是否可见（1:是，0:否）
    */
    @Schema(description = "是否可见（1:是，0:否）")
    private Short visible;

    /**
    * 是否缓存（1:是，0:否）
    */
    @Schema(description = "是否缓存（1:是，0:否）")
    private Short keepAlive;

    /**
    * 是否总是显示（1:是，0:否）
    */
    @Schema(description = "是否总是显示（1:是，0:否）")
    private Short alwaysShow;

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




}
