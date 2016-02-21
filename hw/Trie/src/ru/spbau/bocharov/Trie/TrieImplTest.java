package ru.spbau.bocharov.Trie;

import static org.junit.Assert.*;

/**
 * Created by fyodor on 2/17/16.
 */
public class TrieImplTest {

    @org.junit.Test
    public void testAdd() throws Exception {
        Trie trie = new TrieImpl();

        assertTrue(trie.add("Hello"));
        assertTrue(trie.add("World"));

        assertEquals(2, trie.size());

        assertTrue(trie.contains("Hello"));
        assertTrue(trie.contains("World"));
        assertFalse(trie.contains("H"));
        assertFalse(trie.contains("Wor"));
        assertFalse(trie.contains(""));
        assertFalse(trie.contains("HelloWorld"));

        trie.add("Hi");

        assertEquals(3, trie.size());
        assertEquals(2, trie.howManyStartsWithPrefix("H"));
        assertEquals(1, trie.howManyStartsWithPrefix("W"));

        assertFalse(trie.contains("H"));
        assertFalse(trie.contains("He"));

        trie.add("");

        assertEquals(4, trie.size());
        assertEquals(4, trie.howManyStartsWithPrefix(""));
        assertTrue(trie.contains(""));

        Trie trie2 = new TrieImpl();
        trie2.add("AAB");
        trie2.add("AAB");
        trie2.add("AAB");
        assertEquals(1, trie2.size());
        trie2.add("AAC");
        assertEquals(2, trie2.size());
        trie2.add("AAD");
        assertEquals(3, trie2.size());
        trie2.add("AA");
        assertEquals(4, trie2.size());
        trie2.add("A");
        assertEquals(5, trie2.size());
        trie2.add("BBC");
        assertEquals(6, trie2.size());
        trie2.add("BBD");
        assertEquals(7, trie2.size());
        trie2.add("BBE");
        assertEquals(8, trie2.size());
        trie2.add("B");
        assertEquals(9, trie2.size());
        assertTrue(trie2.contains("AAD"));
        assertTrue(trie2.contains("BBC"));
        assertTrue(trie2.contains("BBD"));
        assertTrue(trie2.contains("BBE"));
        assertEquals(3, trie2.howManyStartsWithPrefix("BB"));
    }

    @org.junit.Test
    public void testContains() throws Exception {
        Trie trie = new TrieImpl();

        trie.add("Hello");
        trie.add("World");
        trie.add("Hello world!");
        trie.add("Fizz");
        trie.add("Buzz");
        trie.add("FizzBuzz");
        trie.add("C++ > Java");

        assertTrue(trie.contains("Buzz"));
        assertTrue(trie.contains("Fizz"));
        assertTrue(trie.contains("Hello"));
        assertTrue(trie.contains("World"));
        assertTrue(trie.contains("FizzBuzz"));

        trie.remove("Hello world!");
        assertFalse(trie.contains("Hello world!"));
        assertTrue(trie.contains("Hello"));
        trie.remove("Fizz");
        assertTrue(trie.contains("FizzBuzz"));
        assertFalse(trie.contains("Fizz"));
    }

    @org.junit.Test
    public void testRemove() throws Exception {
        Trie trie = new TrieImpl();

        trie.add("Hello");
        trie.add("World");

        assertTrue(trie.remove("Hello"));
        assertEquals(1, trie.size());

        trie.add("");
        trie.add("HI");
        trie.add("Hi");

        assertEquals(4, trie.howManyStartsWithPrefix(""));
        assertEquals(4, trie.size());
        assertEquals(2, trie.howManyStartsWithPrefix("H"));

        assertTrue(trie.remove(""));
        assertFalse(trie.remove(""));
        assertFalse(trie.remove("Hello"));
        assertTrue(trie.remove("HI"));

        assertEquals(1, trie.howManyStartsWithPrefix("H"));
    }
}