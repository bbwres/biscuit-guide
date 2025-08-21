package cn.bbwres.biscuit.module.auth.controller.vo;
import lombok.*;
import lombok.experimental.Accessors;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
* <p>
* 登陆账户表 分页 Request VO
* </p>
*
* @author zlf
* @Date 2025-08-19
*/
@Schema(description = "登陆账户表分页 Request VO")
@Data
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class LoginAccountPageReqVO implements Serializable{
    private static final long serialVersionUID = 1L;

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
    * 登陆密码
    */
    @Schema(description = "登陆密码")
    private String loginPassword;

    /**
    * 手机号
    */
    @Schema(description = "手机号")
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
    private String status;

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
