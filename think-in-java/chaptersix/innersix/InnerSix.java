package com.pikzas.thinkinjava.chaptersix.innersix;

import com.pikzas.thinkinjava.chaptersix.TestSix;

class InnerSix extends TestSix{
    public static void main(String[] args) {
        InnerSix innerSix = new InnerSix();
        System.out.println(innerSix.publicInt);
        System.out.println(innerSix.protectedInt);

        innerSix.publicFun();
        innerSix.protectedFun();
    }
}
