package cn.bbwres.biscuit.module.basic.dao;
import cn.bbwres.biscuit.module.basic.entity.SystemDictTypeEntity;
import com.mybatisflex.core.BaseMapper;
import cn.bbwres.biscuit.dto.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.util.ObjectUtils;
import cn.bbwres.biscuit.module.basic.controller.vo .*;
import com.mybatisflex.core.query.QueryWrapper;

import static cn.bbwres.biscuit.module.basic.entity.table.SystemDictTypeEntityTableDef.SYSTEM_DICT_TYPE_ENTITY;

/**
 * <p>
 * 字典类型表 Mapper 接口
 * </p>
 *
 * @author zlf
 * @since 2025-11-26
 */
@Mapper
public interface SystemDictTypeMapper extends BaseMapper<SystemDictTypeEntity> {

        /**
    * 分页查询数据
    * @param reqVO 分页查询条件
    * @return
    */
    default Page<SystemDictTypeEntity,SystemDictTypePageReqVO> selectPage(Page<SystemDictTypeEntity,SystemDictTypePageReqVO> reqVO){
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (!ObjectUtils.isEmpty(reqVO.getQuery())) {
            queryWrapper.where(SYSTEM_DICT_TYPE_ENTITY.ID.eq(reqVO.getQuery().getId()));
        }

        // 大多数情况下，id 倒序
        queryWrapper.orderBy(SYSTEM_DICT_TYPE_ENTITY.ID, false);

        com.mybatisflex.core.paginate.Page<SystemDictTypeEntity> page = paginate(reqVO.getCurrent(), reqVO.getSize(), queryWrapper);
        reqVO.setRecords(page.getRecords());
        reqVO.setTotal(page.getTotalRow());
        reqVO.calculationPages();
        return reqVO;
    }

}

