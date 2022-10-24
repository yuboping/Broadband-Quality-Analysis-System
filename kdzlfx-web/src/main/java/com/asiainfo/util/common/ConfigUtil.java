package com.asiainfo.util.common;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class ConfigUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigUtil.class);

    private static final String CONF_PATH = "config/config.properties";

    /**
     * 
     * <p>
     * Description:获取属性值
     * </p>
     * 
     * @author
     * @date
     * @param key
     * @return
     */
    public static String getPropertyKey(String key) {
        Properties st = getConxtions();
        return st.getProperty(key);
    }

    /**
     * 
     * <p>
     * Description:加载属性文件
     * </p>
     * 
     * @author
     * @date
     * @return
     */
    public static Properties getConxtions() {
        Properties properties = null;
        try {
            properties = PropertiesLoaderUtils.loadProperties(new ClassPathResource(CONF_PATH));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return properties;
    }
}
