package ru.spbau.bocharov.FunctionalJava;

import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.*;

public class PredicateTest {

    @Test
    public void testConstants() {
        assertTrue(Predicate.ALWAYS_TRUE.apply(new Object()));
        assertFalse(Predicate.ALWAYS_FALSE.apply(new Object()));

        assertFalse(Predicate.ALWAYS_TRUE.not().apply(true));
        assertTrue(Predicate.ALWAYS_FALSE.not().apply(false));
    }

    @Test
    public void testCompose() {
        String log = "";
        Function1<Boolean, String> writer = arg -> arg ? log.concat("true") : log.concat("false");
        assertNotEquals("", Predicate.ALWAYS_TRUE.compose(writer).apply(new Object()));
        assertEquals("false", Predicate.ALWAYS_FALSE.compose(writer).apply(new Object()));
    }

    @Test
    public void testOr() {
        assertTrue(IS_EVEN.or(IS_ODD).apply(4));
        assertTrue(IS_ODD.or(IS_EVEN).apply(5));
        assertFalse(IS_ODD.or(IS_ODD).apply(4));

        assertTrue(IS_EVEN.or(IS_EVEN.not()).apply(4));
        assertTrue(IS_EVEN.or(IS_EVEN.not()).apply(5));

        assertTrue(IS_EVEN.or(Predicate.ALWAYS_TRUE).apply(4));
        assertTrue(IS_ODD.or(Predicate.ALWAYS_FALSE).apply(5));
    }

    @Test
    public void testLaziness() {
        Predicate<Object> eq = arg -> arg.equals("");
        assertTrue(Predicate.ALWAYS_TRUE.or(eq).apply(null));
        assertFalse(Predicate.ALWAYS_FALSE.and(eq).apply(null));
    }

    @Test
    public void testAnd() {
        assertFalse(IS_ODD.and(IS_EVEN).apply(4));
        assertFalse(IS_EVEN.and(IS_ODD).apply(5));
        assertTrue(IS_EVEN.and(IS_EVEN).apply(4));

        assertFalse(IS_ODD.and(IS_ODD.not()).apply(4));
        assertTrue(IS_ODD.and(IS_EVEN.not()).apply(5));

        assertTrue(IS_ODD.and(Predicate.ALWAYS_TRUE).apply(5));
        assertFalse(IS_EVEN.and(Predicate.ALWAYS_FALSE).apply(4));
    }

    private static final Predicate<Integer> IS_EVEN = arg -> arg % 2 == 0;
    private static final Predicate<Integer> IS_ODD = arg -> arg % 2 == 1;
}