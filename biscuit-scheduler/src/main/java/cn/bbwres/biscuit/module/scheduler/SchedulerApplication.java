package cn.bbwres.biscuit.module.scheduler;

import cn.bbwres.biscuit.BootstrapProfile;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 定时任务服务
 *
 * @author zhanglinfeng
 */
@SpringBootApplication
public class SchedulerApplication {

    public static void main(String[] args) {
        BootstrapProfile.setBootstrapProfile();
        SpringApplication.run(SchedulerApplication.class, args);
    }

}
