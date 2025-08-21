package cn.bbwres.biscuit.module.auth.controller.vo;

import lombok.*;
import lombok.experimental.Accessors;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
* <p>
* 用户角色表 Response VO
* </p>
*
* @author zlf
* @Date 2025-08-19
*/
@Schema(description = " 用户角色表 Response VO")
@Data
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class RoleAccountRespVO implements Serializable {

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
    * 登陆账号id
    */
    @Schema(description = "登陆账号id")
    private String loginAccountId;

    /**
    * 角色id
    */
    @Schema(description = "角色id")
    private String roleId;

    /**
    * 备注
    */
    @Schema(description = "备注")
    private String remark;

    /**
    * 租户编码
    */
    @Schema(description = "租户编码")
    private String tenantId;




}
