package org.sigmoid.configclient;

import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({ConfigClientService.class})
@EnableConfigurationProperties(ConfigClientProperties.class)
public class ConfigClientAutoConfiguration {

    @Autowired
    private ConfigClientProperties configClientProperties;

    @Bean
    @ConditionalOnMissingBean(ConfigClientService.class)
    public ConfigClientService configClientService() throws NacosException {
        ConfigClientService configClientService = new ConfigClientService();
        configClientService.setHost(configClientProperties.getHosts());
        configClientService.setTimeout(configClientProperties.getTimeout());
        configClientService.connect();
        return configClientService;
    }

}
