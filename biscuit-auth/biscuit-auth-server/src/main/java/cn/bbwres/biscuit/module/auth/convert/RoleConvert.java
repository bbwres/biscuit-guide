package cn.bbwres.biscuit.module.auth.convert;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.auth.controller.vo.RoleAddReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.RolePageReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.RoleRespVO;
import cn.bbwres.biscuit.module.auth.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


/**
 * <p>
 * 角色表 Convert
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Mapper
public interface RoleConvert {

    /**
     * 转换对象
     */
    RoleConvert INSTANCE = Mappers.getMapper(RoleConvert.class);


    /**
     * 对象转换
     *
     * @param bean 分页查询条件
     * @return
     */
    RoleRespVO convert(RoleEntity bean);

    /**
     * 转换list字段
     *
     * @param list 请求list
     * @return
     */
    List<RoleRespVO> convertList(List<RoleEntity> list);

    /**
     * 分页查询数据
     *
     * @param page 分页数据
     * @return
     */
    Page<RoleRespVO, RolePageReqVO> convertPage(Page<RoleEntity, RolePageReqVO> page);


    /**
     * 转换为对象
     *
     * @param req
     * @return
     */
    RoleEntity convertByAddReq(RoleAddReqVO req);

}
