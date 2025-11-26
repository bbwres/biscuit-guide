package cn.bbwres.biscuit.module.basic.service;


import java.util.*;
import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.basic.controller.vo.*;
import cn.bbwres.biscuit.module.basic.entity.SystemDictTypeEntity;
import cn.bbwres.biscuit.module.basic.dao.SystemDictTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * <p>
 * 字典类型表 服务实现类
 * </p>
 *
 * @author zlf
 * @since 2025-11-26
 */
@RequiredArgsConstructor(onConstructor_={@Autowired})
@Slf4j
@Service
public class SystemDictTypeServiceImpl implements SystemDictTypeService {

    private final SystemDictTypeMapper systemDictTypeMapper;


    /**
     * 获得字典类型表
     *
     * @param id 编号
     * @return 字典类型表
     */
    @Override
    public SystemDictTypeEntity getSystemDictType(String id) {
        return systemDictTypeMapper.selectOneById(id);
    }

    /**
     * 获得字典类型表列表
     *
     * @param ids 编号
     * @return 字典类型表列表
     */
    @Override
    public List<SystemDictTypeEntity> getSystemDictTypeList(Collection<String> ids) {
        return systemDictTypeMapper.selectListByIds(ids);
    }

    /**
     * 获得字典类型表分页
     *
     * @param pageReqVO 分页查询
     * @return 字典类型表分页
     */
    @Override
    public  Page<SystemDictTypeEntity,SystemDictTypePageReqVO> getSystemDictTypePage(Page<SystemDictTypeEntity,SystemDictTypePageReqVO> pageReqVO) {
        return systemDictTypeMapper.selectPage(pageReqVO);
    }


}
