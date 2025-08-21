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
 * 菜单权限表
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_menu")
public class MenuEntity extends BaseTenantEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 菜单id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 菜单名称
     */
    @TableField("name")
    private String name;


    /**
     * 菜单类型，目录、菜单、按钮
     */
    @TableField("menu_type")
    private String menuType;


    /**
     * 显示顺序
     */
    @TableField("menu_sort")
    private Integer menuSort;


    /**
     * 父菜单ID
     */
    @TableField("parent_id")
    private String parentId;


    /**
     * 路由地址
     */
    @TableField("path")
    private String path;


    /**
     * 菜单图标
     */
    @TableField("icon")
    private String icon;


    /**
     * 组件路径
     */
    @TableField("component")
    private String component;


    /**
     * 组件名
     */
    @TableField("component_name")
    private String componentName;


    /**
     * 菜单状态
     */
    @TableField("status")
    private String status;


    /**
     * 是否可见（1:是，0:否）
     */
    @TableField("visible")
    private Short visible;


    /**
     * 是否缓存（1:是，0:否）
     */
    @TableField("keep_alive")
    private Short keepAlive;


    /**
     * 是否总是显示（1:是，0:否）
     */
    @TableField("always_show")
    private Short alwaysShow;


}