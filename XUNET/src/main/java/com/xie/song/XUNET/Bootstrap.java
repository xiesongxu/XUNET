package com.xie.song.XUNET;

import com.xie.song.XUNET.annotation.Bean;
import com.xie.song.XUNET.annotation.Config;
import com.xie.song.XUNET.config.Configuration;
import com.xie.song.XUNET.exception.ExistException;
import com.xie.song.XUNET.service.StandardService;
import com.xie.song.XUNET.test.Test1;
import com.xie.song.XUNET.util.AnnotationUtil;
import com.xie.song.XUNET.util.ClassUtil;
import com.xie.song.XUNET.util.Parse;

import java.lang.annotation.Annotation;

public class Bootstrap {

    public static void start(Class configClass) {
        Boolean notEmpty = AnnotationUtil.isNotEmpty(configClass, Config.class);
        if(notEmpty){
            Parse parse = new Parse();
            Configuration configuration = null;
            try {
                 configuration = parse.parse(ClassUtil.createObjectByClass(configClass));
            } catch (ExistException e) {
                e.printStackTrace();
            }
            StandardService standardService = new StandardService();
            standardService.start(configuration);
            System.out.println("完成=======");
        }

    }

    public static void main(String[] args) {

    }

}
