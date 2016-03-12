package ru.spbau.mit.wowember;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public final class SecondPartTasks {

    public static final int ATTEMPTS_COUNT = (int)1e7;
    public static final double RADIUS = 0.5;
    public static final double TARGET_CENTER_X = 0.5;
    public static final double TARGET_CENTER_Y = 0.5;

    private SecondPartTasks() {}

    // Найти строки из переданных файлов, в которых встречается указанная подстрока.
    public static List<String> findQuotes(List<String> paths, CharSequence sequence) {
        try {
            return paths.stream().flatMap(x -> {
                try {
                    return Files.lines(Paths.get(x));
                } catch (IOException e) {
                    throw new UncheckedIOException("Given wrong path: " + x, e);
                }
            }).filter(x -> x.contains(sequence)).collect(Collectors.toList());
        }
        catch (UncheckedIOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // В квадрат с длиной стороны 1 вписана мишень.
    // Стрелок атакует мишень и каждый раз попадает в произвольную точку квадрата.
    // Надо промоделировать этот процесс с помощью класса java.util.Random и посчитать, какова вероятность попасть в мишень.
    public static double piDividedBy4() {
        Random random = new Random();
        class Point {
            private final double x;
            private final double y;

            public Point(double x, double y) {
                this.x = x - TARGET_CENTER_X;
                this.y = y - TARGET_CENTER_Y;
            }

            public boolean isHit() {
                return x * x + y * y <= RADIUS * RADIUS;
            }
        }

        return Stream
                .generate(() -> new Point(random.nextDouble(), random.nextDouble()))
                .limit(ATTEMPTS_COUNT)
                .filter(Point::isHit)
                .count() * 1.0 / ATTEMPTS_COUNT;
    }

    // Дано отображение из имени автора в список с содержанием его произведений.
    // Надо вычислить, чья общая длина произведений наибольшая.
    public static String findPrinter(Map<String, List<String>> compositions) {
        return compositions
                .entrySet()
                .stream()
                .max(Comparator
                        .comparing(p ->
                                p.getValue()
                                        .stream()
                                        .mapToInt(String::length).sum()))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    // Вы крупный поставщик продуктов. Каждая торговая сеть делает вам заказ в виде Map<Товар, Количество>.
    // Необходимо вычислить, какой товар и в каком количестве надо поставить.
    public static Map<String, Integer> calculateGlobalOrder(List<Map<String, Integer>> orders) {
        return orders
                .stream()
                .flatMap(mp -> mp.entrySet().stream())
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.summingInt(Map.Entry::getValue)));
    }
}
