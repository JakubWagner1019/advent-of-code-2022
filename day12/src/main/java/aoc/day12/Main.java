package aoc.day12;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) throws IOException {
        partOne();
        partTwo();
    }

    private static void partTwo() throws IOException {
        Path path = Paths.get("day12.data.txt");
        List<String> lines = Files.readAllLines(path);
        List<int[]> startingPositions = new ArrayList<>();
        int[] finishPosition = null;
        int[][] heightmap = new int[lines.size()][];
        for (int y = 0; y < lines.size(); y++) {
            char[] charArray = lines.get(y).toCharArray();
            heightmap[y] = new int[charArray.length];
            for (int x = 0; x < charArray.length; x++) {
                if (charArray[x] == 'S') {
                    heightmap[y][x] = 'a' - 'a';
                } else if (charArray[x] == 'E') {
                    finishPosition = new int[]{x, y};
                    heightmap[y][x] = 'z' - 'a';
                } else {
                    heightmap[y][x] = charArray[x] - 'a';
                }
                if (heightmap[y][x] == 0) {
                    startingPositions.add(new int[]{x, y});
                }
            }
        }
        assert !startingPositions.isEmpty();
        assert finishPosition != null;

        int[] finalFinishPosition = finishPosition;
        System.out.println(
                startingPositions.stream()
                        .mapToInt(start -> getStepsRequired(start, finalFinishPosition, heightmap))
                        .filter(val -> val > 0)
                        .min()
        );
    }

    private static void partOne() throws IOException {
        Path path = Paths.get("day12.data.txt");
        List<String> lines = Files.readAllLines(path);
        int[] startingPosition = null; // {x,y}
        int[] finishPosition = null;
        int[][] heightmap = new int[lines.size()][];
        for (int y = 0; y < lines.size(); y++) {
            char[] charArray = lines.get(y).toCharArray();
            heightmap[y] = new int[charArray.length];
            for (int x = 0; x < charArray.length; x++) {
                if (charArray[x] == 'S') {
                    startingPosition = new int[]{x, y};
                    heightmap[y][x] = 'a' - 'a';
                } else if (charArray[x] == 'E') {
                    finishPosition = new int[]{x, y};
                    heightmap[y][x] = 'z' - 'a';
                } else {
                    heightmap[y][x] = charArray[x] - 'a';
                }
            }
        }
        assert startingPosition != null;
        assert finishPosition != null;

        int stepsRequired = getStepsRequired(startingPosition, finishPosition, heightmap);
        System.out.println(stepsRequired);
    }

    private static int getStepsRequired(int[] startingPosition, int[] finishPosition, int[][] heightmap) {
        int[][] cost = new int[heightmap.length][];
        for (int i = 0; i < cost.length; i++) {
            cost[i] = new int[heightmap[i].length];
            Arrays.fill(cost[i], -1);
        }

        cost[startingPosition[1]][startingPosition[0]] = 0;

        int ySize = heightmap.length;
        int xSize = heightmap[0].length;

        Deque<int[]> positionsToCheck = new ArrayDeque<>();
        positionsToCheck.add(startingPosition);
        int[] currentPosition;
        while ((currentPosition = positionsToCheck.poll()) != null) {
            int x = currentPosition[0];
            int y = currentPosition[1];
            int currentHeight = heightmap[y][x];
            int currentCost = cost[y][x];
            //left
            if (x > 0) {
                int nX = x - 1;
                int nY = y;
                Optional<int[]> neighbourToCheck = neighbourToCheck(heightmap, cost, x, y, nX, nY);
                neighbourToCheck.ifPresent(positionsToCheck::add);
            }
            //up
            if (y > 0) {
                int nX = x;
                int nY = y - 1;
                Optional<int[]> neighbourToCheck = neighbourToCheck(heightmap, cost, x, y, nX, nY);
                neighbourToCheck.ifPresent(positionsToCheck::add);
            }
            //right
            if (x < xSize - 1) {
                int nX = x + 1;
                int nY = y;
                Optional<int[]> neighbourToCheck = neighbourToCheck(heightmap, cost, x, y, nX, nY);
                neighbourToCheck.ifPresent(positionsToCheck::add);
            }
            //down
            if (y < ySize - 1) {
                int nX = x;
                int nY = y + 1;
                Optional<int[]> neighbourToCheck = neighbourToCheck(heightmap, cost, x, y, nX, nY);
                neighbourToCheck.ifPresent(positionsToCheck::add);
            }
        }

        int stepsRequired = cost[finishPosition[1]][finishPosition[0]];
        return stepsRequired;
    }

    private static Optional<int[]> neighbourToCheck(int[][] heightmap, int[][] cost, int x, int y, int nX, int nY) {
        int neighbourHeight = heightmap[nY][nX];
        int currentHeight = heightmap[y][x];
        int currentCost = cost[y][x];
        if (isReachable(neighbourHeight, currentHeight) &&
                (cost[nY][nX] == -1 || cost[nY][nX] > currentCost + 1)) {
            cost[nY][nX] = currentCost + 1;
            return Optional.of(new int[]{nX, nY});
        }
        return Optional.empty();
    }

    private static boolean isReachable(int neighbourHeight, int currentHeight) {
        return neighbourHeight <= currentHeight + 1;
    }
}
