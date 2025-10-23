package cn.bbwres.biscuit.module.auth.service;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.auth.controller.vo.LoginAccountPageReqVO;
import cn.bbwres.biscuit.module.auth.entity.LoginAccountEntity;

import java.util.Collection;
import java.util.List;


/**
 * <p>
 * 登陆账户表 服务类
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
public interface LoginAccountService {

    /**
     * 获得登陆账户表
     *
     * @param id 编号
     * @return 登陆账户表
     */
    LoginAccountEntity getLoginAccount(String id);

    /**
     * 根据用户名称查询数据
     * @param username
     * @return
     */
    LoginAccountEntity findByLoginUsername(String username);

    /**
     * 获得登陆账户表列表
     *
     * @param ids 编号
     * @return 登陆账户表列表
     */
    List<LoginAccountEntity> getLoginAccountList(Collection<String> ids);

    /**
     * 获得登陆账户表分页
     *
     * @param pageReqVO 分页查询
     * @return 登陆账户表分页
     */
    Page<LoginAccountEntity, LoginAccountPageReqVO> getLoginAccountPage(Page<LoginAccountEntity, LoginAccountPageReqVO> pageReqVO);


    /**
     * 根据id更新数据
     *
     * @param entity
     * @return
     */
    boolean updateById(LoginAccountEntity entity);

    /**
     * 新增用户信息
     *
     * @param entity
     */
    void save(LoginAccountEntity entity);


}
