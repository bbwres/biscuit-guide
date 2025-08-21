package cn.bbwres.biscuit.module.auth.service;

import java.util.*;
import cn.bbwres.biscuit.module.auth.controller.vo.*;
import cn.bbwres.biscuit.module.auth.entity.LoginAccountEntity;
import cn.bbwres.biscuit.dto.Page;





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
    Page<LoginAccountEntity,LoginAccountPageReqVO> getLoginAccountPage(Page<LoginAccountEntity,LoginAccountPageReqVO> pageReqVO);


}
