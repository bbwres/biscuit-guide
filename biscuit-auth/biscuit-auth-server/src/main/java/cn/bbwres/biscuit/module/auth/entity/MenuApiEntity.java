package cn.bbwres.biscuit.module.auth.entity;

import cn.bbwres.biscuit.entity.BaseNameTenantEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * 菜单接口关联表
 * <p>
 * 一个菜单可对应多个后端请求接口
 *
 * @author zlf
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_menu_api")
public class MenuApiEntity extends BaseNameTenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 菜单id
     */
    @TableField("menu_id")
    private String menuId;

    /**
     * 请求接口地址
     */
    @TableField("api_url")
    private String apiUrl;

    /**
     * 请求接口方法
     */
    @TableField("api_url_method")
    private String apiUrlMethod;

}
