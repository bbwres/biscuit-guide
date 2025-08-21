package cn.bbwres.biscuit.module.auth.controller.vo;
import lombok.*;
import lombok.experimental.Accessors;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
* <p>
* 认证客户端信息表 分页 Request VO
* </p>
*
* @author zlf
* @Date 2025-08-19
*/
@Schema(description = "认证客户端信息表分页 Request VO")
@Data
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class OauthClientDetailsPageReqVO implements Serializable{
    private static final long serialVersionUID = 1L;

    /**
    * 客户端id
    */
    @Schema(description = "客户端id")
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
    * 客户端所能访问的资源id集合,多个资源时用逗号(,)分隔
    */
    @Schema(description = "客户端所能访问的资源id集合,多个资源时用逗号(,)分隔")
    private String resourceIds;

    /**
    * 用于指定客户端(client)的访问密匙
    */
    @Schema(description = "用于指定客户端(client)的访问密匙")
    private String clientSecret;

    /**
    * 指定客户端申请的权限范围,可选值包括read,write,trust;
    */
    @Schema(description = "指定客户端申请的权限范围,可选值包括read,write,trust;")
    private String scope;

    /**
    * 指定客户端支持的grant_type
    */
    @Schema(description = "指定客户端支持的grant_type")
    private String authorizedGrantTypes;

    /**
    * 客户端的重定向URI,可为空
    */
    @Schema(description = "客户端的重定向URI,可为空")
    private String webServerRedirectUri;

    /**
    * 指定客户端所拥有的权限值
    */
    @Schema(description = "指定客户端所拥有的权限值")
    private String authorities;

    /**
    * 设定客户端的access_token的有效时间值(单位:秒)
    */
    @Schema(description = "设定客户端的access_token的有效时间值(单位:秒)")
    private Integer accessTokenValidity;

    /**
    * 设定客户端的refresh_token的有效时间值(单位:秒)
    */
    @Schema(description = "设定客户端的refresh_token的有效时间值(单位:秒)")
    private Integer refreshTokenValidity;

    /**
    * 租户编码
    */
    @Schema(description = "租户编码")
    private String tenantId;




}
