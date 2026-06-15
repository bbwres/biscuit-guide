package cn.bbwres.biscuit.module.auth.entity;

import cn.bbwres.biscuit.entity.BaseNameTenantEntity;
import cn.bbwres.biscuit.enums.DataStatusEnum;
import cn.bbwres.biscuit.module.auth.enums.MenuTypeEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;

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
public class MenuEntity extends BaseNameTenantEntity {

    @Serial
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
     * 菜单类型，菜单、按钮
     */
    @TableField("menu_type")
    private MenuTypeEnum menuType;


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
     * 菜单图标
     */
    @TableField("icon")
    private String icon;


    /**
     * 前端组件路径
     */
    @TableField("component")
    private String component;


    /**
     * 前端组件名
     */
    @TableField("component_name")
    private String componentName;


    /**
     * 菜单状态
     */
    @TableField("status")
    private DataStatusEnum status;


    /**
     * 是否可见（1:是，0:否）
     */
    @TableField("visible")
    private Boolean visible;


    /**
     * 是否缓存（1:是，0:否）
     */
    @TableField("keep_alive")
    private Boolean keepAlive;


    /**
     * 是否总是显示（1:是，0:否）
     */
    @TableField("always_show")
    private Boolean alwaysShow;


    /**
     * 树形路径
     */
    @TableField("tree_path")
    private String treePath;



}