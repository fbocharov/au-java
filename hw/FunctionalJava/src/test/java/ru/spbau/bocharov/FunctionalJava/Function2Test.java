package ru.spbau.bocharov.FunctionalJava;

import org.junit.Test;

import static org.junit.Assert.*;

public class Function2Test {

    @Test
    public void testCompose() {
        Function1<Integer, Integer> id = arg -> arg;
        assertEquals(OPTIONAL_MINUS.compose(id).apply(10, false), OPTIONAL_MINUS.apply(10, false));

        Function1<Integer, Integer> zero = arg -> 0;
        assertEquals(OPTIONAL_MINUS.compose(zero).apply(-1000, true), zero.apply(-1000));
        assertEquals(OPTIONAL_MINUS.compose(zero).apply(-1000, true), zero.apply(1000));

        assertEquals("123", OPTIONAL_MINUS.compose(Object::toString).apply(-123, false));
    }

    @Test
    public void testBind1() {
        Function1<Boolean, Integer> f1 = OPTIONAL_MINUS.bind1(10);
        assertTrue(f1.apply(false) < 0);
        assertTrue(f1.apply(true) > 0);

        f1 = OPTIONAL_MINUS.bind1(0);
        assertEquals((int) f1.apply(true), 0);
        assertEquals((int) f1.apply(false), 0);

        f1 = OPTIONAL_MINUS.bind1(-10);
        assertTrue(f1.apply(true) < 0);
        assertTrue(f1.apply(false) > 0);
    }

    @Test
    public void testBind2() {
        Function1<Integer, Integer> f1 = OPTIONAL_MINUS.bind2(false);
        assertTrue(f1.apply(10) < 0);
        assertTrue(f1.apply(-10) > 0);
        assertEquals((int) f1.apply(0), 0);

        f1 = OPTIONAL_MINUS.bind2(true);
        assertFalse(f1.apply(10) < 0);
        assertFalse(f1.apply(-10) > 0);
        assertEquals((int) f1.apply(0), 0);
    }

    @Test
    public void testCurry() {
        assertTrue(OPTIONAL_MINUS.curry().apply(10).apply(true) > 0);
        assertFalse(OPTIONAL_MINUS.curry().apply(-10).apply(true) > 0);
        assertTrue(OPTIONAL_MINUS.curry().apply(5).apply(false) < 0);

        assertEquals((int) OPTIONAL_MINUS.curry().apply(0).apply(true), 0);
        assertEquals((int) OPTIONAL_MINUS.curry().apply(0).apply(false), 0);
    }

    private static final Function2<Integer, Boolean, Integer> OPTIONAL_MINUS = (arg1, arg2) -> arg2 ? arg1 : -arg1;
}