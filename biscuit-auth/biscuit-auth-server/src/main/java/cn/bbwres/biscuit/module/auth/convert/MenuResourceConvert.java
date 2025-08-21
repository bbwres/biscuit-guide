package cn.bbwres.biscuit.module.auth.convert;

import java.util.*;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import cn.bbwres.biscuit.module.auth.controller.vo.*;
import cn.bbwres.biscuit.module.auth.entity.MenuResourceEntity;
import cn.bbwres.biscuit.dto.Page;



/**
 * <p>
 * 菜单资源表 Convert
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Mapper
public interface MenuResourceConvert {

    /**
     * 转换对象
     */
     MenuResourceConvert INSTANCE = Mappers.getMapper(MenuResourceConvert.class);


    /**
     * 对象转换
     * @param bean 分页查询条件
     * @return
     */
     MenuResourceRespVO convert(MenuResourceEntity bean);

    /**
     * 转换list字段
     * @param list 请求list
     * @return
     */
    List<MenuResourceRespVO> convertList(List<MenuResourceEntity> list);

    /**
     * 分页查询数据
     * @param page 分页数据
     * @return
     */
    Page<MenuResourceRespVO,MenuResourcePageReqVO> convertPage(Page<MenuResourceEntity,MenuResourcePageReqVO> page);


}
