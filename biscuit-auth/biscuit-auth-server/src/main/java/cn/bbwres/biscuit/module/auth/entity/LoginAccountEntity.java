package cn.bbwres.biscuit.module.auth.entity;

import cn.bbwres.biscuit.entity.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
*
* 登陆账户表
*
* @author zlf
* @Date 2025-08-19
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_login_account")
public class LoginAccountEntity extends BaseTenantEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 登陆账号
     */
    @TableField("login_name")
    private String loginName;


    /**
     * 登陆密码
     */
    @TableField("login_password")
    private String loginPassword;


    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;




    /**
     * 关联用户表id
     */
    @TableField("user_id")
    private String userId;


    /**
     * 登陆用户状态
     */
    @TableField("status")
    private String status;


    /**
     * 锁定到期时间
     */
    @TableField("locked_time")
    private LocalDateTime lockedTime;


    /**
     * 姓名
     */
    @TableField("name")
    private String name;


    /**
     * 最后一次修改密码时间
     */
    @TableField("last_update_password_time")
    private LocalDateTime lastUpdatePasswordTime;




}