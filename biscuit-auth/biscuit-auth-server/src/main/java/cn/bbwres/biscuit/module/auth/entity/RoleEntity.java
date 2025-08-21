package cn.bbwres.biscuit.module.auth.entity;

import cn.bbwres.biscuit.entity.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 角色表
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_role")
public class RoleEntity extends BaseTenantEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 角色id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 角色编码
     */
    @TableField("role_code")
    private String roleCode;


    /**
     * 角色名称
     */
    @TableField("role_name")
    private String roleName;


    /**
     * 角色状态
     */
    @TableField("status")
    private Short status;


    /**
     * 备注
     */
    @TableField("remark")
    private String remark;


    /**
     * 角色所属客户端应用
     */
    @TableField("client_id")
    private String clientId;


}