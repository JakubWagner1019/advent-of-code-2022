package aoc.day06;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        partOne();
        partTwo();
    }

    private static void partOne() throws IOException {
        Path path = Paths.get("day06.data.txt");
        Files.lines(path).mapToInt(line -> getMarker(line, 4)).forEach(System.out::println);
    }

    private static void partTwo() throws IOException {
        Path path = Paths.get("day06.data.txt");
        Files.lines(path).mapToInt(line -> getMarker(line, 14)).forEach(System.out::println);
    }

    private static int getMarker(String line, int markerLength) {
        Map<Character, Integer> counter = new HashMap<>();
        Deque<Character> characters = new ArrayDeque<>();
        int index = 0;
        for (char c : line.toCharArray()) {
            characters.add(c);
            counter.merge(c, 1, Integer::sum);
            if(characters.size() == markerLength) {
                if(counter.keySet().size() == markerLength) {
                    return index + 1;
                }
                Character poll = characters.poll();
                Integer afterRemoving = counter.merge(poll, -1, Integer::sum);
                if(afterRemoving == 0) {
                    counter.remove(poll);
                }
            }
            index++;
        }
        return index;
    }
}
