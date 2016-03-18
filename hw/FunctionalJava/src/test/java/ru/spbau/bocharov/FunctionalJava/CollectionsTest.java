package ru.spbau.bocharov.FunctionalJava;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class CollectionsTest {

    @Test
    public void testMap() throws Exception {
        assertEquals(Collections.map(id, emptyCollection), emptyCollection);
        assertEquals(Collections.map(id, collection10Elements), collection10Elements);

        Function1<Integer, Integer> sqr = (arg -> (int) Math.pow(arg, 2));
        assertEquals(Collections.map(sqr, collection10Elements), makeSquares(10));
    }

    @Test
    public void testFilter() throws Exception {
        assertEquals(Collections.filter(Predicate.ALWAYS_FALSE, collection10Elements), emptyCollection);
        assertEquals(Collections.filter(Predicate.ALWAYS_TRUE, collection10Elements), collection10Elements);

        Predicate<Integer> p = (arg -> arg < 5);
        assertEquals(Collections.filter(p, collection10Elements), makeIntegers(5));
    }

    @Test
    public void testTakeWhile() throws Exception {
        assertEquals(Collections.takeWhile(Predicate.ALWAYS_FALSE, emptyCollection), emptyCollection);
        assertEquals(Collections.takeWhile(Predicate.ALWAYS_FALSE, collection10Elements), emptyCollection);
        assertEquals(Collections.takeWhile(Predicate.ALWAYS_TRUE, collection10Elements), collection10Elements);

        Predicate<Integer> p = (arg -> (arg + 1) % 7 != 0);
        assertEquals(Collections.takeWhile(p, collection10Elements), makeIntegers(6));
    }

    @Test
    public void testTakeUnless() throws Exception {
        assertEquals(Collections.takeUnless(Predicate.ALWAYS_FALSE, emptyCollection), emptyCollection);
        assertEquals(Collections.takeUnless(Predicate.ALWAYS_FALSE, collection10Elements), collection10Elements);
        assertEquals(Collections.takeUnless(Predicate.ALWAYS_TRUE, collection10Elements), emptyCollection);

        Predicate<Integer> p = (arg -> arg > 4);
        assertEquals(Collections.takeUnless(p, collection10Elements), makeIntegers(5));
    }

    @Test
    public void testFoldr() throws Exception {
        Function2<Integer, Integer, Integer> sum = ((arg1, arg2) -> arg1 + arg2);
        assertTrue(45 == Collections.foldr(sum, 0, collection10Elements));

        Function2<Integer, Boolean, Boolean> less10 = ((arg, p) -> p && arg < 10);
        assertTrue(Collections.foldr(less10, true, collection10Elements));

        Function2<Integer, Boolean, Boolean> has8 = ((arg, p) -> p || arg == 8);
        assertTrue(Collections.foldr(has8, false, collection10Elements));
    }

    @Test
    public void testFoldl() throws Exception {
        Function2<Integer, Integer, Integer> sum = ((arg1, arg2) -> arg1 + arg2);
        assertTrue(45 == Collections.foldr(sum, 0, collection10Elements));

        Function2<Boolean, Integer, Boolean> less10 = ((p, arg) -> p && arg < 10);
        assertTrue(Collections.foldl(less10, true, collection10Elements));

        Function2<Boolean, Integer, Boolean> has8 = ((p, arg) -> p || arg == 8);
        assertTrue(Collections.foldl(has8, false, collection10Elements));
    }

    private Iterable<Integer> makeIntegers(int size) {
        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < size; ++i) {
            list.add(i);
        }
        return list;
    }

    private List<Integer> makeSquares(int size) {
        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < size; ++i) {
            list.add((int) Math.pow(i, 2));
        }
        return list;
    }

    private final Function1<Integer, Integer> id = (arg -> arg);
    private final Iterable<Integer> emptyCollection = makeIntegers(0);
    private final Iterable<Integer> collection10Elements = makeIntegers(10);
}