package ru.spbau.bocharov.streams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public final class SecondPartTasks {

    private SecondPartTasks() {}

    // Найти строки из переданных файлов, в которых встречается указанная подстрока.
    public static List<String> findQuotes(List<String> paths, CharSequence sequence) {
        return paths.stream()
                    .map(SecondPartTasks::safeLines)
                    .flatMap(Collection::stream)
                    .filter(line -> line.contains(sequence))
                    .collect(toList());
    }

    // В квадрат с длиной стороны 1 вписана мишень.
    // Стрелок атакует мишень и каждый раз попадает в произвольную точку квадрата.
    // Надо промоделировать этот процесс с помощью класса java.util.Random и посчитать, какова вероятность попасть в мишень.
    public static double piDividedBy4() {
        return zipSubstracted(new Random().doubles(), new Random().doubles())
                .limit(SHOT_COUNT)
                .filter(p -> p.x * p.x + p.y * p.y <= TARGET_RADIUS * TARGET_RADIUS)
                .count() / (double) SHOT_COUNT;
    }

    // Дано отображение из имени автора в список с содержанием его произведений.
    // Надо вычислить, чья общая длина произведений наибольшая.
    public static String findPrinter(Map<String, List<String>> compositions) {
        return compositions.entrySet()
                           .stream()
                           .max(Comparator.comparing(e -> e.getValue().stream().mapToInt(String::length).sum()))
                           .map(Map.Entry::getKey)
                           .orElse("");
    }

    // Вы крупный поставщик продуктов. Каждая торговая сеть делает вам заказ в виде Map<Товар, Количество>.
    // Необходимо вычислить, какой товар и в каком количестве надо поставить.
    public static Map<String, Integer> calculateGlobalOrder(List<Map<String, Integer>> orders) {
        return orders.stream()
                     .map(Map::entrySet)
                     .flatMap(Collection::stream)
                     .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            Integer::sum));
    }

    private static final int SHOT_COUNT = 10000;
    private static final double TARGET_RADIUS = 0.5;

    private static List<String> safeLines(String path) {
        try (Stream<String> lines = Files.lines(Paths.get(path))) {
            return lines.collect(toList());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static Stream<Pair> zipSubstracted(DoubleStream left, DoubleStream right) {
        Iterator<Double> leftIterator = left.iterator();
        Iterator<Double> rightIterator = right.iterator();

        return Stream.generate(() -> new Pair(leftIterator.next()  - TARGET_RADIUS,
                                              rightIterator.next() - TARGET_RADIUS));
    }

    private static class Pair {
        private double x;
        private double y;

        Pair(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

}
