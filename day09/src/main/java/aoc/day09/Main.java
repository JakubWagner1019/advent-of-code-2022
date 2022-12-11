package aoc.day09;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws IOException {
        partOne();
        partTwo();
    }

    private static void partTwo() throws IOException {
        Path path = Paths.get("day09.data.txt");
        int[][] knotsPositions = new int[10][];
        for (int i = 0; i < knotsPositions.length; i++) {
            knotsPositions[i] = new int[]{0,0};
        }
        int tailIndex = knotsPositions.length - 1;
        Map<Integer, Set<Integer>> visitedPositions = new HashMap<>();
        saveTailPosition(visitedPositions, knotsPositions[tailIndex]); //not needed, after first move tail is always at the same position
        Files.lines(path).map(Move::fromString).forEachOrdered(move -> {
            for (int i = 0; i < move.distance; i++) {
                updateHeadPosition(knotsPositions[0], move.direction);
                for (int j = 1; j < knotsPositions.length; j++) {
                    updateTailPosition(knotsPositions[j-1], knotsPositions[j]);
                }
                saveTailPosition(visitedPositions, knotsPositions[tailIndex]);
            }
        });
        System.out.println(visitedPositions.values().stream().flatMap(Set::stream).count());
    }

    private static void partOne() throws IOException {
        Path path = Paths.get("day09.data.txt");
        int[] headPosition = new int[]{0,0}; //x,y
        int[] tailPosition = new int[]{0,0}; //x,y
        Map<Integer, Set<Integer>> visitedPositions = new HashMap<>();
        saveTailPosition(visitedPositions, tailPosition); //not needed, after first move tail is always at the same position
        Files.lines(path).map(Move::fromString).forEachOrdered(move -> {
            for (int i = 0; i < move.distance; i++) {
                updateHeadPosition(headPosition, move.direction);
                updateTailPosition(headPosition, tailPosition);
                saveTailPosition(visitedPositions, tailPosition);
            }
        });
        System.out.println(visitedPositions.values().stream().flatMap(Set::stream).count());
    }

    private static void saveTailPosition(Map<Integer, Set<Integer>> visitedPositions, int[] tailPosition) {
        visitedPositions.computeIfAbsent(tailPosition[0], v -> new HashSet<>()).add(tailPosition[1]);
    }

    private static void updateTailPosition(int[] headPosition, int[] tailPosition) {
        int dx = headPosition[0] - tailPosition[0];
        int dy = headPosition[1] - tailPosition[1];
        if(Math.abs(dx) == 2 || Math.abs(dy) == 2) {
            tailPosition[0] += signum(dx);
            tailPosition[1] += signum(dy);
        }
    }

    private static int signum(int val) {
        return Integer.compare(val, 0);
    }

    private static void updateHeadPosition(int[] headPosition, Direction direction) {
        headPosition[0] += direction.dx;
        headPosition[1] += direction.dy;
    }

    private enum Direction {
        LEFT(-1, 0),
        UP(0, 1),
        RIGHT(1, 0),
        DOWN(0, -1);
        private final int dx;
        private final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        private static Map<Character, Direction> directionByCharacter = new HashMap<>();

        static {
            directionByCharacter.put('L', LEFT);
            directionByCharacter.put('R', RIGHT);
            directionByCharacter.put('U', UP);
            directionByCharacter.put('D', DOWN);
        }

        public static Direction byCharacter(Character character) {
            return directionByCharacter.get(character);
        }
    }

    private static class Move {
        private final Direction direction;
        private final int distance;

        Move(Direction direction, int distance) {
            this.direction = direction;
            this.distance = distance;
        }

        public static Move fromString(String line) {
            Direction direction = Direction.byCharacter(line.charAt(0));
            int distance = Integer.parseInt(line.substring(2));
            return new Move(direction, distance);
        }

        @Override
        public String toString() {
            return "Move{" +
                    "direction=" + direction +
                    ", distance=" + distance +
                    '}';
        }
    }

}
