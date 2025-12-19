package test;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class test {
    @Test
    public void t(){
        String str1 = "str1";
        String str2 = "str"+1;
        System.out.println(str1==str2);

    }
}
