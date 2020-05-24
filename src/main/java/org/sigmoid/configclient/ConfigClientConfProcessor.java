package org.sigmoid.configclient;

import com.alibaba.nacos.api.exception.NacosException;
import com.google.common.collect.Sets;
import org.sigmoid.configclient.annotations.Conf;
import org.sigmoid.configclient.annotations.ConfAs;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigClientConfProcessor implements BeanPostProcessor {

    private static final Set<Class> clazzSet = Sets.newHashSet(
            int.class, Integer.class,
            short.class, Short.class,
            byte.class, Byte.class,
            long.class, Long.class,
            boolean.class, Boolean.class,
            float.class, Float.class,
            double.class, Double.class,
            String.class
            );

    private void processAnnotationField(Object bean, Field field) throws IllegalAccessException, NacosException {
        Conf annotation = field.getAnnotation(Conf.class);
        if (annotation == null) {
            return;
        }
        String dataId = annotation.dataId();
        String group = annotation.group();
        ConfAs confAs = annotation.as();

        Object o = field.get(bean);

        //validate
        if (confAs.equals(ConfAs.LIST) && !(o instanceof List)) {
            return;
        } else if (confAs.equals(ConfAs.MAP) && !(o instanceof Map)) {
            return;
        } else if (confAs.equals(ConfAs.NORMAL) && !(clazzSet.contains(field.getType()))) {
            return;
        }

        ConfigClientService.addListen(dataId, group, field, bean, field.getClass(), confAs);

    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field declareField : declaredFields) {
            try {
                processAnnotationField(bean, declareField);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Register @Conf field error", e);
            } catch (NacosException e) {
                throw new RuntimeException("Register @Conf field error", e);
            }
        }
        return bean;

    }

}
