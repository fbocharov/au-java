package ru.spbau.bocharov.FunctionalJava;

import org.junit.Test;

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
    public void testOr() {
        assertTrue(even.or(odd).apply(4));
        assertTrue(odd.or(even).apply(5));
        assertFalse(odd.or(odd).apply(4));

        assertTrue(even.or(even.not()).apply(4));
        assertTrue(even.or(even.not()).apply(5));

        assertTrue(even.or(Predicate.ALWAYS_TRUE).apply(4));
        assertTrue(odd.or(Predicate.ALWAYS_FALSE).apply(5));
    }

    @Test
    public void testAnd() {
        assertFalse(odd.and(even).apply(4));
        assertFalse(even.and(odd).apply(5));
        assertTrue(even.and(even).apply(4));

        assertFalse(odd.and(odd.not()).apply(4));
        assertTrue(odd.and(even.not()).apply(5));

        assertTrue(odd.and(Predicate.ALWAYS_TRUE).apply(5));
        assertFalse(even.and(Predicate.ALWAYS_FALSE).apply(4));
    }

    private final Predicate<Integer> even = (arg -> arg % 2 == 0);
    private final Predicate<Integer> odd = (arg -> arg % 2 == 1);
}