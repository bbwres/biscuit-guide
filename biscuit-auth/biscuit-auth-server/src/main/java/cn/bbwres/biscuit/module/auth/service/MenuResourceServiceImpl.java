package cn.bbwres.biscuit.module.auth.service;


import java.util.*;
import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.auth.controller.vo.*;
import cn.bbwres.biscuit.module.auth.entity.MenuResourceEntity;
import cn.bbwres.biscuit.module.auth.dao.MenuResourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * <p>
 * 菜单资源表 服务实现类
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@RequiredArgsConstructor(onConstructor_={@Autowired})
@Slf4j
@Service
public class MenuResourceServiceImpl implements MenuResourceService {

    private final MenuResourceMapper menuResourceMapper;


    /**
     * 获得菜单资源表
     *
     * @param id 编号
     * @return 菜单资源表
     */
    @Override
    public MenuResourceEntity getMenuResource(String id) {
        return menuResourceMapper.selectById(id);
    }

    /**
     * 获得菜单资源表列表
     *
     * @param ids 编号
     * @return 菜单资源表列表
     */
    @Override
    public List<MenuResourceEntity> getMenuResourceList(Collection<String> ids) {
        return menuResourceMapper.selectByIds(ids);
    }

    /**
     * 获得菜单资源表分页
     *
     * @param pageReqVO 分页查询
     * @return 菜单资源表分页
     */
    @Override
    public  Page<MenuResourceEntity,MenuResourcePageReqVO> getMenuResourcePage(Page<MenuResourceEntity,MenuResourcePageReqVO> pageReqVO) {
        return menuResourceMapper.selectPage(pageReqVO);
    }


}
