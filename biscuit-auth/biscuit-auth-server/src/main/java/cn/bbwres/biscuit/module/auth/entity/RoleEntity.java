package cn.bbwres.biscuit.module.auth.entity;

import cn.bbwres.biscuit.entity.BaseTenantEntity;
import cn.bbwres.biscuit.enums.DataStatusEnum;
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
 * 角色表
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Table("t_role")
public class RoleEntity extends BaseTenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 角色id
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private String id;


    /**
     * 角色编码
     */
    @Column("role_code")
    private String roleCode;


    /**
     * 角色名称
     */
    @Column("role_name")
    private String roleName;


    /**
     * 角色状态
     */
    @Column("status")
    private DataStatusEnum status;


    /**
     * 备注
     */
    @Column("remark")
    private String remark;


    /**
     * 角色所属客户端应用
     */
    @Column("client_id")
    private String clientId;


}