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
                    throw new UncheckedIOException("Given wrong path: "+x, e);
                }
            }).filter(x -> x.contains(sequence)).collect(Collectors.toList());
        }
        catch (UncheckedIOException e) {
            System.err.println(e.getMessage());
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
            private double x;
            private double y;

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
                .sorted(Comparator
                        .comparing(p ->
                                p.getValue()
                                        .stream()
                                        .reduce(0, (sum, book) -> sum + book.length(), (sum1, sum2) -> sum1 + sum2), Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    // Вы крупный поставщик продуктов. Каждая торговая сеть делает вам заказ в виде Map<Товар, Количество>.
    // Необходимо вычислить, какой товар и в каком количестве надо поставить.
    public static Map<String, Integer> calculateGlobalOrder(List<Map<String, Integer>> orders) {
        return orders
                .stream()
                .reduce(new HashMap(), (ans, mp) -> {
                        mp.entrySet()
                                .stream()
                                .forEach(p -> {
                                    if (ans.get(p.getKey()) == null) {
                                        ans.put(p.getKey(), p.getValue());
                                    } else {
                                        ans.put(p.getKey(), ans.get(p.getKey()) + p.getValue());
                                    }

                                });
                        return ans;});
    }
}
