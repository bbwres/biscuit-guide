package cn.bbwres.biscuit.module.auth.controller.vo;

import cn.bbwres.biscuit.module.auth.enums.LoginAccountStatusEnum;
import cn.bbwres.biscuit.validate.ValidateAddGroup;
import cn.bbwres.biscuit.validate.ValidateEditGroup;
import cn.bbwres.biscuit.validate.ValidateEditStatusGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 登陆账户表 新增或者修改请求参数
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Schema(description = "登陆账户表 新增或者修改请求参数")
@Data
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class LoginAccountAddOrUpdateReqVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键,修改时必填")
    @NotBlank(groups = {ValidateEditGroup.class, ValidateEditStatusGroup.class})
    private String id;

    /**
     * 登陆用户状态
     */
    @Schema(description = "登陆用户状态")
    @NotNull(groups = {ValidateEditStatusGroup.class})
    private LoginAccountStatusEnum status;

    /**
     * 登陆账号
     */
    @Schema(description = "登陆账号")
    @NotBlank(groups = {ValidateAddGroup.class})
    private String loginName;

    /**
     * 登陆密码
     */
    @Schema(description = "登陆密码,新增时必填，修改时不需要填写")
    @NotBlank(groups = {ValidateAddGroup.class})
    private String loginPassword;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    @NotBlank(groups = {ValidateAddGroup.class})
    private String phone;


    /**
     * 关联用户表id
     */
    @Schema(description = "关联用户表id")
    @NotBlank(groups = {ValidateAddGroup.class})
    private String userId;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    @NotBlank(groups = {ValidateAddGroup.class})
    private String name;


}
