package ru.spbau.bocharov.FunctionalJava;

import org.junit.Test;

import static org.junit.Assert.*;

public class Function2Test {

    @Test
    public void testCompose() throws Exception {
        Function1<Integer, Integer> id = (arg -> arg);
        assertEquals(optionalMinus.compose(id).apply(10, false), optionalMinus.apply(10, false));

        Function1<Integer, Integer> zero = (arg -> 0);
        assertEquals(optionalMinus.compose(zero).apply(-1000, true), zero.apply(-1000));
        assertEquals(optionalMinus.compose(zero).apply(-1000, true), zero.apply(1000));
    }

    @Test
    public void testBind1() throws Exception {
        Function1<Boolean, Integer> f1 = optionalMinus.bind1(10);
        assertTrue(f1.apply(false) < 0);
        assertTrue(f1.apply(true) > 0);

        f1 = optionalMinus.bind1(0);
        assertTrue(f1.apply(true) == 0);
        assertTrue(f1.apply(false) == 0);

        f1 = optionalMinus.bind1(-10);
        assertTrue(f1.apply(true) < 0);
        assertTrue(f1.apply(false) > 0);
    }

    @Test
    public void testBind2() throws Exception {
        Function1<Integer, Integer> f1 = optionalMinus.bind2(false);
        assertTrue(f1.apply(10) < 0);
        assertTrue(f1.apply(-10) > 0);
        assertFalse(f1.apply(0) != 0);

        f1 = optionalMinus.bind2(true);
        assertFalse(f1.apply(10) < 0);
        assertFalse(f1.apply(-10) > 0);
        assertTrue(f1.apply(0) == 0);
    }

    @Test
    public void testCurry() throws Exception {
        assertTrue(optionalMinus.curry().apply(10).apply(true) > 0);
        assertFalse(optionalMinus.curry().apply(-10).apply(true) > 0);
        assertTrue(optionalMinus.curry().apply(5).apply(false) < 0);
        assertTrue(optionalMinus.curry().apply(0).apply(true) == 0);
        assertFalse(optionalMinus.curry().apply(0).apply(false) != 0);
    }

    private final Function2<Integer, Boolean, Integer> optionalMinus = ((arg1, arg2) -> arg2 ? arg1 : -arg1);
}