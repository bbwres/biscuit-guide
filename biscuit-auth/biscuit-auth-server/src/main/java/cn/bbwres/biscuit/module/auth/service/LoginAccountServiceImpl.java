package cn.bbwres.biscuit.module.auth.service;


import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.auth.controller.vo.LoginAccountPageReqVO;
import cn.bbwres.biscuit.module.auth.dao.LoginAccountMapper;
import cn.bbwres.biscuit.module.auth.entity.LoginAccountEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public LoginAccountEntity findByLoginUsername(String tenantId,String username) {
        return loginAccountMapper.findByLoginUsername(tenantId,username);
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
        loginAccountMapper.insert(entity);
    }


}
