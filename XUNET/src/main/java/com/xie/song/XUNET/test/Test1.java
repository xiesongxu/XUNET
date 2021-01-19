package com.xie.song.XUNET.test;

import com.xie.song.XUNET.annotation.Config;
import com.xie.song.XUNET.config.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Properties;

@Config
public class Test1 {
    public static void main(String[] args) throws NoSuchMethodException, IOException {
//        String simpleName = "BBBB";
//        String beanName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
//        System.out.println(beanName);
//        Constructor<Test1> constructor = Test1.class.getConstructor(null);
//        System.out.println(constructor);
//        String DEFAULT_CONFIG_FILE = "com/xie/song/XUNET/config/configuration.properties";
//
//        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("configuration.properties");
//        Properties properties = new Properties();
//        properties.load(inputStream);
//        Object o = properties.get("xie");
//        System.out.println(o);

//        String path = Test1.class.getResource("").getPath();
//        String path1 = Test1.class.getResource("/").getPath();
//        String path2 = Test1.class.getClassLoader().getResource("/").getPath();
//        System.out.println(path);
//        System.out.println(path1);
//        System.out.println(path2);

        String a = "xie,song,xu";
        String[] split = a.split(",");
        System.out.println(split[0]+":"+split[1]);
        System.out.println(split[2]);
    }
}
