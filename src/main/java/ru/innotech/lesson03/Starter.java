package ru.innotech.lesson03;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Starter {
    public static void main(String[] args) throws InterruptedException {
        Fraction fraction = new Fraction(7, 2);
        Fractionable fproxy = Utils.cache(fraction);

        System.out.println(fproxy.doubleValue());
        System.out.println(fproxy.doubleValue());
        System.out.println(fproxy.sumValue());
        System.out.println(fproxy.sumValue());

        Fraction fraction1 = new Fraction(7, 2);
        Fractionable fproxy1 = Utils.cache(fraction);

        Thread.sleep(2000);
        System.out.println(ListObjCache.cacheObj.size());
        ListObjCache.cleanCache();
        /*        System.out.println(fproxy.doubleValue());
        fproxy.setDenum(4);
        System.out.println(fproxy.doubleValue());
        System.out.println(fproxy.doubleValue());
        Date moment = new Date(1451665447567L); // Задаем количество миллисекунд Unix-time с того-самого-момента
        moment.getTime(); // Узнаем количество миллисекунд Unix-time с того-самого-момента.
        System.out.println(moment.getTime());
        Date d = new Date(Instant.now().toEpochMilli());
        System.out.println(d);*/

//        Map<Map<String, Object>, Map<Method, DataCache>> cacheValues = new HashMap<>();
//        Map<String, Object> m1 = new HashMap<>();
//        m1.put("Привет", 1);
//        m1.put("Пока", 2);
//
//        cacheValues.put(m1,3);
//        System.out.println(cacheValues.get(m1));
//
//        m1.put("Пока", 3);
//        System.out.println(cacheValues.get(m1));
//
//        m1.put("Пока", 2);
//        System.out.println(cacheValues.get(m1));


    }
}