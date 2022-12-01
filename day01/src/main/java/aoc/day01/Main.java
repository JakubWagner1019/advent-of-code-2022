package aoc.day01;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

public class Main {
    public static void main(String[] args) {
        partOne();
        partTwo();
    }

    private static void partOne() {
        Path path = Paths.get("day01.data.txt");
        List<Integer> calories = new ArrayList<>();
        calories.add(0);

        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty()) {
                    calories.add(0);
                    continue;
                }
                int count = Integer.parseUnsignedInt(line);
                Integer current = calories.get(calories.size() - 1);
                current = current + count;
                calories.set(calories.size() - 1, current);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        OptionalInt optionalInt = calories.stream().mapToInt(Integer::intValue).max();
        optionalInt.ifPresent(System.out::println);
    }

    private static void partTwo() {
        Path path = Paths.get("day01.data.txt");
        List<Integer> calories = new ArrayList<>();
        calories.add(0);

        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty()) {
                    calories.add(0);
                    continue;
                }
                int count = Integer.parseUnsignedInt(line);
                Integer current = calories.get(calories.size() - 1);
                current = current + count;
                calories.set(calories.size() - 1, current);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int sumOfTopThree = calories.stream().mapToInt(Integer::intValue)
                .map(i -> i * -1) //there's no 'IntStream.reverse' method, so first I'm sorting negative valuess
                .sorted()
                .limit(3)
                .map(i -> i * -1)
                .sum();
        System.out.println(sumOfTopThree);
    }
}
