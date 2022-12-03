package aoc.day03;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {
        partOne();
        partTwo();
    }

    private static void partTwo() throws IOException {
        Path path = Paths.get("day03.data.txt");
        BufferedReader bufferedReader = Files.newBufferedReader(path);
        List<Integer> groupPriorities = new ArrayList<>();
        String line;
        List<String> lines = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
            if (lines.size() == 3) {
                Integer next = lines.stream().map(Main::getItems).reduce(Main::intersection).get().iterator().next();
                groupPriorities.add(next);
                lines.clear();
            }
        }
        System.out.println(groupPriorities.stream().mapToInt(Integer::intValue).sum());
    }

    private static void partOne() throws IOException {
        Path path = Paths.get("day03.data.txt");
        System.out.println(Files.lines(path).mapToInt(Main::prioritizeRearrangement).sum());
    }

    private static int prioritizeRearrangement(String line) {
        int length = line.length();
        assert length % 2 == 0;
        int half = length / 2;
        String firstHalf = line.substring(0, half);
        String secondHalf = line.substring(half);
        Set<Integer> firstCompartment = getItems(firstHalf);
        Set<Integer> secondCompartment = getItems(secondHalf);
        return intersection(firstCompartment, secondCompartment).iterator().next();
    }

    private static Set<Integer> intersection(Set<Integer> firstSet, Set<Integer> otherSet) {
        Set<Integer> intersection = new HashSet<>(firstSet);
        intersection.retainAll(otherSet);
        return intersection;
    }

    private static Set<Integer> getItems(String compartment) {
        Set<Integer> set = new HashSet<>();
        for (char c : compartment.toCharArray()) {
            set.add(itemPriority(c));
        }
        return set;
    }

    private static int itemPriority(char item) {
        if (Character.isUpperCase(item)) {
            return item - 'A' + 27;
        } else if (Character.isLowerCase(item)) {
            return item - 'a' + 1;
        } else {
            throw new RuntimeException("Didn't predict char " + item);
        }
    }
}
