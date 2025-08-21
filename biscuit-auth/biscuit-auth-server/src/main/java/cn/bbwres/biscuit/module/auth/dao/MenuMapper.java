package cn.bbwres.biscuit.module.auth.dao;
import cn.bbwres.biscuit.module.auth.entity.MenuEntity;
import cn.bbwres.biscuit.mybatis.mapper.BatchBaseMapper;
import cn.bbwres.biscuit.dto.Page;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.util.ObjectUtils;
import cn.bbwres.biscuit.module.auth.controller.vo .*;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;


/**
 * <p>
 * 菜单权限表 Mapper 接口
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Mapper
public interface MenuMapper extends BatchBaseMapper<MenuEntity> {

        /**
    * 分页查询数据
    * @param reqVO 分页查询条件
    * @return
    */
    default Page<MenuEntity,MenuPageReqVO> selectPage(Page<MenuEntity,MenuPageReqVO> reqVO){
        LambdaQueryWrapper<MenuEntity> queryWrapper = Wrappers.lambdaQuery(MenuEntity.class);
        if (!ObjectUtils.isEmpty(reqVO.getQuery())) {
            queryWrapper.eq(!ObjectUtils.isEmpty(reqVO.getQuery().getId()),
                MenuEntity::getId, reqVO.getQuery().getId());
        }

        // 大多数情况下，id 倒序
        queryWrapper.orderByDesc(MenuEntity::getId);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<MenuEntity> page = selectPage(PageDTO.of(reqVO.getCurrent(), reqVO.getSize()),queryWrapper);

        reqVO.setRecords(page.getRecords());
        reqVO.setTotal(page.getTotal());
        reqVO.calculationPages();
        return reqVO;
    }

}

