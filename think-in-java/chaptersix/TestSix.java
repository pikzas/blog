package com.pikzas.thinkinjava.chaptersix;

import com.pikzas.thinkinjava.chapterfive.TestFive;

public class TestSix {
    public int publicInt = 123;
    int defaultInt = 234;
    protected int protectedInt = 123;
    private int privateInt = 123;

    public void publicFun(){
        System.out.println(123);
    }

    void defaultFun(){
        System.out.println(123);
    }

    protected void protectedFun(){
        System.out.println(123);
    }
    private void privateFun(){
        System.out.println(123);
    }

    public static void main(String[] args) {
        test1();
        test2();
        test3();
        test4();
        test5();
        TestSix1 six1 = new TestSix1();

    }

    public static void test1() {
        System.out.println("--test1--");
    }

    public static void test2() {
        System.out.println("--test2--");
    }

    public static void test3() {
        System.out.println("--test3--");
    }

    public static void test4() {
        System.out.println("--test4--");
    }

    public static void test5() {
        System.out.println("--test5--");
    }
}
 class TestSix1{
     public int publicInt = 123;
     int defaultInt = 234;
     protected int protectedInt = 123;
     private int privateInt = 123;

     public void publicFun(){
         System.out.println(123);
     }

     void defaultFun(){
         System.out.println(123);
     }

     protected void protectedFun(){
         System.out.println(123);
     }
     private void privateFun(){
         System.out.println(123);
     }

     public static void main(String[] args) {
         TestSix six = new TestSix();
         TestSix.test1();
         TestFive.test1();
     }
}
