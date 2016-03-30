package ru.spbau.bocharov.FunctionalJava;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class CollectionsTest {

    @Test
    public void testMap() {
        assertEquals(Collections.map(IDENTITY, EMPTY_COLLECTION), EMPTY_COLLECTION);
        assertEquals(Collections.map(IDENTITY, COLLECTION_10_ELEMENTS), COLLECTION_10_ELEMENTS);

        Function1<Integer, Integer> sqr = arg -> (int) Math.pow(arg, 2);
        assertEquals(Collections.map(sqr, COLLECTION_10_ELEMENTS), makeSquares(10));

        List<String> strNumbers = Collections.map(Object::toString, COLLECTION_10_ELEMENTS);
        assertEquals(strNumbers.get(0), COLLECTION_10_ELEMENTS.iterator().next().toString());
    }

    @Test
    public void testFilter() {
        assertEquals(Collections.filter(Predicate.ALWAYS_FALSE, COLLECTION_10_ELEMENTS), EMPTY_COLLECTION);
        assertEquals(Collections.filter(Predicate.ALWAYS_TRUE, COLLECTION_10_ELEMENTS), COLLECTION_10_ELEMENTS);

        Predicate<Integer> p = arg -> arg < 5;
        assertEquals(Collections.filter(p, COLLECTION_10_ELEMENTS), makeIntegers(5));
    }

    @Test
    public void testTakeWhile() {
        assertEquals(Collections.takeWhile(Predicate.ALWAYS_FALSE, EMPTY_COLLECTION), EMPTY_COLLECTION);
        assertEquals(Collections.takeWhile(Predicate.ALWAYS_FALSE, COLLECTION_10_ELEMENTS), EMPTY_COLLECTION);
        assertEquals(Collections.takeWhile(Predicate.ALWAYS_TRUE, COLLECTION_10_ELEMENTS), COLLECTION_10_ELEMENTS);

        Predicate<Integer> p = arg -> (arg + 1) % 7 != 0;
        assertEquals(Collections.takeWhile(p, COLLECTION_10_ELEMENTS), makeIntegers(6));
    }

    @Test
    public void testTakeUnless() {
        assertEquals(Collections.takeUnless(Predicate.ALWAYS_FALSE, EMPTY_COLLECTION), EMPTY_COLLECTION);
        assertEquals(Collections.takeUnless(Predicate.ALWAYS_FALSE, COLLECTION_10_ELEMENTS), COLLECTION_10_ELEMENTS);
        assertEquals(Collections.takeUnless(Predicate.ALWAYS_TRUE, COLLECTION_10_ELEMENTS), EMPTY_COLLECTION);

        Predicate<Integer> p = arg -> arg > 4;
        assertEquals(Collections.takeUnless(p, COLLECTION_10_ELEMENTS), makeIntegers(5));
    }

    @Test
    public void testFoldr() {
        Function2<Integer, Integer, Integer> sum = (arg1, arg2) -> arg1 + arg2;
        assertEquals(45, (int) Collections.foldr(sum, 0, COLLECTION_10_ELEMENTS));

        Function2<Integer, Boolean, Boolean> less10 = (arg, p) -> p && arg < 10;
        assertTrue(Collections.foldr(less10, true, COLLECTION_10_ELEMENTS));

        Function2<Integer, Boolean, Boolean> has8 = (arg, p) -> p || arg == 8;
        assertTrue(Collections.foldr(has8, false, COLLECTION_10_ELEMENTS));

        Function2<Object, String, String> toStringConcat = (arg1, arg2) -> arg1.toString().concat(arg2);
        assertEquals("0123456789", Collections.foldr(toStringConcat, "", COLLECTION_10_ELEMENTS));
    }

    @Test
    public void testFoldl() {
        Function2<Integer, Integer, Integer> sum = (arg1, arg2) -> arg1 + arg2;
        assertEquals(45, (int) Collections.foldr(sum, 0, COLLECTION_10_ELEMENTS));

        Function2<Boolean, Integer, Boolean> less10 = (p, arg) -> p && arg < 10;
        assertTrue(Collections.foldl(less10, true, COLLECTION_10_ELEMENTS));

        Function2<Boolean, Integer, Boolean> has8 = (p, arg) -> p || arg == 8;
        assertTrue(Collections.foldl(has8, false, COLLECTION_10_ELEMENTS));

        Function2<String, Object, String> toStringConcat = (arg1, arg2) -> arg2.toString().concat(arg1);
        assertEquals("9876543210", Collections.foldl(toStringConcat, "", COLLECTION_10_ELEMENTS));
    }

    @Test
    public void testFoldDiff() {
        Function2<Integer, Integer, Integer> minus = (arg1, arg2) -> arg1 - arg2;
        assertNotEquals(Collections.foldl(minus, 0, COLLECTION_10_ELEMENTS),
                Collections.foldr(minus, 0, COLLECTION_10_ELEMENTS));
    }

    private static Iterable<Integer> makeIntegers(int size) {
        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < size; ++i) {
            list.add(i);
        }
        return list;
    }

    private static List<Integer> makeSquares(int size) {
        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < size; ++i) {
            list.add((int) Math.pow(i, 2));
        }
        return list;
    }

    private static final Function1<Integer, Integer> IDENTITY = (arg -> arg);
    private static final Iterable<Integer> EMPTY_COLLECTION = makeIntegers(0);
    private static final Iterable<Integer> COLLECTION_10_ELEMENTS = makeIntegers(10);
}