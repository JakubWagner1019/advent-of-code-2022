package aoc.day05;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("day05.data.txt");
        BufferedReader bufferedReader = Files.newBufferedReader(path);
        String line;
        boolean readingStacks = true;
        List<String> stacksDiagram = new ArrayList<>();
        List<Move> moves = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            if (line.isEmpty()) {
                readingStacks = false;
                continue;
            }

            if (readingStacks) {
                stacksDiagram.add(line);
            } else {
                moves.add(Move.from(line));
            }
        }
        List<Deque<Character>> stacks = parseStacks(stacksDiagram);

        //partOne(moves, stacks);
        partTwo(moves, stacks);
    }

    private static void partTwo(List<Move> moves, List<Deque<Character>> stacks) {
        for (Move move : moves) {
            move.withCraneMover9001(stacks);
        }
        StringBuilder sb = new StringBuilder();
        for (Deque<Character> stack : stacks) {
            sb.append(stack.pop());
        }
        System.out.println(sb);
    }

    private static void partOne(List<Move> moves, List<Deque<Character>> stacks) {
        for (Move move : moves) {
            move.withCraneMover9000(stacks);
        }
        StringBuilder sb = new StringBuilder();
        for (Deque<Character> stack : stacks) {
            sb.append(stack.pop());
        }
        System.out.println(sb);
    }

    private static List<Deque<Character>> parseStacks(List<String> stacksDiagram) {
        String lastLine = stacksDiagram.get(stacksDiagram.size() - 1);
        List<Integer> stackIndexes = getIndexes(lastLine);
        List<Deque<Character>> stacks = new ArrayList<>();
        for (int i = 0; i < stackIndexes.size(); i++) {
            stacks.add(new ArrayDeque<>());
        }

        for (int i = stacksDiagram.size() - 2; i >= 0; i--) {
            String line = stacksDiagram.get(i);
            for (int i1 = 0; i1 < stackIndexes.size(); i1++) {
                int index = stackIndexes.get(i1);
                if (index > line.length()) {
                    continue;
                }
                char charAt = line.charAt(index);
                if (Character.isWhitespace(charAt)) {
                    continue;
                }
                stacks.get(i1).push(charAt);
            }
        }
        return stacks;
    }

    private static List<Integer> getIndexes(String lastLine) {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < lastLine.toCharArray().length; i++) {
            if (!Character.isWhitespace(lastLine.charAt(i))) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    private static class Move {

        private static final Pattern INTEGER = Pattern.compile("\\d+");
        private final int count;
        private final int from;
        private final int to;

        private Move(int count, int from, int to) {
            this.count = count;
            this.from = from;
            this.to = to;
        }

        public static Move from(String line) {
            int[] vals = new int[3];
            int start = 0;
            Matcher matcher = INTEGER.matcher(line);
            int valIndex = 0;
            while (matcher.find(start)) {
                vals[valIndex++] = Integer.parseInt(matcher.group());
                start = matcher.end();
            }
            return new Move(vals[0], vals[1], vals[2]);
        }

        public void withCraneMover9000(List<Deque<Character>> stacks) {
            Deque<Character> fromStack = stacks.get(from - 1);
            Deque<Character> toStack = stacks.get(to - 1);
            for (int i = 0; i < count; i++) {
                toStack.push(fromStack.pop());
            }
        }

        public void withCraneMover9001(List<Deque<Character>> stacks) {
            Deque<Character> fromStack = stacks.get(from - 1);
            Deque<Character> crane = new ArrayDeque<>();
            Deque<Character> toStack = stacks.get(to - 1);
            for (int i = 0; i < count; i++) {
                crane.push(fromStack.pop());
            }
            for (int i = 0; i < count; i++) {
                toStack.push(crane.pop());
            }
        }
    }
}
