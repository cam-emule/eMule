package uk.ac.cam.cl.emule;

import org.junit.Test;

import uk.ac.cam.cl.emule.util.LocationUtil;

import static org.junit.Assert.assertEquals;


/**
 * Created by Fergus Leen (fl376@cl.cam.ac.uk) on 18/04/2017.
 */

public class LocationUtilTest {
//    @Test
////    public void LocationUtil_getDate() {
////
////        assertEquals (LocationUtil.getDate(1492523649), "18 Apr 14:54");
////    }

    @Test
    public void LocationUtil_getDateCurrentTimeZone() {
        assertEquals(LocationUtil.getDateCurrentTimeZone(1492523649), "18 Apr 14:54");
    }
}
