package cn.bbwres.biscuit.module.auth.convert;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.auth.controller.vo.LoginAccountAddOrUpdateReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.LoginAccountPageReqVO;
import cn.bbwres.biscuit.module.auth.controller.vo.LoginAccountRespVO;
import cn.bbwres.biscuit.module.auth.entity.LoginAccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


/**
 * <p>
 * 登陆账户表 Convert
 * </p>
 *
 * @author zlf
 * @Date 2025-08-19
 */
@Mapper
public interface LoginAccountConvert {

    /**
     * 转换对象
     */
    LoginAccountConvert INSTANCE = Mappers.getMapper(LoginAccountConvert.class);


    /**
     * 对象转换
     *
     * @param bean 分页查询条件
     * @return
     */
    LoginAccountRespVO convert(LoginAccountEntity bean);

    /**
     * 转换list字段
     *
     * @param list 请求list
     * @return
     */
    List<LoginAccountRespVO> convertList(List<LoginAccountEntity> list);

    /**
     * 分页查询数据
     *
     * @param page 分页数据
     * @return
     */
    Page<LoginAccountRespVO, LoginAccountPageReqVO> convertPage(Page<LoginAccountEntity, LoginAccountPageReqVO> page);


    /**
     * 转换请求对象
     *
     * @param loginAccountAddOrUpdateReq
     * @return
     */
    LoginAccountEntity convertByAddReq(LoginAccountAddOrUpdateReqVO loginAccountAddOrUpdateReq);

}
