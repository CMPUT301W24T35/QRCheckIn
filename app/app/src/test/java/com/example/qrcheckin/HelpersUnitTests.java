package com.example.qrcheckin;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;


public class HelpersUnitTests {

    @Test
    public void reverseStringTest(){
        assertEquals(Helpers.reverseString("string"),"gnirts");
    }
}
