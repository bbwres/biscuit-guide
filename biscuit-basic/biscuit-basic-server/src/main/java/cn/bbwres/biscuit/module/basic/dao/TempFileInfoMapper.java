package cn.bbwres.biscuit.module.basic.dao;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.basic.controller.vo.TempFileInfoPageReqVO;
import cn.bbwres.biscuit.module.basic.entity.TempFileInfoEntity;
import cn.bbwres.biscuit.mybatis.mapper.BatchBaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;


/**
 * <p>
 * 临时文件表 Mapper 接口
 * </p>
 *
 * @author zlf
 * @since 2025-12-11
 */
@Mapper
public interface TempFileInfoMapper extends BatchBaseMapper<TempFileInfoEntity> {

    /**
     * 分页查询数据
     *
     * @param reqVO 分页查询条件
     * @return
     */
    default Page<TempFileInfoEntity, TempFileInfoPageReqVO> selectPage(Page<TempFileInfoEntity, TempFileInfoPageReqVO> reqVO) {
        LambdaQueryWrapper<TempFileInfoEntity> queryWrapper = Wrappers.lambdaQuery(TempFileInfoEntity.class);
        if (!ObjectUtils.isEmpty(reqVO.getQuery())) {
            queryWrapper.eq(!ObjectUtils.isEmpty(reqVO.getQuery().getId()),
                    TempFileInfoEntity::getId, reqVO.getQuery().getId());
        }

        // 大多数情况下，id 倒序
        queryWrapper.orderByDesc(TempFileInfoEntity::getId);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<TempFileInfoEntity> page = selectPage(PageDTO.of(reqVO.getCurrent(), reqVO.getSize()), queryWrapper);

        reqVO.setRecords(page.getRecords());
        reqVO.setTotal(page.getTotal());
        reqVO.calculationPages();
        return reqVO;
    }


    /**
     * 查询在指定时间也没有业务关联的临时文件信息
     *
     * @param gtData
     * @return
     */
    default List<TempFileInfoEntity> findByNoBusiness(LocalDateTime gtData) {
        return selectList(Wrappers.lambdaQuery(TempFileInfoEntity.class)
                .le(TempFileInfoEntity::getCreateTime, gtData));
    }
}

