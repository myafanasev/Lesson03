package ru.innotech.lesson03;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class Tests {
    @Test
    @DisplayName("Проверка простого кэширования")
    public void testCash(){
        TestFraction testFraction = new TestFraction(1,2);
        Fractionable fproxy = Utils.cache(testFraction);
        fproxy.doubleValue();
        fproxy.doubleValue();
        Assertions.assertEquals(testFraction.stat.countCache, 1);
    }
    @Test
    @DisplayName("Проверка кэширования предыдущего состояния")
    public void testCashPrev(){
        TestFraction testFraction = new TestFraction(1,2);
        Fractionable fproxy = Utils.cache(testFraction);
        fproxy.doubleValue();
        fproxy.setNum(2);
        fproxy.doubleValue();
        fproxy.setNum(1); // возвращаем предыдущее состояние
        fproxy.doubleValue();
        Assertions.assertEquals(testFraction.stat.countCache, 2);
    }

    @Test
    @DisplayName("Проверка значения из кэша")
    public void testCashValue(){
        TestFraction testFraction = new TestFraction(1,2);
        Fractionable fproxy = Utils.cache(testFraction);
        fproxy.doubleValue();
        Assertions.assertEquals(fproxy.doubleValue(), 0.5);
    }

    @Test
    @DisplayName("Проверка очистки кэша")
    public void testCashClean() throws InterruptedException {
        TestFraction testFraction = new TestFraction(1,2);
        Fractionable fproxy = Utils.cache(testFraction);
        fproxy.doubleValue();
        fproxy.setNum(2);
        fproxy.doubleValue();
        Thread.sleep(1500);
        fproxy.setNum(1); // возвращаем предыдущее состояние
        Thread.sleep(500); // пауза, чтобы подождать заверешия потока очистки кэша
        fproxy.doubleValue();
        Assertions.assertEquals(testFraction.stat.countCache, 3);
    }
}
