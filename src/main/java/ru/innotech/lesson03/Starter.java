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
        Fraction fr = new Fraction(2,3);
        Fractionable num = Utils.cache(fr);
        num.doubleValue();// sout сработал
        num.doubleValue();// sout молчит

        num.setNum(5);
        num.doubleValue();// sout сработал
        num.doubleValue();// sout молчит

        num.setNum(2);
        num.doubleValue();// sout молчит

        Thread.sleep(1500);
        num.setNum(2);
        Thread.sleep(500);
        num.doubleValue();// sout сработал
        num.doubleValue();// sout молчит
    }
}