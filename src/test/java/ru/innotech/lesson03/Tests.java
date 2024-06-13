package ru.innotech.lesson03;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class Tests {
    @Test
    @DisplayName("Проверка кэширования")
    public void testCash(){
        TestFraction testFraction = new TestFraction(1,2);
        Fractionable fproxy = Utils.cache(testFraction);
        fproxy.doubleValue();
        fproxy.doubleValue();
        Assertions.assertEquals(testFraction.countCache, 1);
    }
    @Test
    @DisplayName("Проверка сброса кэширования")
    public void testCashStop(){
        TestFraction testFraction = new TestFraction(1,2);
        Fractionable fproxy = Utils.cache(testFraction);
        fproxy.doubleValue();
        fproxy.setNum(2);
        fproxy.doubleValue();
        Assertions.assertEquals(testFraction.countCache, 2);
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
    @DisplayName("Проверка, что после сброса кэш считается заново")
    public void testCashNewValue(){
        TestFraction testFraction = new TestFraction(1,2);
        Fractionable fproxy = Utils.cache(testFraction);
        fproxy.doubleValue();
        fproxy.setNum(4);
        fproxy.doubleValue();
        Assertions.assertEquals(fproxy.doubleValue(), 2);
    }

    @Test
    @DisplayName("Проверка, что метод без аннотации @Cache не кэшируется")
    public void testCashNot(){
        TestFraction testFraction = new TestFraction(1,2);
        Fractionable fproxy = Utils.cache(testFraction);
        fproxy.setDenum(2);
        fproxy.setDenum(2);
        Assertions.assertEquals(testFraction.countCalc, 2);
    }

    @Test
    @DisplayName("Проверка, что метод без аннотации @Mutator не сбрасывает кэш")
    public void testMutatorNot(){
        TestFraction testFraction = new TestFraction(1,2);
        Fractionable fproxy = Utils.cache(testFraction);
        fproxy.doubleValue();
        fproxy.setDenum(2);
        fproxy.doubleValue();
        Assertions.assertEquals(testFraction.countCache, 1);
    }

}
