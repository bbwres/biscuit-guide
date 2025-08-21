package cn.bbwres.biscuit.module.auth.service;


import java.util.*;
import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.auth.controller.vo.*;
import cn.bbwres.biscuit.module.auth.entity.LoginAccountEntity;
import cn.bbwres.biscuit.module.auth.dao.LoginAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * <p>
 * 登陆账户表 服务实现类
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@RequiredArgsConstructor(onConstructor_={@Autowired})
@Slf4j
@Service
public class LoginAccountServiceImpl implements LoginAccountService {

    private final LoginAccountMapper loginAccountMapper;


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
    public  Page<LoginAccountEntity,LoginAccountPageReqVO> getLoginAccountPage(Page<LoginAccountEntity,LoginAccountPageReqVO> pageReqVO) {
        return loginAccountMapper.selectPage(pageReqVO);
    }


}
