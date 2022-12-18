package aoc.day13;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        partOne();
        partTwo();
    }

    private static void partTwo() throws IOException {
        Path path = Paths.get("day13.data.txt");
        BufferedReader bufferedReader = Files.newBufferedReader(path);
        List<String[]> pairs = new ArrayList<>();
        while (newPair(bufferedReader, pairs)) {
        }

        String dividerOne = "[[2]]";
        String dividerTwo = "[[6]]";
        List<String> orderedPackets = Stream.concat(pairs.stream().flatMap(Arrays::stream), Stream.of(dividerOne, dividerTwo)).map(Main::parse).sorted(Main::compare).map(Object::toString).collect(Collectors.toList());
        int indexOne = orderedPackets.indexOf(dividerOne) + 1;
        int indexTwo = orderedPackets.indexOf(dividerTwo) + 1;
        System.out.println(indexOne * indexTwo);
    }

    private static void partOne() throws IOException {
        Path path = Paths.get("day13.data.txt");
        BufferedReader bufferedReader = Files.newBufferedReader(path);
        List<String[]> pairs = new ArrayList<>();
        while (newPair(bufferedReader, pairs)) {
        }

        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < pairs.size(); i++) {
            if (stringCompare(pairs.get(i)[0], pairs.get(i)[1])) {
                indexes.add(i + 1);
                System.out.println("Pair: " + (i + 1) + " in order: " + pairs.get(i)[0] + " and " + pairs.get(i)[1]);
            } else {
                System.out.println("Pair: " + (i + 1) + " not in order: " + pairs.get(i)[0] + " and " + pairs.get(i)[1]);
            }
        }
        int sum = indexes.stream().mapToInt(Integer::intValue).sum();
        System.out.println(sum);

    }

    private static boolean stringCompare(String first, String second) {
        List<Object> firstParsed = parse(first);
        List<Object> secondParsed = parse(second);
        int compare = compare(firstParsed, secondParsed);
        return compare == -1;
    }

    private static int compare(Object firstParsed, Object secondParsed) {
        if (firstParsed instanceof Integer) {
            int firstVal = (Integer) firstParsed;
            if (secondParsed instanceof Integer) {
                int secondVal = (Integer) secondParsed;
                return Integer.compare(firstVal, secondVal);
            } else if (secondParsed instanceof List) {
                return compare(Collections.singletonList(firstParsed), secondParsed);
            }
        } else if (firstParsed instanceof List) {
            if (secondParsed instanceof Integer) {
                return compare(firstParsed, Collections.singletonList(secondParsed));
            } else if (secondParsed instanceof List) {
                List<Object> firstList = (List<Object>) firstParsed;
                List<Object> secondList = (List<Object>) secondParsed;

                int min = Math.min(firstList.size(), secondList.size());
                for (int i = 0; i < min; i++) {
                    int compare = compare(firstList.get(i), secondList.get(i));
                    if (compare == 0) continue;
                    return compare;
                }

                return Integer.compare(firstList.size(), secondList.size());
            }
        }
        throw new RuntimeException("???");
    }

    private static List<Object> parse(String first) {
        Deque<List<Object>> stack = new ArrayDeque<>();
        stack.push(new ArrayList<>());
        Integer current = null;
        char[] chars = first.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '[') {
                List<Object> newArr = new ArrayList<>();
                stack.peek().add(newArr);
                stack.push(newArr);
            } else if (chars[i] == ']') {
                if (current != null) {
                    stack.peek().add(current);
                    current = null;
                }
                ;
                stack.pop();
            } else if (chars[i] == ',') {
                if (current != null) {
                    stack.peek().add(current);
                    current = null;
                }
            } else if (Character.isDigit(chars[i])) {
                if (current != null) {
                    current *= 10;
                    current += chars[i] - '0';
                } else {
                    current = chars[i] - '0';
                }
            }
        }
        return (List<Object>) stack.pop().get(0);
    }

    private static boolean newPair(BufferedReader bufferedReader, List<String[]> pairs) throws IOException {
        String line;
        boolean found = false;

        while ((line = bufferedReader.readLine()) != null) {
            if (line.startsWith("[")) {
                found = true;
                break;
            }
        }
        if (!found) return false;

        pairs.add(new String[]{line, bufferedReader.readLine()});
        return true;
    }
}
