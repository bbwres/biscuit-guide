package cn.bbwres.biscuit.module.scheduler.convert;

import cn.bbwres.biscuit.module.scheduler.controller.vo.JobDetailRespVO;
import cn.bbwres.biscuit.module.scheduler.controller.vo.TriggerKeyReqVO;
import cn.bbwres.biscuit.module.scheduler.controller.vo.TriggerRespVO;
import cn.bbwres.biscuit.scheduler.dto.TriggerInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.quartz.JobDetail;

import java.util.List;

/**
 * 定时任务 Convert
 *
 * @author zhanglinfeng
 */
@Mapper
public interface JobConvert {

    /**
     * 转换对象
     */
    JobConvert INSTANCE = Mappers.getMapper(JobConvert.class);

    /**
     * 触发器信息转换
     *
     * @param triggerInfo 触发器信息
     * @return 响应 VO
     */
    TriggerRespVO convert(TriggerInfo triggerInfo);

    /**
     * 触发器信息列表转换
     *
     * @param list 触发器信息列表
     * @return 响应 VO 列表
     */
    List<TriggerRespVO> convertList(List<TriggerInfo> list);

    /**
     * 触发器标识转换为触发器信息（用于批量操作）
     *
     * @param reqVO 触发器标识
     * @return 触发器信息
     */
    @Mapping(source = "triggerName", target = "name")
    @Mapping(source = "triggerGroup", target = "group")
    TriggerInfo convert(TriggerKeyReqVO reqVO);

    /**
     * 触发器标识列表转换为触发器信息列表（用于批量操作）
     *
     * @param list 触发器标识列表
     * @return 触发器信息列表
     */
    List<TriggerInfo> convertTriggerList(List<TriggerKeyReqVO> list);

    /**
     * 任务明细转换（手动处理 JobKey 嵌套字段）
     *
     * @param jobDetail 任务明细
     * @return 响应 VO
     */
    default JobDetailRespVO convert(JobDetail jobDetail) {
        if (jobDetail == null) {
            return null;
        }
        JobDetailRespVO respVO = new JobDetailRespVO();
        if (jobDetail.getKey() != null) {
            respVO.setJobName(jobDetail.getKey().getName());
            respVO.setJobGroup(jobDetail.getKey().getGroup());
        }
        respVO.setDescription(jobDetail.getDescription());
        respVO.setJobClassName(jobDetail.getJobClass() != null ? jobDetail.getJobClass().getName() : null);
        respVO.setDurable(jobDetail.isDurable());
        respVO.setRequestsRecovery(jobDetail.requestsRecovery());
        if (jobDetail.getJobDataMap() != null) {
            respVO.setJobDataMap(jobDetail.getJobDataMap().getWrappedMap());
        }
        return respVO;
    }

}
