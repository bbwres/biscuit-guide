package cn.bbwres.biscuit.module.auth.service;


import java.util.*;
import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.auth.controller.vo.*;
import cn.bbwres.biscuit.module.auth.entity.RoleMenuEntity;
import cn.bbwres.biscuit.module.auth.dao.RoleMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * <p>
 * 角色所属的资源 服务实现类
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@RequiredArgsConstructor(onConstructor_={@Autowired})
@Slf4j
@Service
public class RoleMenuServiceImpl implements RoleMenuService {

    private final RoleMenuMapper roleMenuMapper;


    /**
     * 获得角色所属的资源
     *
     * @param id 编号
     * @return 角色所属的资源
     */
    @Override
    public RoleMenuEntity getRoleMenu(String id) {
        return roleMenuMapper.selectById(id);
    }

    /**
     * 获得角色所属的资源列表
     *
     * @param ids 编号
     * @return 角色所属的资源列表
     */
    @Override
    public List<RoleMenuEntity> getRoleMenuList(Collection<String> ids) {
        return roleMenuMapper.selectByIds(ids);
    }

    /**
     * 获得角色所属的资源分页
     *
     * @param pageReqVO 分页查询
     * @return 角色所属的资源分页
     */
    @Override
    public  Page<RoleMenuEntity,RoleMenuPageReqVO> getRoleMenuPage(Page<RoleMenuEntity,RoleMenuPageReqVO> pageReqVO) {
        return roleMenuMapper.selectPage(pageReqVO);
    }


}
