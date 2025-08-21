package cn.bbwres.biscuit.module.auth.convert;

import java.util.*;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import cn.bbwres.biscuit.module.auth.controller.vo.*;
import cn.bbwres.biscuit.module.auth.entity.RoleMenuEntity;
import cn.bbwres.biscuit.dto.Page;



/**
 * <p>
 * 角色所属的资源 Convert
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Mapper
public interface RoleMenuConvert {

    /**
     * 转换对象
     */
     RoleMenuConvert INSTANCE = Mappers.getMapper(RoleMenuConvert.class);


    /**
     * 对象转换
     * @param bean 分页查询条件
     * @return
     */
     RoleMenuRespVO convert(RoleMenuEntity bean);

    /**
     * 转换list字段
     * @param list 请求list
     * @return
     */
    List<RoleMenuRespVO> convertList(List<RoleMenuEntity> list);

    /**
     * 分页查询数据
     * @param page 分页数据
     * @return
     */
    Page<RoleMenuRespVO,RoleMenuPageReqVO> convertPage(Page<RoleMenuEntity,RoleMenuPageReqVO> page);


}
