package org.sigmoid.configclient;

import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.sigmoid.configclient.annotations.ConfAs;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;

@Data
@Slf4j
public class ConfigClientService {

    private String host;

    private int timeout;

    private ConfigService configService = null;

    private static ConcurrentMap<String, ConfigItem> configs = Maps.newConcurrentMap();

    private static String generateKey(String dataId, String group) {
        return String.format("%s_%s", dataId, group);
    }

    public void connect() throws NacosException {
        this.configService = ConfigFactory.createConfigService(host);
    }

    private void updateField(String value, ConfigItem configItem) {

        Object bean = configItem.getBean();
        Class clazz = configItem.getClazz();
        ConfAs confAs = configItem.getConfAs();
        Field field = configItem.getField();

        try {
            if (confAs.equals(ConfAs.NORMAL)) {
                if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
                    field.setInt(bean,
                            Optional.ofNullable(Integer.parseInt(value)).orElse(Integer.MIN_VALUE).intValue());
                } else if (clazz.equals(long.class) || clazz.equals(Long.class)) {
                    field.setLong(bean,
                            Optional.ofNullable(Long.parseLong(value)).orElse(Long.MIN_VALUE).longValue());
                } else if (clazz.equals(short.class) || clazz.equals(Short.class)) {
                    field.setShort(bean,
                            Optional.ofNullable(Short.parseShort(value)).orElse(Short.MIN_VALUE).shortValue());
                } else if (clazz.equals(byte.class) || clazz.equals(Byte.class)) {
                    field.setByte(bean,
                            Optional.ofNullable(Byte.parseByte(value)).orElse(Byte.MIN_VALUE).byteValue());
                } else if (clazz.equals(boolean.class) || clazz.equals(Boolean.class)) {
                    field.setBoolean(bean,
                            Optional.ofNullable(Boolean.parseBoolean(value)).orElse(Boolean.FALSE).booleanValue());
                } else if (clazz.equals(float.class) || clazz.equals(Float.class)) {
                    field.setFloat(bean,
                            Optional.ofNullable(Float.parseFloat(value)).orElse(0.0f).floatValue());
                } else if (clazz.equals(double.class) || clazz.equals(Double.class)) {
                    field.setDouble(bean,
                            Optional.ofNullable(Double.parseDouble(value)).orElse(0.0).doubleValue());
                } else if (clazz.equals(String.class)) {
                    field.set(bean, Optional.ofNullable(value).orElse(StringUtils.EMPTY));
                }
            } else if (confAs.equals(ConfAs.JSON)) {
                field.set(bean, Serializer.jsonToBean(value, clazz));
            } else if (confAs.equals(ConfAs.MAP)) {
                field.set(bean, Serializer.jsonToMap(value));
            } else if (confAs.equals(ConfAs.LIST)) {
                field.set(bean, Serializer.jsonToBeans(value, String.class));
            }
        } catch (Exception e) {
            log.error("Refresh local config value fail dataId [%s] group [%s] bean [%s] field [%s]",
                    configItem.getDataId(), configItem.getGroup(), bean.getClass().getName(), field.getName());
        }

    }

    public void init() throws NacosException {
        for (Map.Entry<String, ConfigItem> entry : configs.entrySet()){
            ConfigItem configItem = entry.getValue();
            String group = configItem.getGroup();
            String dataId = configItem.getDataId();
            Optional.ofNullable(this.configService.getConfig(dataId, group, timeout))
                    .ifPresent(v -> updateField(v, configItem));

            this.configService.addListener(dataId, group, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String s) {
                    log.info("Receive config push dataId [{}] group [{}] value [{}]", dataId, group, s);
                    updateField(s, configItem);
                }
            });
        }
    }

    public static void addListen(String dataId, String group, Field field, Object bean, Class clazz, ConfAs confAs) throws NacosException {
        field.setAccessible(true);
        configs.put(generateKey(dataId, group),
                ConfigItem.builder().field(field).bean(bean).clazz(clazz).confAs(confAs).build());
    }

    @Getter
    @Builder
    static class ConfigItem {
        private String dataId;
        private String group;
        private Field field;
        private Object bean;
        private Class clazz;
        private ConfAs confAs;
    }

}
