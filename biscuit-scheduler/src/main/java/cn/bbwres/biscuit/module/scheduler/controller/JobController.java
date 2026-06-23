package cn.bbwres.biscuit.module.scheduler.controller;

import cn.bbwres.biscuit.dto.Result;
import cn.bbwres.biscuit.entity.UserBaseInfo;
import cn.bbwres.biscuit.module.scheduler.constants.SchedulerErrorCodeConstants;
import cn.bbwres.biscuit.module.scheduler.controller.vo.JobDetailRespVO;
import cn.bbwres.biscuit.module.scheduler.controller.vo.TriggerBatchReqVO;
import cn.bbwres.biscuit.module.scheduler.controller.vo.TriggerCronUpdateReqVO;
import cn.bbwres.biscuit.module.scheduler.controller.vo.TriggerRespVO;
import cn.bbwres.biscuit.module.scheduler.convert.JobConvert;
import cn.bbwres.biscuit.scheduler.dto.TriggerInfo;
import cn.bbwres.biscuit.scheduler.service.JobManagerService;
import cn.bbwres.biscuit.web.utils.WebFrameworkUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 定时任务管理 controller
 * </p>
 *
 * @author zhanglinfeng
 * @Date 2026-06-23
 */
@Slf4j
@Tag(name = "定时任务管理")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Validated
@RestController
@RequestMapping("/job")
public class JobController {

    private final JobManagerService jobManagerService;

    /**
     * 获取所有定时任务
     *
     * @return Result
     */
    @GetMapping("/list")
    @Operation(summary = "获取所有定时任务列表")
    public Result<List<TriggerRespVO>> list() {
        List<TriggerInfo> triggers = jobManagerService.queryAllTrigger();
        if (triggers == null) {
            return Result.success(Collections.emptyList());
        }
        return Result.success(JobConvert.INSTANCE.convertList(triggers));
    }

    /**
     * 获取指定任务的明细
     *
     * @param jobName  任务名称
     * @param jobGroup 任务分组
     * @return Result
     */
    @GetMapping("/getJobDetail")
    @Operation(summary = "获取指定任务明细", parameters = {
            @Parameter(name = "jobName", description = "任务名称", required = true),
            @Parameter(name = "jobGroup", description = "任务分组", required = true)
    })
    public Result<JobDetailRespVO> getJobDetail(@RequestParam("jobName") String jobName,
                                                @RequestParam("jobGroup") String jobGroup) {
        JobDetail jobDetail = jobManagerService.queryJobDetail(jobName, jobGroup);
        if (jobDetail == null) {
            return Result.error(SchedulerErrorCodeConstants.JOB_NO_EXISTS_ERROR);
        }
        return Result.success(JobConvert.INSTANCE.convert(jobDetail));
    }

    /**
     * 暂停任务
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     * @return Result
     */
    @PostMapping("/pause")
    @Operation(summary = "暂停任务", parameters = {
            @Parameter(name = "triggerName", description = "触发器名称", required = true),
            @Parameter(name = "triggerGroup", description = "触发器分组", required = true)
    })
    public Result<Void> pause(@RequestParam("triggerName") String triggerName,
                              @RequestParam("triggerGroup") String triggerGroup) {
        log.info("当前用户:[{}]暂停定时任务,触发器:[{}:{}]",
                WebFrameworkUtils.getUserInfo(UserBaseInfo::getUsername), triggerName, triggerGroup);
        if (Boolean.FALSE.equals(jobManagerService.pausedTrigger(triggerName, triggerGroup))) {
            return Result.error(SchedulerErrorCodeConstants.JOB_OPERATE_ERROR);
        }
        return Result.success(null);
    }

    /**
     * 批量暂停任务
     *
     * @param reqVO 批量暂停请求参数
     * @return Result
     */
    @PostMapping("/pauseBatch")
    @Operation(summary = "批量暂停任务")
    public Result<Void> pauseBatch(@Validated @RequestBody TriggerBatchReqVO reqVO) {
        log.info("当前用户:[{}]批量暂停定时任务:[{}]",
                WebFrameworkUtils.getUserInfo(UserBaseInfo::getUsername), reqVO.getTriggers());
        List<TriggerInfo> triggerKeys = JobConvert.INSTANCE.convertTriggerList(reqVO.getTriggers());
        if (Boolean.FALSE.equals(jobManagerService.pausedTriggers(triggerKeys))) {
            return Result.error(SchedulerErrorCodeConstants.JOB_OPERATE_ERROR);
        }
        return Result.success(null);
    }

    /**
     * 恢复任务
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     * @return Result
     */
    @PostMapping("/resume")
    @Operation(summary = "恢复任务", parameters = {
            @Parameter(name = "triggerName", description = "触发器名称", required = true),
            @Parameter(name = "triggerGroup", description = "触发器分组", required = true)
    })
    public Result<Void> resume(@RequestParam("triggerName") String triggerName,
                               @RequestParam("triggerGroup") String triggerGroup) {
        log.info("当前用户:[{}]恢复定时任务,触发器:[{}:{}]",
                WebFrameworkUtils.getUserInfo(UserBaseInfo::getUsername), triggerName, triggerGroup);
        if (Boolean.FALSE.equals(jobManagerService.resumeTrigger(triggerName, triggerGroup))) {
            return Result.error(SchedulerErrorCodeConstants.JOB_OPERATE_ERROR);
        }
        return Result.success(null);
    }

    /**
     * 批量恢复任务
     *
     * @param reqVO 批量恢复请求参数
     * @return Result
     */
    @PostMapping("/resumeBatch")
    @Operation(summary = "批量恢复任务")
    public Result<Void> resumeBatch(@Validated @RequestBody TriggerBatchReqVO reqVO) {
        log.info("当前用户:[{}]批量恢复定时任务:[{}]",
                WebFrameworkUtils.getUserInfo(UserBaseInfo::getUsername), reqVO.getTriggers());
        List<TriggerInfo> triggerKeys = JobConvert.INSTANCE.convertTriggerList(reqVO.getTriggers());
        if (Boolean.FALSE.equals(jobManagerService.resumeTriggers(triggerKeys))) {
            return Result.error(SchedulerErrorCodeConstants.JOB_OPERATE_ERROR);
        }
        return Result.success(null);
    }

    /**
     * 修改定时任务 cron 表达式
     *
     * @param reqVO 修改请求参数
     * @return Result
     */
    @PostMapping("/updateCron")
    @Operation(summary = "修改定时任务 cron 表达式")
    public Result<Void> updateCron(@Validated @RequestBody TriggerCronUpdateReqVO reqVO) {
        log.info("当前用户:[{}]修改定时任务 cron 表达式:[{}]",
                WebFrameworkUtils.getUserInfo(UserBaseInfo::getUsername), reqVO);
        if (Boolean.FALSE.equals(jobManagerService.updateTriggerCron(
                reqVO.getTriggerName(), reqVO.getTriggerGroup(), reqVO.getCron()))) {
            return Result.error(SchedulerErrorCodeConstants.JOB_OPERATE_ERROR);
        }
        return Result.success(null);
    }

    /**
     * 立即执行任务
     *
     * @param jobName  任务名称
     * @param jobGroup 任务分组
     * @return Result
     */
    @PostMapping("/triggerJob")
    @Operation(summary = "立即执行任务", parameters = {
            @Parameter(name = "jobName", description = "任务名称", required = true),
            @Parameter(name = "jobGroup", description = "任务分组", required = true)
    })
    public Result<Void> triggerJob(@RequestParam("jobName") String jobName,
                                   @RequestParam("jobGroup") String jobGroup) {
        log.info("当前用户:[{}]立即执行定时任务,任务:[{}:{}]",
                WebFrameworkUtils.getUserInfo(UserBaseInfo::getUsername), jobName, jobGroup);
        if (Boolean.FALSE.equals(jobManagerService.triggerJob(jobName, jobGroup))) {
            return Result.error(SchedulerErrorCodeConstants.JOB_OPERATE_ERROR);
        }
        return Result.success(null);
    }

    /**
     * 立即执行触发器对应任务
     *
     * @param triggerName  触发器名称
     * @param triggerGroup 触发器分组
     * @return Result
     */
    @PostMapping("/trigger")
    @Operation(summary = "立即执行触发器对应任务", parameters = {
            @Parameter(name = "triggerName", description = "触发器名称", required = true),
            @Parameter(name = "triggerGroup", description = "触发器分组", required = true)
    })
    public Result<Void> trigger(@RequestParam("triggerName") String triggerName,
                                @RequestParam("triggerGroup") String triggerGroup) {
        log.info("当前用户:[{}]立即执行触发器对应任务,触发器:[{}:{}]",
                WebFrameworkUtils.getUserInfo(UserBaseInfo::getUsername), triggerName, triggerGroup);
        if (Boolean.FALSE.equals(jobManagerService.triggerJobByTrigger(triggerName, triggerGroup))) {
            return Result.error(SchedulerErrorCodeConstants.JOB_OPERATE_ERROR);
        }
        return Result.success(null);
    }

}
