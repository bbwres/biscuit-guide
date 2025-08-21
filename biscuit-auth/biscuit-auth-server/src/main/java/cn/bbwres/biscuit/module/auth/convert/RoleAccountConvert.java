package cn.bbwres.biscuit.module.auth.convert;

import java.util.*;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import cn.bbwres.biscuit.module.auth.controller.vo.*;
import cn.bbwres.biscuit.module.auth.entity.RoleAccountEntity;
import cn.bbwres.biscuit.dto.Page;



/**
 * <p>
 * 用户角色表 Convert
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Mapper
public interface RoleAccountConvert {

    /**
     * 转换对象
     */
     RoleAccountConvert INSTANCE = Mappers.getMapper(RoleAccountConvert.class);


    /**
     * 对象转换
     * @param bean 分页查询条件
     * @return
     */
     RoleAccountRespVO convert(RoleAccountEntity bean);

    /**
     * 转换list字段
     * @param list 请求list
     * @return
     */
    List<RoleAccountRespVO> convertList(List<RoleAccountEntity> list);

    /**
     * 分页查询数据
     * @param page 分页数据
     * @return
     */
    Page<RoleAccountRespVO,RoleAccountPageReqVO> convertPage(Page<RoleAccountEntity,RoleAccountPageReqVO> page);


}
