package org.sigmoid.configclient;

import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

public class ConfigClientInitProcessor implements BeanPostProcessor, Ordered {

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (!bean.getClass().equals(ConfigClientService.class)) {
            return bean;
        }
        ConfigClientService configClientService = (ConfigClientService) bean;
        try {
            configClientService.init();
        } catch (NacosException e) {
            throw new RuntimeException("Init configClientService error", e);
        }
        return bean;

    }
}
