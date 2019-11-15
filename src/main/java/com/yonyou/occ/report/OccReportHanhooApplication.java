package com.yonyou.occ.report;

import com.nepxion.aquarius.lock.annotation.EnableLock;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@EnableLock
@EnableCaching
@EnableFeignClients
@EnableHystrix
@EnableHystrixDashboard
@EnableDiscoveryClient
//@ComponentScan(nameGenerator = DefaultBeanNameGenerator.class, basePackages = {"com.yonyou.ocm.*.utils"})
@SpringBootApplication(
        scanBasePackages = {"com.yonyou.ocm.common", "com.yonyou.occ.report", "com.yonyou.occ.b2b", "com.yonyou.iuap.bpm", "com.yonyou.iuap.message"}

//		,exclude = {com.yonyou.occ.prom.client.PrivilegeClient.class}
//		scanBasePackages = {"com.yonyou.ocm","com.yonyou.occ"}
)
//@ComponentScan(
//
//)
//@SpringBootApplication(
//		scanBasePackages = {"com.yonyou.ocm", "com.yonyou.iuap.bpm",
//				"com.yonyou.iuap.message","com.yonyou.occ.report",
//				"com.yonyou.occ.*.entity","com.yonyou.occ.*.dto","com.yonyou.occ.*.service.dto"
//		}
//)
public class OccReportHanhooApplication {
    public static ApplicationContext ac;

    public static void main(String[] args) {
        ac = SpringApplication.run(OccReportHanhooApplication.class, args);
    }

}
