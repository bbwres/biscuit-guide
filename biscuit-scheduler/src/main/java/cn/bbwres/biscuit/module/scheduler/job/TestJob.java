package cn.bbwres.biscuit.module.scheduler.job;

import cn.bbwres.biscuit.scheduler.annotation.JobDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * 测试的任务
 *
 * @author zhanglinfeng
 */
@Slf4j
@Service
@JobDefinition(jobName = "test", group = "test", targetMethod = "execute", cron = "0/3 * * * * ? *", description = "测试任务")
public class TestJob {

    /**
     * 执行方法
     */
    public void execute() {
        log.info("执行任务!!");
    }
}
