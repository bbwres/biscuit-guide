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
 * 角色所属的资源
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_role_menu")
public class RoleMenuEntity extends BaseTenantEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 角色id
     */
    @TableField("role_id")
    private String roleId;


    /**
     * 菜单id
     */
    @TableField("menu_id")
    private String menuId;


}