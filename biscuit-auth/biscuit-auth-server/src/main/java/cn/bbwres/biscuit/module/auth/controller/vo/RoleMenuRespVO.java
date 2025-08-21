package cn.bbwres.biscuit.module.auth.controller.vo;

import lombok.*;
import lombok.experimental.Accessors;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
* <p>
* 角色所属的资源 Response VO
* </p>
*
* @author zlf
* @Date 2025-08-19
*/
@Schema(description = " 角色所属的资源 Response VO")
@Data
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class RoleMenuRespVO implements Serializable {

    /**
    * 主键
    */
    @Schema(description = "主键")
    private String id;

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
    * 角色id
    */
    @Schema(description = "角色id")
    private String roleId;

    /**
    * 菜单id
    */
    @Schema(description = "菜单id")
    private String menuId;

    /**
    * 租户编码
    */
    @Schema(description = "租户编码")
    private String tenantId;




}
