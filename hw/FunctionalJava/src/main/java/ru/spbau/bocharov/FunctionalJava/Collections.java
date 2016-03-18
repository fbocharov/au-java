package ru.spbau.bocharov.FunctionalJava;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Collections {
    public static <T, R> List<R> map(AbstractFunction1<? super T, R> f, Iterable<T> c) {
        List<R> result = new ArrayList<>();
        for (T e : c) {
            result.add(f.apply(e));
        }
        return result;
    }

    public static <T> List<T> filter(Predicate<? super T> p, Iterable<T> c) {
        List<T> result = new ArrayList<>();
        for (T e : c) {
            if (p.apply(e)) {
                result.add(e);
            }
        }
        return result;
    }

    public static <T> List<T> takeWhile(Predicate<? super T> p, Iterable<T> c) {
        List<T> result = new ArrayList<>();
        for (T e : c) {
            if (p.apply(e)) {
                result.add(e);
            } else {
                break;
            }
        }
        return result;
    }

    public static <T> List<T> takeUnless(Predicate<? super T> p, Iterable<T> c) {
        return takeWhile(p.not(), c);
    }

    public static <T, R> R foldr(Function2<? super T, R, R> f, R ini, Iterable<T> c) {
        return foldr(f, ini, c.iterator());
    }

    public static <T, R> R foldl(Function2<R, ? super T, R> f, R ini, Iterable<T> c) {
        R result = ini;
        for (T e : c) {
            result = f.apply(result, e);
        }
        return result;
    }

    private static <T, R> R foldr(Function2<? super T, R, R> f, R ini, Iterator<T> it) {
        if (!it.hasNext()) {
            return ini;
        }
        T e = it.next();
        return f.apply(e, foldr(f, ini, it));
    }
}
