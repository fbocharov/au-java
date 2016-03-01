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

        assertTrue(trie.add(""));
        assertFalse(trie.add(""));

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

        String str1 = "cat";
        String str2 = "dog";

        assertTrue(trie.add(str1));
        assertTrue(trie.add(str2));
        assertFalse(trie.add(str1));
        assertFalse(trie.add(str2));

        assertTrue(trie.add("ca"));
        assertTrue(trie.add("c"));
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

        trie.add("Very");
        trie.add("VeryVery");
        trie.add("VeryVeryLong");
        trie.add("VeryVeryLongWord");

        assertTrue(trie.contains("Very"));
        assertTrue(trie.contains("VeryVery"));
        assertTrue(trie.contains("VeryVeryLong"));
        assertTrue(trie.contains("VeryVeryLongWord"));

        assertFalse(trie.contains("AnyWord"));
        assertFalse(trie.contains("Word"));
        assertFalse(trie.contains("Ve"));
        assertFalse(trie.contains("VeryV"));
        assertFalse(trie.contains("VeryVeryLongWord1"));
        assertFalse(trie.contains("VeryVeryLongWor"));

        trie.remove("Very");
        assertFalse(trie.contains("Very"));
        assertTrue(trie.contains("VeryVery"));

        trie.remove("VeryVery");
        assertFalse(trie.contains("VeryVery"));
        assertTrue(trie.contains("VeryVeryLong"));

        trie.add("VeryVeryLongWord1");
        trie.remove("VeryVeryLongWord");
        assertTrue(trie.contains("VeryVeryLongWord1"));
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

        trie.add("String");
        trie.add("Word");
        assertFalse(trie.remove("YetWord"));
        assertTrue(trie.remove("Word"));

        trie.add("Word");
        trie.add("word");

        assertTrue(trie.remove("word"));

        Trie set = new TrieImpl();

        String s = "s";
        String ss = "ss";
        String sa = "sa";

        assertTrue(set.add(s));
        assertTrue(set.add(ss));
        assertTrue(set.add(sa));

        assertEquals(3, set.size());

        assertTrue(set.remove(s));
        assertFalse(set.remove(s));

        assertEquals(2, set.howManyStartsWithPrefix("s"));
    }

    @org.junit.Test
    public void testSize() throws Exception {
        Trie trie = new TrieImpl();
        assertEquals(trie.size(), 0);
        trie.add("String");
        assertEquals(trie.size(), 1);
        trie.add("String2");
        assertEquals(trie.size(), 2);
        trie.add("Word");
        assertEquals(trie.size(), 3);

        trie.remove("str");
        assertEquals(trie.size(), 3);

        trie.remove("String");
        assertEquals(trie.size(), 2);

        trie.remove("String2");
        assertEquals(trie.size(), 1);

        trie.remove("Word");
        assertEquals(trie.size(), 0);

        trie.remove("Word");
        assertEquals(trie.size(), 0);

        trie.remove("Word");
        assertEquals(trie.size(), 0);
    }

    @org.junit.Test
    public void testHowManyStartsWithPrefix() throws Exception {
        Trie trie = new TrieImpl();
        trie.add("Very");
        trie.add("VeryVery");
        trie.add("VeryVeryLong");
        trie.add("VeryVeryLongWord");

        assertEquals(trie.howManyStartsWithPrefix("V"), 4);
        assertEquals(trie.howManyStartsWithPrefix("Very"), 4);
        assertEquals(trie.howManyStartsWithPrefix("VE"), 0);
        assertEquals(trie.howManyStartsWithPrefix("VeryV"), 3);
        assertEquals(trie.howManyStartsWithPrefix("VeryVery"), 3);
        assertEquals(trie.howManyStartsWithPrefix("VeryVeryV"), 0);
        assertEquals(trie.howManyStartsWithPrefix("VeryVeryL"), 2);
        assertEquals(trie.howManyStartsWithPrefix("VeryVeryLongWord"), 1);
    }
}