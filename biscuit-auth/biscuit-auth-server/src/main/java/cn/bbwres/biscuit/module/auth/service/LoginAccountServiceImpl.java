package cn.bbwres.biscuit.module.auth.service;


import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.exception.SystemRuntimeException;
import cn.bbwres.biscuit.module.auth.constants.AuthErrorCodeConstants;
import cn.bbwres.biscuit.module.auth.controller.vo.LoginAccountAddOrUpdateReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.LoginAccountAddRoleReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.LoginAccountEditPasswordReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.LoginAccountEditReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.LoginAccountPageReqVO;
import cn.bbwres.biscuit.module.auth.dao.LoginAccountMapper;
import cn.bbwres.biscuit.module.auth.dao.RoleAccountMapper;
import cn.bbwres.biscuit.module.auth.entity.LoginAccountEntity;
import cn.bbwres.biscuit.module.auth.entity.RoleAccountEntity;
import cn.bbwres.biscuit.module.auth.entity.RoleEntity;
import cn.bbwres.biscuit.module.auth.enums.LoginAccountStatusEnum;
import cn.bbwres.biscuit.module.auth.service.cache.RoleCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


/**
 * <p>
 * 登陆账户表 服务实现类
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
@Service
public class LoginAccountServiceImpl implements LoginAccountService {

    private final LoginAccountMapper loginAccountMapper;

    private final PasswordEncoder passwordEncoder;

    private final RoleAccountMapper roleAccountMapper;
    private final RoleCacheService roleCacheService;


    /**
     * 获得登陆账户表
     *
     * @param id 编号
     * @return 登陆账户表
     */
    @Override
    public LoginAccountEntity getLoginAccount(String id) {
        return loginAccountMapper.selectById(id);
    }

    /**
     * 根据用户名称查询数据
     *
     * @param tenantId
     * @param username
     * @return
     */
    @Override
    public LoginAccountEntity findByLoginUsernameNoTenant(String tenantId, String username) {
        return loginAccountMapper.findByLoginUsernameNoTenant(tenantId, username);
    }

    /**
     * 获得登陆账户表列表
     *
     * @param ids 编号
     * @return 登陆账户表列表
     */
    @Override
    public List<LoginAccountEntity> getLoginAccountList(Collection<String> ids) {
        return loginAccountMapper.selectByIds(ids);
    }

    /**
     * 获得登陆账户表分页
     *
     * @param pageReqVO 分页查询
     * @return 登陆账户表分页
     */
    @Override
    public Page<LoginAccountEntity, LoginAccountPageReqVO> getLoginAccountPage(Page<LoginAccountEntity, LoginAccountPageReqVO> pageReqVO) {
        return loginAccountMapper.selectPage(pageReqVO);
    }

    /**
     * 根据id更新数据
     *
     * @param entity
     * @return
     */
    @Override
    public boolean updateById(LoginAccountEntity entity) {
        return loginAccountMapper.updateById(entity) > 0;
    }

    /**
     * 新增用户信息
     *
     * @param entity
     */
    @Override
    public void save(LoginAccountEntity entity) {
        entity.setLoginPassword(passwordEncoder.encode(entity.getLoginPassword()));
        entity.setStatus(LoginAccountStatusEnum.UNACTIVATED);
        entity.setLastUpdatePasswordTime(LocalDateTime.now());
        loginAccountMapper.insert(entity);
    }

    /**
     * 账号角色配置
     *
     * @param loginAccountAddRoleReq
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void accountRoleConfig(LoginAccountAddRoleReqVO loginAccountAddRoleReq) {
        //先删除所有账户角色数据
        roleAccountMapper.deleteByAccountId(loginAccountAddRoleReq.getId());
        if (CollectionUtils.isEmpty(loginAccountAddRoleReq.getRoleIds())) {
            log.info("当前账号id:[{}]配置的角色信息为空!", loginAccountAddRoleReq.getId());
            return;
        }
        List<RoleAccountEntity> roleAccounts = loginAccountAddRoleReq.getRoleIds().stream().map(roleId -> new RoleAccountEntity()
                .setLoginAccountId(loginAccountAddRoleReq.getId())
                .setRoleId(roleId)).toList();
        roleAccountMapper.insert(roleAccounts);
        // 清除该账户的角色缓存，使配置立即生效
        roleCacheService.deleteCacheByAccountId(loginAccountAddRoleReq.getId());
    }

    /**
     * 检查用户是否配置角色信息
     *
     * @param id
     * @return
     */
    @Override
    public boolean checkUserRole(String id) {
        return roleAccountMapper.countByAccountId(id) > 0;
    }

    /**
     * 修改账户状态
     *
     * @param loginAccountAddOrUpdateReq
     */
    @Override
    public void editAccountStatus(LoginAccountAddOrUpdateReqVO loginAccountAddOrUpdateReq) {
        LoginAccountEntity entity = new LoginAccountEntity();
        entity.setId(loginAccountAddOrUpdateReq.getId());
        entity.setStatus(loginAccountAddOrUpdateReq.getStatus());
        loginAccountMapper.updateById(entity);
    }

    /**
     * 编辑账户信息（姓名、手机号）
     * <p>如果手机号包含掩码字符（*），说明前端未修改，跳过手机号更新</p>
     *
     * @param loginAccountEditReq
     */
    @Override
    public void editAccount(LoginAccountEditReqVO loginAccountEditReq) {
        LoginAccountEntity entity = new LoginAccountEntity();
        entity.setId(loginAccountEditReq.getId());
        entity.setName(loginAccountEditReq.getName());
        // 手机号包含掩码字符 * 时视为未修改，不更新
        String phone = loginAccountEditReq.getPhone();
        if (phone != null && !phone.contains("*")) {
            entity.setPhone(phone);
        }
        loginAccountMapper.updateById(entity);
    }

    /**
     * 修改密码
     *
     * @param entity
     * @param loginAccountEditPasswordReq
     */
    @Override
    public void editAccountPassword(LoginAccountEntity entity, LoginAccountEditPasswordReqVO loginAccountEditPasswordReq) {
        if (!ObjectUtils.isEmpty(loginAccountEditPasswordReq.getOldPassword())) {
            log.info("当前用户:[{}]修改密码，校验原密码是否正确", entity.getName());
            if (!passwordEncoder.matches(loginAccountEditPasswordReq.getOldPassword(), entity.getLoginPassword())) {
                throw new SystemRuntimeException(AuthErrorCodeConstants.ACCOUNT_PASSWORD_ERROR);
            }
        }
        LoginAccountEntity updateEntity = new LoginAccountEntity();
        updateEntity.setId(loginAccountEditPasswordReq.getId());
        updateEntity.setLoginPassword(passwordEncoder.encode(loginAccountEditPasswordReq.getNewPassword()));
        updateEntity.setLastUpdatePasswordTime(LocalDateTime.now());
        loginAccountMapper.updateById(updateEntity);
    }


}
