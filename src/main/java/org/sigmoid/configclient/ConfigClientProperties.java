package org.sigmoid.configclient;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "config.client")
public class ConfigClientProperties {

    public static final String DEFAULT_HOSTS = "localhost:8848";

    private String hosts = DEFAULT_HOSTS;

    private static final int DEFAULT_TIMEOUT = 3000;

    private int timeout = DEFAULT_TIMEOUT;

}
