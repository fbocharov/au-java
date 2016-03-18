package ru.spbau.bocharov.FunctionalJava;

import org.junit.Test;

import static org.junit.Assert.*;

public class Function1Test {

    @Test
    public void testFunction1Compose() {
        Function1<Integer, Integer> f = (arg -> arg > 0 ? 1 : -1);
        Function1<Integer, Boolean> g = (arg -> arg == 1);
        Function1<Integer, Boolean> h = f.compose(g);

        assertTrue(h.apply(10));
        assertFalse(h.apply(-10));

        Function1<Integer, Integer> id = (arg -> arg);
        assertEquals(f.compose(id).apply(10), f.apply(10));
        assertEquals(id.compose(g).apply(5), g.apply(5));
    }
}