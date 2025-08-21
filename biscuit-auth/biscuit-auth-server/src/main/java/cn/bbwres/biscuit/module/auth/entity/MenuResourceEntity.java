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
 * 菜单资源表
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_menu_resource")
public class MenuResourceEntity extends BaseTenantEntity {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 菜单id
     */
    @TableField("menu_id")
    private String menuId;


    /**
     * 请求方法
     */
    @TableField("url_method")
    private String urlMethod;


    /**
     * 请求地址
     */
    @TableField("url_path")
    private String urlPath;


    /**
     * 匹配类型,ANT ,REGX
     */
    @TableField("match_type")
    private String matchType;


    /**
     * 资源鉴权类型,无需鉴权、登录鉴权、完整鉴权
     */
    @TableField("auth_type")
    private String authType;


    /**
     * 资源描述
     */
    @TableField("description")
    private String description;


}