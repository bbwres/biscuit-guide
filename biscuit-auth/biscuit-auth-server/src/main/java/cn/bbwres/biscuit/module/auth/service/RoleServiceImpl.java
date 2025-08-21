package cn.bbwres.biscuit.module.auth.service;


import java.util.*;
import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.auth.controller.vo.*;
import cn.bbwres.biscuit.module.auth.entity.RoleEntity;
import cn.bbwres.biscuit.module.auth.dao.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@RequiredArgsConstructor(onConstructor_={@Autowired})
@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;


    /**
     * 获得角色表
     *
     * @param id 编号
     * @return 角色表
     */
    @Override
    public RoleEntity getRole(String id) {
        return roleMapper.selectById(id);
    }

    /**
     * 获得角色表列表
     *
     * @param ids 编号
     * @return 角色表列表
     */
    @Override
    public List<RoleEntity> getRoleList(Collection<String> ids) {
        return roleMapper.selectByIds(ids);
    }

    /**
     * 获得角色表分页
     *
     * @param pageReqVO 分页查询
     * @return 角色表分页
     */
    @Override
    public  Page<RoleEntity,RolePageReqVO> getRolePage(Page<RoleEntity,RolePageReqVO> pageReqVO) {
        return roleMapper.selectPage(pageReqVO);
    }


}
