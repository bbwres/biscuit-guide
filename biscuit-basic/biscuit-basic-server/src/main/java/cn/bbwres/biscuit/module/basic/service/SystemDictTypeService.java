package cn.bbwres.biscuit.module.basic.service;

import java.util.*;
import cn.bbwres.biscuit.module.basic.controller.vo.*;
import cn.bbwres.biscuit.module.basic.entity.SystemDictTypeEntity;
import cn.bbwres.biscuit.dto.Page;





/**
 * <p>
 * 字典类型表 服务类
 * </p>
 *
 * @author zlf
 * @since 2025-11-26
 */
public interface SystemDictTypeService {

    /**
     * 获得字典类型表
     *
     * @param id 编号
     * @return 字典类型表
     */
    SystemDictTypeEntity getSystemDictType(String id);

    /**
     * 获得字典类型表列表
     *
     * @param ids 编号
     * @return 字典类型表列表
     */
    List<SystemDictTypeEntity> getSystemDictTypeList(Collection<String> ids);

    /**
     * 获得字典类型表分页
     *
     * @param pageReqVO 分页查询
     * @return 字典类型表分页
     */
    Page<SystemDictTypeEntity,SystemDictTypePageReqVO> getSystemDictTypePage(Page<SystemDictTypeEntity,SystemDictTypePageReqVO> pageReqVO);


}
