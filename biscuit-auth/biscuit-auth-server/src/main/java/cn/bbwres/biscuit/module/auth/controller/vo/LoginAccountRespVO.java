package cn.bbwres.biscuit.module.auth.controller.vo;

import cn.bbwres.biscuit.module.auth.enums.LoginAccountStatusEnum;
import cn.bbwres.biscuit.utils.desensitize.annotation.Desensitize;
import cn.bbwres.biscuit.utils.desensitize.impl.PhoneDesensitization;
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
 * 登陆账户表 Response VO
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Schema(description = " 登陆账户表 Response VO")
@Data
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class LoginAccountRespVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -9109387680865529585L;
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

    /**
     * 登陆账号
     */
    @Schema(description = "登陆账号")
    private String loginName;


    /**
     * 手机号
     */
    @Schema(description = "手机号")
    @Desensitize(desensitization = PhoneDesensitization.class)
    private String phone;

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
     * 关联用户表id
     */
    @Schema(description = "关联用户表id")
    private String userId;

    /**
     * 登陆用户状态
     */
    @Schema(description = "登陆用户状态")
    private LoginAccountStatusEnum status;

    /**
     * 锁定到期时间
     */
    @Schema(description = "锁定到期时间")
    private LocalDateTime lockedTime;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    private String name;

    /**
     * 最后一次修改密码时间
     */
    @Schema(description = "最后一次修改密码时间")
    private LocalDateTime lastUpdatePasswordTime;


}
