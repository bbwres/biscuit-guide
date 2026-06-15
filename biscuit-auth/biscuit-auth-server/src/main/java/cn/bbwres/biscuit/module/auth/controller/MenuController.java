package cn.bbwres.biscuit.module.auth.controller;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.dto.Result;
import cn.bbwres.biscuit.entity.UserBaseInfo;
import cn.bbwres.biscuit.module.auth.api.vo.MenuRespVO;
import cn.bbwres.biscuit.module.auth.api.vo.MenuTreeRespVO;
import cn.bbwres.biscuit.module.auth.constants.AuthErrorCodeConstants;
import cn.bbwres.biscuit.module.auth.controller.vo.MenuAddReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.MenuPageReqVO;
import cn.bbwres.biscuit.module.auth.convert.MenuConvert;
import cn.bbwres.biscuit.module.auth.entity.MenuApiEntity;
import cn.bbwres.biscuit.module.auth.entity.MenuEntity;
import cn.bbwres.biscuit.module.auth.service.MenuService;
import cn.bbwres.biscuit.module.auth.service.cache.MenuCacheService;
import cn.bbwres.biscuit.validate.ValidateAddGroup;
import cn.bbwres.biscuit.validate.ValidateEditGroup;
import cn.bbwres.biscuit.validate.ValidateEditStatusGroup;
import cn.bbwres.biscuit.web.utils.WebFrameworkUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 菜单权限表 controller
 * </p>
 *
 * @author zlf
 * @Date 2025-10-25
 */
@Slf4j
@Tag(name = " 菜单权限表")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RestController
@RequestMapping("/menu")
public class MenuController {
    private final MenuService menuService;

    private final MenuCacheService menuCacheService;


    /**
     * 分页参数信息
     *
     * @param pageVO 分页参数
     * @return Result
     */
    @PostMapping("/page")
    @Operation(summary = "获得菜单权限表分页")
    public Result<Page<MenuRespVO, MenuPageReqVO>> getMenuPage(@Validated @RequestBody Page<MenuEntity, MenuPageReqVO> pageVO) {
        Page<MenuEntity, MenuPageReqVO> pageResult = menuService.getMenuPage(pageVO);
        return Result.success(MenuConvert.INSTANCE.convertPage(pageResult));
    }

    /**
     * 根据id获取数据
     *
     * @return Result
     */
    @GetMapping("/getById")
    @Operation(summary = "根据id获取菜单权限表详情数据", parameters = {@Parameter(name = "id", description = "id", required = true)})
    public Result<MenuRespVO> getById(@RequestParam("id") String entityId) {
        MenuEntity entity = menuService.getMenu(entityId);
        if (ObjectUtils.isEmpty(entity)) {
            return Result.success(null);
        }
        MenuRespVO resp = MenuConvert.INSTANCE.convert(entity);
        // 加载关联接口列表
        resp.setMenuApiList(MenuConvert.INSTANCE.convertApiList(menuService.findApiListByMenuId(entityId)));
        return Result.success(resp);
    }

    /**
     * 根据id获取树形结构
     *
     * @param entityId
     * @return
     */
    @GetMapping("/getMenuTreeById")
    @Operation(summary = "根据id获取菜单权限的树形结构", parameters = {@Parameter(name = "id", description = "id")})
    public Result<List<MenuTreeRespVO>> getMenuTreeById(@RequestParam(value = "id", required = false) String entityId) {
        return Result.success(menuService.getMenuTreeById(entityId));
    }

    /**
     * 新增菜单信息
     *
     * @param req
     * @return
     */
    @PostMapping("/addMenu")
    @Operation(summary = "新增菜单")
    public Result<Void> addMenu(@Validated(ValidateAddGroup.class) @RequestBody MenuAddReqVO req) {
        log.info("当前用户:[{}]新增菜单信息:[{}]", WebFrameworkUtils.getUserInfo(UserBaseInfo::getUsername), req);
        MenuEntity parentMenu = menuService.getMenu(req.getParentId());
        if (ObjectUtils.isEmpty(parentMenu) && !ObjectUtils.isEmpty(req.getParentId())) {
            log.warn("新增菜单信息失败，传入了父级id，但是根据父级id没有查询到父级数据。父级id:[{}]", req.getParentId());
            return Result.error(AuthErrorCodeConstants.DATA_NO_EXISTS_ERROR);
        }
        List<MenuApiEntity> apiList = CollectionUtils.isEmpty(req.getMenuApiList())
                ? Collections.emptyList()
                : MenuConvert.INSTANCE.convertApiEntityList(req.getMenuApiList());
        menuService.addMenu(MenuConvert.INSTANCE.convertByAddReq(req), parentMenu, apiList);
        return Result.success(null);
    }

    /**
     * 修改菜单信息
     *
     * @param req
     * @return
     */
    @PostMapping("/editMenu")
    @Operation(summary = "修改菜单")
    public Result<Void> editMenu(@Validated(ValidateEditGroup.class) @RequestBody MenuAddReqVO req) {
        log.info("当前用户:[{}]修改修改菜单信息信息:[{}]", WebFrameworkUtils.getUserInfo(UserBaseInfo::getUsername), req);
        MenuEntity parentMenu = menuService.getMenu(req.getParentId());
        if (ObjectUtils.isEmpty(parentMenu) && !ObjectUtils.isEmpty(req.getParentId())) {
            log.warn("修改菜单失败，传入了父级id，但是根据父级id没有查询到父级数据。父级id:[{}]", req.getParentId());
            return Result.error(AuthErrorCodeConstants.DATA_NO_EXISTS_ERROR);
        }
        MenuEntity oldMenu = menuService.getMenu(req.getId());
        if (ObjectUtils.isEmpty(oldMenu)) {
            log.warn("修改菜单失败，根据id每次有查询到数据，id:[{}]", req.getId());
            return Result.error(AuthErrorCodeConstants.DATA_NO_EXISTS_ERROR);
        }
        MenuEntity updateMenu = MenuConvert.INSTANCE.convertByAddReq(req);
        List<MenuApiEntity> apiList = CollectionUtils.isEmpty(req.getMenuApiList())
                ? Collections.emptyList()
                : MenuConvert.INSTANCE.convertApiEntityList(req.getMenuApiList());
        menuService.editMenu(oldMenu, updateMenu, parentMenu, apiList);
        deleteRoleCache(req.getId());
        return Result.success(null);
    }


    /**
     * 修改菜单状态
     *
     * @param req
     * @return
     */
    @PostMapping("/editMenuStatus")
    @Operation(summary = "修改菜单状态")
    public Result<Void> editMenuStatus(@Validated(ValidateEditStatusGroup.class) @RequestBody MenuAddReqVO req) {
        log.info("当前用户:[{}]修改修改菜单状态信息:[{}]", WebFrameworkUtils.getUserInfo(UserBaseInfo::getUsername), req);
        MenuEntity oldMenu = menuService.getMenu(req.getId());
        if (ObjectUtils.isEmpty(oldMenu)) {
            log.warn("修改菜单失败，根据id每次有查询到数据，id:[{}]", req.getId());
            return Result.error(AuthErrorCodeConstants.DATA_NO_EXISTS_ERROR);
        }
        if (oldMenu.getStatus().equals(req.getStatus())) {
            return Result.success(null);
        }
        oldMenu.setStatus(req.getStatus());
        menuService.editMenuStatus(oldMenu);
        deleteRoleCache(req.getId());
        return Result.success(null);
    }

    /**
     * 删除菜单
     *
     * @param id 菜单id
     * @return Result
     */
    @PostMapping("/deleteMenu")
    @Operation(summary = "删除菜单", parameters = {@Parameter(name = "id", description = "菜单id", required = true)})
    public Result<Void> deleteMenu(@RequestParam("id") String id) {
        log.info("当前用户:[{}]删除菜单id:[{}]", WebFrameworkUtils.getUserInfo(UserBaseInfo::getUsername), id);
        MenuEntity menu = menuService.getMenu(id);
        if (ObjectUtils.isEmpty(menu)) {
            return Result.error(AuthErrorCodeConstants.DATA_NO_EXISTS_ERROR);
        }
        menuService.deleteMenu(id);
        deleteRoleCache(id);
        return Result.success(null);
    }

    /**
     * 刷新菜单缓存
     *
     * @return Result
     */
    @PostMapping("/refreshCache")
    @Operation(summary = "刷新菜单缓存")
    public Result<Void> refreshCache() {
        log.info("当前用户:[{}]刷新菜单缓存", WebFrameworkUtils.getUserInfo(UserBaseInfo::getUsername));
        menuCacheService.refreshAllCache();
        return Result.success(null);
    }

    /**
     * 根据menuId删除角色缓存信息
     *
     * @param menuId 菜单id
     */
    private void deleteRoleCache(String menuId) {
        List<String> roleIds = menuService.findRolesByMenuId(menuId);
        for (String roleId : roleIds) {
            menuCacheService.deleteCacheByRoleId(roleId);
        }
    }

}
