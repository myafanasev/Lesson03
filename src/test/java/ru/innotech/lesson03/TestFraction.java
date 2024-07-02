package ru.innotech.lesson03;

public class TestFraction implements Fractionable{
    private int num;
    private int denum;

    public Statistic stat = new Statistic();

    public TestFraction(int num, int denum) {
        this.num = num;
        this.denum = denum;
        stat.countCache = 0;
    }
    @Override
    @Mutator
    public void setNum(int num) {
        this.num = num;
    }
    @Override
    public void setDenum(int denum) {
        this.denum = denum;
    }

    @Override
    @Cache(1000)
    public double doubleValue() {
        System.out.println("invoke double value");
        stat.countCache++;
        return (double) num/denum;
    }

}

class Statistic
{
    public int countCache;
}