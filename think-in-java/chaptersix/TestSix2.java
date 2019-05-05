package com.pikzas.thinkinjava.chaptersix;

public class TestSix2 extends TestSix1{

    public static void main(String[] args) {
        TestSix six = new TestSix();
        System.out.println(six.publicInt);
        System.out.println(six.defaultInt);
        System.out.println(six.protectedInt);
//        System.out.println(six.privateInt);
        six.publicFun();
        six.defaultFun();
        six.protectedFun();


        TestSix2 six2 = new TestSix2();
        System.out.println(six2.publicInt);
        System.out.println(six2.defaultInt);
        System.out.println(six2.protectedInt);
//        System.out.println(six2.privateInt);
        six2.publicFun();
        six2.defaultFun();
        six2.protectedFun();



    }
}
