package com.asiainfo.util.logs;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.util.SystemPropertyUtils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;

/**
 * <p>
 * Title:LogbackConfigurer
 * </p>
 * <p>
 * Description:logback日志加载类
 * </p>
 * <p>
 * Company
 * </p>
 * 
 * @author
 * @date 2016年5月20日下午2:30:03
 */
public class LogbackConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(LogbackConfigurer.class);

    /**
     * 
     * <p>
     * Description:配置初始化日志文件
     * </p>
     * 
     * @author
     * @date
     * @param locations
     */
    public static void initLogback(String locations) {
        try {
            String resolvedLocation = SystemPropertyUtils.resolvePlaceholders(locations);
            URL url = ResourceUtils.getURL(resolvedLocation);
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            loggerContext.reset();
            JoranConfigurator joranConfigurator = new JoranConfigurator();
            joranConfigurator.setContext(loggerContext);
            joranConfigurator.doConfigure(url);
            logger.debug("loaded slf4j configure file from {}", url);
        } catch (Exception e) {
            logger.error("can loading slf4j configure file from {}" + locations, e);
        }
    }

}