package cn.bbwres.biscuit.module.auth.controller;


import cn.bbwres.biscuit.dto.Result;
import cn.bbwres.biscuit.entity.UserBaseInfo;
import cn.bbwres.biscuit.module.auth.api.vo.MenuTreeRespVO;
import cn.bbwres.biscuit.module.auth.controller.vo.RoleRespVO;
import cn.bbwres.biscuit.module.auth.convert.MenuConvert;
import cn.bbwres.biscuit.module.auth.convert.RoleConvert;
import cn.bbwres.biscuit.module.auth.entity.MenuEntity;
import cn.bbwres.biscuit.module.auth.entity.RoleEntity;
import cn.bbwres.biscuit.module.auth.service.cache.MenuCacheService;
import cn.bbwres.biscuit.module.auth.service.cache.RoleCacheService;
import cn.bbwres.biscuit.module.auth.utils.MenuTreeUtils;
import cn.bbwres.biscuit.web.utils.WebFrameworkUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户菜单信息
 *
 * @author zhanglinfeng
 */
@Slf4j
@Tag(name = "用户菜单信息")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RestController
@RequestMapping("/userMenu")
public class UserMenuController {

    private final RoleCacheService roleCacheService;
    private final MenuCacheService menuCacheService;


    /**
     * 获取当前登录用户的角色信息
     *
     * @return Result
     */
    @GetMapping("/roleInfo")
    @Operation(summary = "获取当前登录用户的角色信息")
    public Result<List<RoleRespVO>> roleInfo() {
        String userId = WebFrameworkUtils.getUserInfo(UserBaseInfo::getUserId);
        List<RoleEntity> roleEntityList = roleCacheService.findByAccountId(userId);
        if (CollectionUtils.isEmpty(roleEntityList)) {
            return Result.success(null);
        }
        return Result.success(RoleConvert.INSTANCE.convertList(roleEntityList));
    }


    /**
     * 获取当前登录用户的菜单权限信息
     *
     * @return Result
     */
    @GetMapping("/menuTree")
    @Operation(summary = "获取当前登录用户的菜单权限信息")
    public Result<List<MenuTreeRespVO>> menuTree() {
        UserBaseInfo userInfo = WebFrameworkUtils.getRequestUser(true);
        //获取当前用户的角色
        List<RoleEntity> roleEntityList = roleCacheService.findByAccountId(userInfo.getUserId());
        if (CollectionUtils.isEmpty(roleEntityList)) {
            return Result.success(null);
        }
        //过滤角色
        List<String> roleList = roleEntityList.stream()
                .filter(roleEntity -> roleEntity.getClientId().equals(userInfo.getClientId()))
                .map(RoleEntity::getId).toList();
        if (CollectionUtils.isEmpty(roleList)) {
            return Result.success(null);
        }
        List<MenuEntity> menuEntityList = new ArrayList<>(16);
        for (String roleId : roleList) {
            menuEntityList.addAll(menuCacheService.findByRoleId(roleId));
        }
        List<MenuTreeRespVO> menuTreeRespList = MenuConvert.INSTANCE.convertTreeList(menuEntityList);

        //获取出根节点
        List<MenuTreeRespVO> rootMenuEntityList = new ArrayList<>(16);
        for (MenuTreeRespVO menu : menuTreeRespList) {
            if (StringUtils.isBlank(menu.getParentId())) {
                rootMenuEntityList.add(menu);
            }
        }

        List<MenuTreeRespVO> menuTreeRespResult = MenuTreeUtils.buildMenuTree(s -> rootMenuEntityList,
                null,
                s -> menuTreeRespList);

        return Result.success(menuTreeRespResult);
    }
}
