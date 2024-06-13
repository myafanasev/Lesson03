package ru.innotech.lesson03;

public class TestFraction implements Fractionable{
    private int num;
    private int denum;
    public int countCache;
    public int countCalc;

    public TestFraction(int num, int denum) {
        this.num = num;
        this.denum = denum;
        countCache = 0;
        countCalc = 0;
    }
    @Override
    @Mutator
    public void setNum(int num) {
        this.num = num;
    }
    @Override
    public void setDenum(int denum) {
        this.denum = denum;
        countCalc++;
    }

    @Override
    @Cache
    public double doubleValue() {
        System.out.println("invoke double value");
        countCache++;
        return (double) num/denum;
    }

}
