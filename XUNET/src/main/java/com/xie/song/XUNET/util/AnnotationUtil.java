package com.xie.song.XUNET.util;

import java.lang.annotation.Annotation;

public class AnnotationUtil {

    public static Boolean isNotEmpty(Class targetClass, Class targetAnnotationClass) {
        Annotation[] annotations = targetClass.getAnnotations();
        for(Annotation annotation : annotations){
            if(targetAnnotationClass != null && annotation.annotationType() == targetAnnotationClass){
                return true;
            }
        }
        return false;
    }

//    public static Annotation

}
