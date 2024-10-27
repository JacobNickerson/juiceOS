package jbash;


import jbash.filesystem.TBFF;
import org.junit.jupiter.api.Test;

public class TBFFTests {
    TBFF testPartition = new TBFF("testPartition.bff", 1024*1024);

    @Test
    void testGetFreeInodeSpot() {
        testPartition.getFreeInodeSpot();
    }
}