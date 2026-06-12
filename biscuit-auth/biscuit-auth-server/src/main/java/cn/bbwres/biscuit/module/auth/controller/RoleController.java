package cn.bbwres.biscuit.module.auth.controller;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.dto.Result;
import cn.bbwres.biscuit.entity.UserBaseInfo;
import cn.bbwres.biscuit.module.auth.constants.AuthErrorCodeConstants;
import cn.bbwres.biscuit.module.auth.controller.vo.RoleAddMenuReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.RoleAddReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.RolePageReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.RoleRespVO;
import cn.bbwres.biscuit.module.auth.api.vo.MenuRespVO;
import cn.bbwres.biscuit.module.auth.api.vo.MenuTreeRespVO;
import cn.bbwres.biscuit.module.auth.convert.MenuConvert;
import cn.bbwres.biscuit.module.auth.convert.RoleConvert;
import cn.bbwres.biscuit.module.auth.entity.MenuEntity;
import cn.bbwres.biscuit.module.auth.entity.RoleEntity;
import cn.bbwres.biscuit.module.auth.service.RoleService;
import cn.bbwres.biscuit.module.auth.service.cache.MenuCacheService;
import cn.bbwres.biscuit.module.auth.service.cache.RoleCacheService;
import cn.bbwres.biscuit.module.auth.utils.MenuTreeUtils;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 角色表 controller
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Slf4j
@Tag(name = " 角色表")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RestController
@RequestMapping("/role")
public class RoleController {
    private final RoleService roleService;

    private final RoleCacheService roleCacheService;
    private final MenuCacheService menuCacheService;

    /**
     * 分页参数信息
     *
     * @param pageVO 分页参数
     * @return Result
     */
    @PostMapping("/page")
    @Operation(summary = "获得角色表分页")
    public Result<Page<RoleRespVO, RolePageReqVO>> getRolePage(@Validated @RequestBody Page<RoleEntity, RolePageReqVO> pageVO) {
        Page<RoleEntity, RolePageReqVO> pageResult = roleService.getRolePage(pageVO);
        return Result.success(RoleConvert.INSTANCE.convertPage(pageResult));
    }

    /**
     * 根据id获取数据
     *
     * @return Result
     */
    @GetMapping("/getById")
    @Operation(summary = "根据id获取角色的数据",
            parameters = {
                    @Parameter(name = "id", description = "id", required = true),
                    @Parameter(name = "withMenus", description = "是否查询关联菜单（可选，默认false）")
            })
    public Result<RoleRespVO> getById(@RequestParam("id") String entityId,
                                       @RequestParam(value = "withMenus", required = false, defaultValue = "false") Boolean withMenus) {
        RoleRespVO respVO = RoleConvert.INSTANCE.convert(roleService.getRole(entityId));
        if (Boolean.TRUE.equals(withMenus)) {
            List<MenuEntity> menuEntities = roleService.findMenusByRoleId(entityId);
            List<MenuTreeRespVO> flatMenus = MenuConvert.INSTANCE.convertTreeList(menuEntities);
            respVO.setMenus(MenuTreeUtils.buildMenuTreeByParentId(flatMenus));
        }
        return Result.success(respVO);
    }


    /**
     * 新增角色
     *
     * @return Result
     */
    @PostMapping("/addRole")
    @Operation(summary = "新增角色")
    public Result<Void> addRole(@RequestBody @Validated(ValidateAddGroup.class) RoleAddReqVO roleAddReq) {
        log.info("当前用户:[{}]新增角色信息:[{}]", WebFrameworkUtils.getUserInfo(UserBaseInfo::getUsername), roleAddReq);
        RoleEntity oldRoleEntity = roleService.findByRoleCodeAndClientId(roleAddReq.getRoleCode(), roleAddReq.getClientId());
        if (Objects.nonNull(oldRoleEntity)) {
            return Result.error(AuthErrorCodeConstants.DATA_ALREADY_EXISTS_ERROR);
        }
        //查询当前的角色编码是否已经存在
        RoleEntity roleEntity = RoleConvert.INSTANCE.convertByAddReq(roleAddReq);
        roleService.addRole(roleEntity);
        return Result.success(null);
    }


    /**
     * 修改角色
     *
     * @return Result
     */
    @PostMapping("/editRole")
    @Operation(summary = "修改角色")
    public Result<Void> editRole(@RequestBody @Validated(ValidateEditGroup.class) RoleAddReqVO roleEditReq) {
        log.info("当前用户:[{}]修改角色信息:[{}]", WebFrameworkUtils.getUserInfo(UserBaseInfo::getUsername), roleEditReq);
        RoleEntity oldRoleEntity = roleService.findById(roleEditReq.getId());
        if (Objects.isNull(oldRoleEntity)) {
            return Result.error(AuthErrorCodeConstants.DATA_NO_EXISTS_ERROR);
        }
        oldRoleEntity.setRoleName(roleEditReq.getRoleName())
                .setClientId(roleEditReq.getClientId())
                .setRemark(roleEditReq.getRemark());

        roleService.updateById(oldRoleEntity);
        deleteAccountCache(roleEditReq.getId());
        return Result.success(null);
    }


    /**
     * 修改角色 状态
     *
     * @return Result
     */
    @PostMapping("/editRoleStatus")
    @Operation(summary = "修改角色状态")
    public Result<Void> editRoleStatus(@RequestBody @Validated(ValidateEditStatusGroup.class) RoleAddReqVO roleEditReq) {
        log.info("当前用户:[{}]修改角色状态信息:[{}]", WebFrameworkUtils.getUserInfo(UserBaseInfo::getUsername), roleEditReq);
        RoleEntity roleEntity = roleService.findById(roleEditReq.getId());
        if (Objects.isNull(roleEntity)) {
            return Result.error(AuthErrorCodeConstants.DATA_NO_EXISTS_ERROR);
        }
        if (roleEntity.getStatus().equals(roleEditReq.getStatus())) {
            return Result.success(null);
        }
        roleEntity.setStatus(roleEditReq.getStatus());
        roleService.updateById(roleEntity);
        deleteAccountCache(roleEditReq.getId());
        return Result.success(null);
    }


    /**
     * 角色菜单配置
     *
     * @return Result
     */
    @PostMapping("/roleMenuConfig")
    @Operation(summary = "角色菜单配置")
    public Result<Void> roleMenuConfig(@RequestBody @Validated RoleAddMenuReqVO roleAddMenuReq) {
        log.info("当前用户:[{}]角色菜单配置信息:[{}]", WebFrameworkUtils.getUserInfo(UserBaseInfo::getUsername), roleAddMenuReq);
        RoleEntity roleEntity = roleService.findById(roleAddMenuReq.getId());
        if (Objects.isNull(roleEntity)) {
            return Result.error(AuthErrorCodeConstants.DATA_NO_EXISTS_ERROR);
        }
        roleService.roleMenuConfig(roleEntity, roleAddMenuReq);
        deleteAccountCache(roleAddMenuReq.getId());
        return Result.success(null);
    }


    /**
     * 根据roleId删除account的角色缓存
     *
     * @param roleId 角色id
     */
    private void deleteAccountCache(String roleId) {
        menuCacheService.deleteCacheByRoleId(roleId);
        List<String> accountIds = roleService.findAccountsByRoleId(roleId);
        for (String accountId : accountIds) {
            roleCacheService.deleteCacheByAccountId(accountId);
        }

    }


}
