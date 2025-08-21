package cn.bbwres.biscuit.module.auth.controller.vo;

import lombok.*;
import lombok.experimental.Accessors;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
* <p>
* 菜单资源表 Response VO
* </p>
*
* @author zlf
* @Date 2025-08-19
*/
@Schema(description = " 菜单资源表 Response VO")
@Data
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class MenuResourceRespVO implements Serializable {

    /**
    * id
    */
    @Schema(description = "id")
    private String id;

    /**
    * 菜单id
    */
    @Schema(description = "菜单id")
    private String menuId;

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
    * 请求方法
    */
    @Schema(description = "请求方法")
    private String urlMethod;

    /**
    * 请求地址
    */
    @Schema(description = "请求地址")
    private String urlPath;

    /**
    * 匹配类型,ANT ,REGX
    */
    @Schema(description = "匹配类型,ANT ,REGX")
    private String matchType;

    /**
    * 资源鉴权类型,无需鉴权、登录鉴权、完整鉴权
    */
    @Schema(description = "资源鉴权类型,无需鉴权、登录鉴权、完整鉴权")
    private String authType;

    /**
    * 资源描述
    */
    @Schema(description = "资源描述")
    private String description;




}
