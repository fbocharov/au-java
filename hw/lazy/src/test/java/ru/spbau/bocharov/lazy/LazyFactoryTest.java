package ru.spbau.bocharov.lazy;

import org.junit.Test;

import java.util.function.Supplier;

import static org.junit.Assert.*;

public class LazyFactoryTest {

    private final Supplier<Boolean> s = () -> false;

    @Test
    public void testShouldCreateSingleThreadLazy() throws Exception {
        assertNotEquals(
                LazyFactory.createSingleThreadLazy(s),
                null);
    }

    @Test
    public void testShouldCreateMultiThreadLazy() throws Exception {
        assertNotEquals(
                LazyFactory.createMultiThreadLazy(s),
                null);
    }

    @Test
    public void testShouldCreateLockFreeLazy() throws Exception {
        assertNotEquals(
                LazyFactory.createLockFreeLazy(s),
                null);
    }

}