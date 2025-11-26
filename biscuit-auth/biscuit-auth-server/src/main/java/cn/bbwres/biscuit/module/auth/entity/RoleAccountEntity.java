package cn.bbwres.biscuit.module.auth.entity;

import cn.bbwres.biscuit.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * 用户角色表
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Table("t_role_account")
public class RoleAccountEntity extends BaseTenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private String id;


    /**
     * 登陆账号id
     */
    @Column("login_account_id")
    private String loginAccountId;


    /**
     * 角色id
     */
    @Column("role_id")
    private String roleId;


    /**
     * 备注
     */
    @Column("remark")
    private String remark;


}