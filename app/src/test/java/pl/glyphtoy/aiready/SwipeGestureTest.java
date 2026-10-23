package pl.glyphtoy.aiready;

import org.junit.Test;
import static org.junit.Assert.*;

public class SwipeGestureTest {
    @Test public void tapsAndVerticalScrollingDoNotBecomeHorizontalPaging() {
        assertFalse(SwipeGesture.horizontal(4,2,8));
        assertFalse(SwipeGesture.horizontal(15,90,8));
        assertFalse(SwipeGesture.horizontal(40,40,8));
        assertTrue(SwipeGesture.horizontal(80,20,8));
        assertEquals(0,SwipeGesture.pageDelta(10,2,320,40,false));
        assertEquals(0,SwipeGesture.pageDelta(80,100,320,40,false));
    }

    @Test public void directionMatchesReadingOrder() {
        assertEquals(1,SwipeGesture.pageDelta(-90,5,320,40,false));
        assertEquals(-1,SwipeGesture.pageDelta(90,5,320,40,false));
        assertEquals(-1,SwipeGesture.pageDelta(-90,5,320,40,true));
        assertEquals(1,SwipeGesture.pageDelta(90,5,320,40,true));
    }

    @Test public void shortDragsDoNotSwitchPagesOnWiderScreens() {
        assertEquals(1,SwipeGesture.pageDelta(-60,2,320,40,false));
        assertEquals(0,SwipeGesture.pageDelta(-60,2,600,40,false));
        assertEquals(1,SwipeGesture.pageDelta(-110,2,600,40,false));
    }

    @Test public void releasedDiagonalDragsDoNotSwitchPages() {
        assertTrue(SwipeGesture.horizontal(-90,5,8));
        assertEquals(0,SwipeGesture.pageDelta(-90,100,320,40,false));
    }
}
