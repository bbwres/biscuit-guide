package cn.bbwres.biscuit.module.auth.entity;

import cn.bbwres.biscuit.entity.BaseTenantEntity;
import cn.bbwres.biscuit.module.auth.enums.LoginAccountStatusEnum;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 登陆账户表
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Table("t_login_account")
public class LoginAccountEntity extends BaseTenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private String id;


    /**
     * 登陆账号
     */
    @Column("login_name")
    private String loginName;


    /**
     * 登陆密码
     */
    @Column("login_password")
    private String loginPassword;


    /**
     * 手机号
     */
    @Column("phone")
    private String phone;


    /**
     * 关联用户表id
     */
    @Column("user_id")
    private String userId;


    /**
     * 登陆用户状态
     */
    @Column("status")
    private LoginAccountStatusEnum status;


    /**
     * 锁定到期时间
     */
    @Column("locked_time")
    private LocalDateTime lockedTime;


    /**
     * 姓名
     */
    @Column("name")
    private String name;


    /**
     * 最后一次修改密码时间
     */
    @Column("last_update_password_time")
    private LocalDateTime lastUpdatePasswordTime;


}