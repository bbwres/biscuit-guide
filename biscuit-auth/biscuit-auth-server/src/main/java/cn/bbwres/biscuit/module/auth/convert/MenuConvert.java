package cn.bbwres.biscuit.module.auth.convert;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.auth.api.vo.MenuApiRespVO;
import cn.bbwres.biscuit.module.auth.api.vo.MenuTreeRespVO;
import cn.bbwres.biscuit.module.auth.controller.vo.MenuAddReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.MenuApiItemVO;
import cn.bbwres.biscuit.module.auth.controller.vo.MenuPageReqVO;
import cn.bbwres.biscuit.module.auth.api.vo.MenuRespVO;
import cn.bbwres.biscuit.module.auth.entity.MenuApiEntity;
import cn.bbwres.biscuit.module.auth.entity.MenuEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


/**
 * <p>
 * 菜单权限表 Convert
 * </p>
 *
 * @author zlf
 * @Date 2025-10-25
 */
@Mapper
public interface MenuConvert {

    /**
     * 转换对象
     */
    MenuConvert INSTANCE = Mappers.getMapper(MenuConvert.class);


    /**
     * 对象转换
     *
     * @param bean 分页查询条件
     * @return
     */
    MenuRespVO convert(MenuEntity bean);

    /**
     * 转换list字段
     *
     * @param list 请求list
     * @return
     */
    List<MenuRespVO> convertList(List<MenuEntity> list);


    /**
     * 转换为tree的list
     * @param list
     * @return
     */
    List<MenuTreeRespVO> convertTreeList(List<MenuEntity> list);

    /**
     * 分页查询数据
     *
     * @param page 分页数据
     * @return
     */
    Page<MenuRespVO, MenuPageReqVO> convertPage(Page<MenuEntity, MenuPageReqVO> page);


    /**
     * 转换数据
     *
     * @param req
     * @return
     */
    MenuEntity convertByAddReq(MenuAddReqVO req);

    /**
     * 菜单接口实体 -> VO
     *
     * @param entity 菜单接口实体
     * @return 菜单接口 VO
     */
    MenuApiRespVO convertApi(MenuApiEntity entity);

    /**
     * 菜单接口实体列表 -> VO 列表
     *
     * @param list 菜单接口实体列表
     * @return 菜单接口 VO 列表
     */
    List<MenuApiRespVO> convertApiList(List<MenuApiEntity> list);

    /**
     * 菜单接口请求项 -> 实体
     *
     * @param item 菜单接口请求项
     * @return 菜单接口实体
     */
    MenuApiEntity convertApiByItem(MenuApiItemVO item);

    /**
     * 菜单接口请求项列表 -> 实体列表
     *
     * @param list 菜单接口请求项列表
     * @return 菜单接口实体列表
     */
    List<MenuApiEntity> convertApiEntityList(List<MenuApiItemVO> list);
}
