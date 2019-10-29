package com.eci.security.auth.util;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/10/24.
 */
@Slf4j
public class JsonUtil {
    private final static ObjectMapper MAPPER = new ObjectMapper();

    public static  <T> T jsonToObject(String json, Class<T> clazz)  {
        try {
            T returnObject = MAPPER.readValue(json, clazz);
            return returnObject;
        } catch (IOException e) {
            log.error("jsonValue: {} transform to {} error", json, clazz.getCanonicalName());
            return null;
        }
    }

    public  static <T> String ObjectToJson(T object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (IOException e) {
            log.error("{} transform to json error", object.getClass().getCanonicalName());
            return null;
        }
    }



}
