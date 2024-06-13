package ru.innotech.lesson03;

public class Starter {
    public static void main(String[] args) {
        Fraction fraction = new Fraction(7, 2);
        Fractionable fproxy = Utils.cache(fraction);

        System.out.println(fproxy.doubleValue());
        System.out.println(fproxy.doubleValue());
        System.out.println(fproxy.doubleValue());
        fproxy.setDenum(4);
        System.out.println(fproxy.doubleValue());
        System.out.println(fproxy.doubleValue());
    }
}