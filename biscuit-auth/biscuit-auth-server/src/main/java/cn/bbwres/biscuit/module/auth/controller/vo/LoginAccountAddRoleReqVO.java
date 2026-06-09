package cn.bbwres.biscuit.module.auth.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 登陆账户表 新增角色信息
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Schema(description = " 登陆账户表 新增角色信息")
@Data
@EqualsAndHashCode
@ToString
@Accessors(chain = true)
public class LoginAccountAddRoleReqVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Schema(description = "登陆账户表主键")
    @NotBlank
    private String id;

    /**
     * 角色id列表
     * <p>使用 {@link NotEmpty} 校验集合非空，并对每个元素使用 {@link NotBlank} 校验非空白字符串。
     */
    @Schema(description = "角色id")
    @NotEmpty
    private List<@NotBlank String> roleIds;

}
