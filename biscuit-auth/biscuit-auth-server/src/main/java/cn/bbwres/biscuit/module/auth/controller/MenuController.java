package cn.bbwres.biscuit.module.auth.controller;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.dto.Result;
import cn.bbwres.biscuit.entity.UserBaseInfo;
import cn.bbwres.biscuit.module.auth.constants.AuthErrorCodeConstants;
import cn.bbwres.biscuit.module.auth.controller.vo.MenuAddReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.MenuPageReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.MenuRespVO;
import cn.bbwres.biscuit.module.auth.convert.MenuConvert;
import cn.bbwres.biscuit.module.auth.entity.MenuEntity;
import cn.bbwres.biscuit.module.auth.service.MenuService;
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
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        return Result.success(MenuConvert.INSTANCE.convert(menuService.getMenu(entityId)));
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
        menuService.addMenu(MenuConvert.INSTANCE.convertByAddReq(req), parentMenu);
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
        menuService.editMenu(oldMenu, updateMenu, parentMenu);
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
        return Result.success(null);
    }

}
