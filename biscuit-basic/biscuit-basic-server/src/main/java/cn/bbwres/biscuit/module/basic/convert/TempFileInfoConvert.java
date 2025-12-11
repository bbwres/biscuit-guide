package cn.bbwres.biscuit.module.basic.convert;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.basic.controller.vo.TempFileInfoPageReqVO;
import cn.bbwres.biscuit.module.basic.controller.vo.TempFileInfoRespVO;
import cn.bbwres.biscuit.module.basic.entity.TempFileInfoEntity;
import cn.bbwres.biscuit.web.file.entity.TempFileInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


/**
 * <p>
 * 临时文件表 Convert
 * </p>
 *
 * @author zlf
 * @since 2025-12-11
 */
@Mapper
public interface TempFileInfoConvert {

    /**
     * 转换对象
     */
    TempFileInfoConvert INSTANCE = Mappers.getMapper(TempFileInfoConvert.class);


    /**
     * 对象转换
     *
     * @param bean 分页查询条件
     * @return
     */
    TempFileInfoRespVO convert(TempFileInfoEntity bean);

    /**
     * 转换list字段
     *
     * @param list 请求list
     * @return
     */
    List<TempFileInfoRespVO> convertList(List<TempFileInfoEntity> list);

    /**
     * 分页查询数据
     *
     * @param page 分页数据
     * @return
     */
    Page<TempFileInfoRespVO, TempFileInfoPageReqVO> convertPage(Page<TempFileInfoEntity, TempFileInfoPageReqVO> page);


    /**
     * 转换临时文件信息存储对象
     *
     * @param fileInfo
     * @return
     */
    TempFileInfoEntity convertByInfo(TempFileInfo fileInfo);

    /**
     * 对象转文件信息
     *
     * @param fileInfo
     * @return
     */
    List<TempFileInfo> convert2Info(List<TempFileInfoEntity> fileInfo);

}
