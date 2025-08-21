package cn.bbwres.biscuit.module.auth.convert;

import java.util.*;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import cn.bbwres.biscuit.module.auth.controller.vo.*;
import cn.bbwres.biscuit.module.auth.entity.MenuEntity;
import cn.bbwres.biscuit.dto.Page;



/**
 * <p>
 * 菜单权限表 Convert
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Mapper
public interface MenuConvert {

    /**
     * 转换对象
     */
     MenuConvert INSTANCE = Mappers.getMapper(MenuConvert.class);


    /**
     * 对象转换
     * @param bean 分页查询条件
     * @return
     */
     MenuRespVO convert(MenuEntity bean);

    /**
     * 转换list字段
     * @param list 请求list
     * @return
     */
    List<MenuRespVO> convertList(List<MenuEntity> list);

    /**
     * 分页查询数据
     * @param page 分页数据
     * @return
     */
    Page<MenuRespVO,MenuPageReqVO> convertPage(Page<MenuEntity,MenuPageReqVO> page);


}
