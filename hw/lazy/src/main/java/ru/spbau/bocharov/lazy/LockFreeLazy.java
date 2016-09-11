package ru.spbau.bocharov.lazy;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Supplier;

public class LockFreeLazy<T> implements Lazy<T> {

    private volatile Object supplierOrValue = null;

    private static final AtomicReferenceFieldUpdater<LockFreeLazy, Object> UPDATER =
            AtomicReferenceFieldUpdater.newUpdater(LockFreeLazy.class, Object.class, "supplierOrValue");

    public LockFreeLazy(Supplier<T> supplier) {
        supplierOrValue = new SupplierWrapper<>(supplier);
    }

    @Override
    public T get() {
        Object supplier = supplierOrValue;
        if (supplierOrValue != null && supplierOrValue instanceof SupplierWrapper) {
            Object o = ((SupplierWrapper<T>) supplier).get().get();
            UPDATER.compareAndSet(this, supplier, o);
        }
        return (T) supplierOrValue;
    }

    private class SupplierWrapper<T> implements Supplier<Supplier<T>> {

        private Supplier<T> wrapped = null;

        SupplierWrapper(Supplier<T> supplier) {
            wrapped = supplier;
        }

        @Override
        public Supplier<T> get() {
            return wrapped;
        }
    }
}
