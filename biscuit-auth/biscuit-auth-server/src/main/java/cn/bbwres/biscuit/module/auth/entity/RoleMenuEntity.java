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
 * 角色所属的资源
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Table("t_role_menu")
public class RoleMenuEntity extends BaseTenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private String id;


    /**
     * 角色id
     */
    @Column("role_id")
    private String roleId;


    /**
     * 菜单id
     */
    @Column("menu_id")
    private String menuId;


    /**
     * 角色编码
     */
    @Column("role_code")
    private String roleCode;


    /**
     * 角色所属客户端应用
     */
    @Column("client_id")
    private String clientId;

}