package cn.bbwres.biscuit.module.basic.convert;

import java.util.*;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import cn.bbwres.biscuit.module.basic.controller.vo.*;
import cn.bbwres.biscuit.module.basic.entity.SystemDictTypeEntity;
import cn.bbwres.biscuit.dto.Page;



/**
 * <p>
 * 字典类型表 Convert
 * </p>
 *
 * @author zlf
 * @since 2025-11-26
 */
@Mapper
public interface SystemDictTypeConvert {

    /**
     * 转换对象
     */
     SystemDictTypeConvert INSTANCE = Mappers.getMapper(SystemDictTypeConvert.class);


    /**
     * 对象转换
     * @param bean 分页查询条件
     * @return
     */
     SystemDictTypeRespVO convert(SystemDictTypeEntity bean);

    /**
     * 转换list字段
     * @param list 请求list
     * @return
     */
    List<SystemDictTypeRespVO> convertList(List<SystemDictTypeEntity> list);

    /**
     * 分页查询数据
     * @param page 分页数据
     * @return
     */
    Page<SystemDictTypeRespVO,SystemDictTypePageReqVO> convertPage(Page<SystemDictTypeEntity,SystemDictTypePageReqVO> page);


}
