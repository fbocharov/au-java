package ru.spbau.bocharov.streams;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;

public class SecondPartTasksTest {

    @Test
    public void testFindQuotes() {
        List<String> paths =
                Arrays.asList(
                        getFilePath(TEST_FILE_1),
                        getFilePath(TEST_FILE_2)
                );

        assertEquals(
                SecondPartTasks.findQuotes(paths, "second"),
                Collections.singletonList("second"));

        assertEquals(
                SecondPartTasks.findQuotes(paths, "sts"),
                Collections.singletonList("And said: \"O mists, make room for me!\""));

        assertEquals(
                SecondPartTasks.findQuotes(paths, "on"),
                Arrays.asList(
                        "second",
                        "It hailed the ships, and cried: \"Sail on,",
                        "Ye mariners, the night is gone.\""
                ));

        assertEquals(
                SecondPartTasks.findQuotes(Collections.emptyList(), ""),
                Collections.emptyList());
    }

    @Test
    public void testPiDividedBy4() {
        assertEquals(
                Math.PI / 4.0,
                SecondPartTasks.piDividedBy4(),
                EPSILON);
    }

    @Test
    public void testFindPrinter() {
        assertEquals(
                SecondPartTasks.findPrinter(AUTHORS_COMPOSITIONS),
                AUTHOR_2);

        assertEquals(
                SecondPartTasks.findPrinter(Collections.emptyMap()),
                EMPTY_AUTHOR);
    }

    @Test
    public void testCalculateGlobalOrder() {
        assertEquals(
                SecondPartTasks.calculateGlobalOrder(ORDERS),
                ImmutableMap.of(
                        ITEM_1, 2 * ITEM_1_COUNT,
                        ITEM_2, 2 * ITEM_2_COUNT
                ));

        assertEquals(
                SecondPartTasks.calculateGlobalOrder(Collections.emptyList()),
                Collections.emptyMap());
    }

    private static final String TEST_FILE_1 = "test-file1.txt";
    private static final String TEST_FILE_2 = "test-file2.txt";

    private static final double EPSILON = 1e-2;

    private static final String AUTHOR_1 = "author1";
    private static final List<String> AUTHOR_1_COMPOSITIONS =
            Arrays.asList(
                    "short composition",
                    "very     long     composition",
                    "" // incomplete one
            );
    private static final String AUTHOR_2 = "author2";
    private static final List<String> AUTHOR_2_COMPOSITIONS =
            Arrays.asList(
                    "very   long   composition    one",
                    "very   long   composition    two"
            );
    private static final String AUTHOR_3 = "author3";
    private static final List<String> AUTHOR_3_COMPOSITIONS =
            Collections.emptyList();
    private static final String EMPTY_AUTHOR = "";
    private static final Map<String, List<String>> AUTHORS_COMPOSITIONS =
            ImmutableMap.of(
                    AUTHOR_1, AUTHOR_1_COMPOSITIONS,
                    AUTHOR_2, AUTHOR_2_COMPOSITIONS,
                    AUTHOR_3, AUTHOR_3_COMPOSITIONS
            );


    private static final String ITEM_1 = "item1";
    private static final int ITEM_1_COUNT = 10;
    private static final String ITEM_2 = "item2";
    private static final int ITEM_2_COUNT = 20;
    private static final List<Map<String, Integer>> ORDERS =
            Arrays.asList(
                    ImmutableMap.of(
                            ITEM_1, ITEM_1_COUNT,
                            ITEM_2, ITEM_2_COUNT
                    ),
                    ImmutableMap.of(
                            ITEM_1, ITEM_1_COUNT,
                            ITEM_2, ITEM_2_COUNT
                    )
            );

    private static String getFilePath(String filename) {
        URL resource = ClassLoader.getSystemClassLoader().getResource(filename);
        return resource == null ? null : resource.getFile();
    }
}