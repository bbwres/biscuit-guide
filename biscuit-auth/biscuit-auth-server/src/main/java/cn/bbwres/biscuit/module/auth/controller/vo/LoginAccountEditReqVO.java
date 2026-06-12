package cn.bbwres.biscuit.module.auth.controller.vo;

import cn.bbwres.biscuit.validate.ValidateEditGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 登陆账户表 编辑请求参数（仅允许修改姓名和手机号）
 * </p>
 *
 * @author zlf
 */
@Schema(description = "登陆账户表 编辑请求参数")
@Data
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class LoginAccountEditReqVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "主键,修改时必填")
    @NotBlank(groups = {ValidateEditGroup.class})
    private String id;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    @NotBlank(groups = {ValidateEditGroup.class})
    private String name;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;

}
