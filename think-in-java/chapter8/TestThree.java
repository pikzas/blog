package com.pikzas.thinkinjava.chapterthree;

public class TestThree {
    public static void main(String[] args) {
        test2();
        test3();
        test4();
        test5();
    }

    public static void test1() {
        Integer i1 = new Integer(1);
        Integer i2 = new Integer(1);
        System.out.println(i1 != i2);  //true
        System.out.println(i1 == i2);  //false
        System.out.println(i1 > i2);   //false
        System.out.println(i1 < i2);   //false
    }

    public static void test2() {
        System.out.println("--test2--");
        Double d1 = new Double(1.001);
        Double d2 = new Double(1.001);
        System.out.println(d1.equals(d2));
    }

    public static void test3() {
        System.out.println("--test3--");
        System.out.println((0b11)^(0b01));
    }

    public static void test4() {
        System.out.println("--test4--");
        System.out.println((int)(-29.5));
    }

    public static void test5() {
        System.out.println("--test5--");
        System.out.println(Math.round(-29.3));
        System.out.println(Math.round(-29.5));
        System.out.println(Math.round(-29.7));
        System.out.println(Math.round(29.3));
        System.out.println(Math.round(29.5));
        System.out.println(Math.round(29.7));
    }
}
