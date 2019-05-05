package com.pikzas.thinkinjava.chapterfour;

public class TestFour {
    public static void main(String[] args) {
        test1();
        test2();
        test3();
        test4();
        test5();

    }

    public static void test1() {
        System.out.println("--test1--");
        for (int i = 1 , j = i +5 ; (i < 5 && j < 8) ; i++ , j =i *2) {
            System.out.println("i = "+i+", j = "+j);
        }
    }

    public static void test2() {
        System.out.println("--test2--");
        int i = 0;
        outer:
        for(;;){
            inner:
            for(;i<10;i++){
                System.out.println("i = "+i);
                if(i==2){
                    System.out.println("continue");
                    continue ;
                }
                if(i==3){
                    System.out.println("break");
                    i++;
                    break;
                }
                if(i==7){
                    System.out.println("continue outer");
                    i++;
                    continue outer;
                }
                if(i==8){
                    System.out.println("break outer");
                    break outer;
                }

                for (int k = 0;k < 5;k++ ) {
                    if(k==3){
                        System.out.println("continue inner");
                        continue inner;
                    }
                }
            }
        }
    }

    public static void test3() {
        System.out.println("--test3--");
        short i = 126;
        switch (i){
            case (char)23:
                System.out.println(1111);
                break;
            case 0b1111110:
                System.out.println(2222);
                break;
            default:
                System.out.println("xxxxx");
        }

    }

    public static void test4() {
        System.out.println("--test4--");
    }

    public static void test5() {
        System.out.println("--test5--");
    }
}
