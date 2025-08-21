package cn.bbwres.biscuit.module.auth.service;


import java.util.*;
import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.auth.controller.vo.*;
import cn.bbwres.biscuit.module.auth.entity.MenuEntity;
import cn.bbwres.biscuit.module.auth.dao.MenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * <p>
 * 菜单权限表 服务实现类
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@RequiredArgsConstructor(onConstructor_={@Autowired})
@Slf4j
@Service
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;


    /**
     * 获得菜单权限表
     *
     * @param id 编号
     * @return 菜单权限表
     */
    @Override
    public MenuEntity getMenu(String id) {
        return menuMapper.selectById(id);
    }

    /**
     * 获得菜单权限表列表
     *
     * @param ids 编号
     * @return 菜单权限表列表
     */
    @Override
    public List<MenuEntity> getMenuList(Collection<String> ids) {
        return menuMapper.selectByIds(ids);
    }

    /**
     * 获得菜单权限表分页
     *
     * @param pageReqVO 分页查询
     * @return 菜单权限表分页
     */
    @Override
    public  Page<MenuEntity,MenuPageReqVO> getMenuPage(Page<MenuEntity,MenuPageReqVO> pageReqVO) {
        return menuMapper.selectPage(pageReqVO);
    }


}
