package cn.bbwres.biscuit.module.auth.service;


import java.util.*;
import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.auth.controller.vo.*;
import cn.bbwres.biscuit.module.auth.entity.RoleAccountEntity;
import cn.bbwres.biscuit.module.auth.dao.RoleAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * <p>
 * 用户角色表 服务实现类
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@RequiredArgsConstructor(onConstructor_={@Autowired})
@Slf4j
@Service
public class RoleAccountServiceImpl implements RoleAccountService {

    private final RoleAccountMapper roleAccountMapper;


    /**
     * 获得用户角色表
     *
     * @param id 编号
     * @return 用户角色表
     */
    @Override
    public RoleAccountEntity getRoleAccount(String id) {
        return roleAccountMapper.selectById(id);
    }

    /**
     * 获得用户角色表列表
     *
     * @param ids 编号
     * @return 用户角色表列表
     */
    @Override
    public List<RoleAccountEntity> getRoleAccountList(Collection<String> ids) {
        return roleAccountMapper.selectByIds(ids);
    }

    /**
     * 获得用户角色表分页
     *
     * @param pageReqVO 分页查询
     * @return 用户角色表分页
     */
    @Override
    public  Page<RoleAccountEntity,RoleAccountPageReqVO> getRoleAccountPage(Page<RoleAccountEntity,RoleAccountPageReqVO> pageReqVO) {
        return roleAccountMapper.selectPage(pageReqVO);
    }


}
