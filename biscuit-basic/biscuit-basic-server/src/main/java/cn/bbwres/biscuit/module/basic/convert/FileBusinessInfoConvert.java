package cn.bbwres.biscuit.module.basic.convert;

import java.util.*;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import cn.bbwres.biscuit.module.basic.controller.vo.*;
import cn.bbwres.biscuit.module.basic.entity.FileBusinessInfoEntity;
import cn.bbwres.biscuit.dto.Page;



/**
 * <p>
 * 业务配置表 Convert
 * </p>
 *
 * @author zlf
 * @since 2025-12-11
 */
@Mapper
public interface FileBusinessInfoConvert {

    /**
     * 转换对象
     */
     FileBusinessInfoConvert INSTANCE = Mappers.getMapper(FileBusinessInfoConvert.class);


    /**
     * 对象转换
     * @param bean 分页查询条件
     * @return
     */
     FileBusinessInfoRespVO convert(FileBusinessInfoEntity bean);

    /**
     * 转换list字段
     * @param list 请求list
     * @return
     */
    List<FileBusinessInfoRespVO> convertList(List<FileBusinessInfoEntity> list);

    /**
     * 分页查询数据
     * @param page 分页数据
     * @return
     */
    Page<FileBusinessInfoRespVO,FileBusinessInfoPageReqVO> convertPage(Page<FileBusinessInfoEntity,FileBusinessInfoPageReqVO> page);


}
