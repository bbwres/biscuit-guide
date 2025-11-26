package cn.bbwres.biscuit.module.auth.entity;

import cn.bbwres.biscuit.entity.BaseTenantEntity;
import cn.bbwres.biscuit.enums.DataStatusEnum;
import cn.bbwres.biscuit.module.auth.enums.MenuTypeEnum;
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
 * 菜单权限表
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Table("t_menu")
public class MenuEntity extends BaseTenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 菜单id
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private String id;


    /**
     * 菜单名称
     */
    @Column("name")
    private String name;


    /**
     * 菜单类型，菜单、按钮
     */
    @Column("menu_type")
    private MenuTypeEnum menuType;


    /**
     * 显示顺序
     */
    @Column("menu_sort")
    private Integer menuSort;


    /**
     * 父菜单ID
     */
    @Column("parent_id")
    private String parentId;


    /**
     * 菜单图标
     */
    @Column("icon")
    private String icon;


    /**
     * 前端组件路径
     */
    @Column("component")
    private String component;


    /**
     * 前端组件名
     */
    @Column("component_name")
    private String componentName;


    /**
     * 菜单状态
     */
    @Column("status")
    private DataStatusEnum status;


    /**
     * 是否可见（1:是，0:否）
     */
    @Column("visible")
    private Boolean visible;


    /**
     * 是否缓存（1:是，0:否）
     */
    @Column("keep_alive")
    private Boolean keepAlive;


    /**
     * 是否总是显示（1:是，0:否）
     */
    @Column("always_show")
    private Boolean alwaysShow;

    /**
     * 请求接口方法
     */
    @Column("api_url_method")
    private String apiUrlMethod;
    /**
     * 请求接口地址
     */
    @Column("api_url")
    private String apiUrl;


    /**
     * 树形路径
     */
    @Column("tree_path")
    private String treePath;


}