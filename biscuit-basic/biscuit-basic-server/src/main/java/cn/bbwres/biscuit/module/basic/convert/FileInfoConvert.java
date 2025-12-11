package cn.bbwres.biscuit.module.basic.convert;

import cn.bbwres.biscuit.dto.Page;
import cn.bbwres.biscuit.module.basic.api.vo.FileInfoResultVO;
import cn.bbwres.biscuit.module.basic.controller.vo.FileInfoPageReqVO;
import cn.bbwres.biscuit.module.basic.controller.vo.FileInfoRespVO;
import cn.bbwres.biscuit.module.basic.entity.FileInfoEntity;
import cn.bbwres.biscuit.web.file.entity.FileInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


/**
 * <p>
 * 业务文件信息表 Convert
 * </p>
 *
 * @author zlf
 * @since 2025-12-11
 */
@Mapper
public interface FileInfoConvert {

    /**
     * 转换对象
     */
    FileInfoConvert INSTANCE = Mappers.getMapper(FileInfoConvert.class);


    /**
     * 对象转换
     *
     * @param bean 分页查询条件
     * @return
     */
    FileInfoRespVO convert(FileInfoEntity bean);

    /**
     * 转换list字段
     *
     * @param list 请求list
     * @return
     */
    List<FileInfoRespVO> convertList(List<FileInfoEntity> list);

    /**
     * 分页查询数据
     *
     * @param page 分页数据
     * @return
     */
    Page<FileInfoRespVO, FileInfoPageReqVO> convertPage(Page<FileInfoEntity, FileInfoPageReqVO> page);


    /**
     * 实体对象转换为info对象
     *
     * @param fileInfoEntities
     * @return
     */
    List<FileInfo> convertList2Info(List<FileInfoEntity> fileInfoEntities);

    /**
     * 单个数据对象转换
     *
     * @param fileInfo
     * @return
     */
    FileInfo convert2Info(FileInfoEntity fileInfo);

    /**
     * info信息转换为实体对象信息
     *
     * @param fileInfos
     * @return
     */
    List<FileInfoEntity> convertInfoList2Entity(List<FileInfo> fileInfos);

    /**
     * info 信息转换为接口响应参数
     * @param fileInfoByBusiness
     * @return
     */
    List<FileInfoResultVO> convertInfo2FileInfoResult(List<FileInfo> fileInfoByBusiness);

}
