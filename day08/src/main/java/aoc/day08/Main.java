package aoc.day08;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        partOne();
        partTwo();
    }

    private static void partOne() throws IOException {
        int[][] trees = getTrees();
        boolean[][] visible = countVisibleTrees(trees);
        System.out.println(Arrays.stream(visible).mapToInt(bArr -> {
            int sum = 0;
            for (boolean b : bArr) {
                if (b) {
                    sum += 1;
                }
            }
            return sum;
        }).sum());
    }

    private static int[][] getTrees() throws IOException {
        Path path = Paths.get("day08.data.txt");
        int[][] trees = Files.lines(path).map(s -> {
            char[] chars = s.toCharArray();
            int[] ints = new int[chars.length];
            for (int i = 0; i < chars.length; i++) {
                ints[i] = chars[i] - '0';
            }
            return ints;
        }).collect(Collectors.toList()).toArray(new int[0][]);
        return trees;
    }

    private static boolean[][] countVisibleTrees(int[][] trees) {
        boolean[][] visible = initBooleanMap(trees, false);
        int xSize = trees[0].length;

        markVisibleFromLeft(trees, visible);
        markVisibleFromUp(trees, visible, xSize);
        markVisibleFromRight(trees, visible);
        markVisibleFromBottom(trees, visible, xSize);

        return visible;
    }

    private static void markVisibleFromLeft(int[][] trees, boolean[][] visible) {
        for (int y = 0; y < trees.length; y++) {
            int current = -1;
            for (int x = 0; x < trees[y].length; x++) {
                if (trees[y][x] > current) {
                    current = trees[y][x];
                    visible[y][x] = true;
                }
            }
        }
    }

    private static void markVisibleFromUp(int[][] trees, boolean[][] visible, int xSize) {
        for (int x = 0; x < xSize; x++) {
            int current = -1;
            for (int y = 0; y < trees.length; y++) {
                if (trees[y][x] > current) {
                    current = trees[y][x];
                    visible[y][x] = true;
                }
            }
        }
    }

    private static void markVisibleFromRight(int[][] trees, boolean[][] visible) {
        for (int y = 0; y < trees.length; y++) {
            int current = -1;
            for (int x = trees[y].length - 1; x >= 0; x--) {
                if (trees[y][x] > current) {
                    current = trees[y][x];
                    visible[y][x] = true;
                }
            }
        }
    }

    private static void markVisibleFromBottom(int[][] trees, boolean[][] visible, int xSize) {
        for (int x = 0; x < xSize; x++) {
            int current = -1;
            for (int y = trees.length - 1; y >= 0; y--) {
                if (trees[y][x] > current) {
                    current = trees[y][x];
                    visible[y][x] = true;
                }
            }
        }
    }


    private static boolean[][] initBooleanMap(int[][] trees, boolean startValue) {
        boolean[][] map = new boolean[trees.length][];
        for (int i = 0; i < map.length; i++) {
            map[i] = new boolean[trees[i].length];
            if (startValue) {
                Arrays.fill(map[i], true);
            }
        }
        return map;
    }

    private static void partTwo() throws IOException {
        int[][] trees = getTrees();
        long[][] scenicScore = countScenicScores(trees);
        System.out.println(Arrays.stream(scenicScore).flatMapToLong(Arrays::stream).max());
    }

    private static long[][] countScenicScores(int[][] trees) {
        long[][] scenicScores = initScenicScoresMap(trees);
        int xSize = trees[0].length;

        updateScenicScoreFromLeft(trees, scenicScores);
        updateScenicScoreFromRight(trees, scenicScores);
        updateScenicScoreFromUp(trees, scenicScores, xSize);
        updateScenicScoreFromBottom(trees, scenicScores, xSize);
        return scenicScores;
    }

    private static void updateScenicScoreFromBottom(int[][] trees, long[][] scenicScores, int xSize) {
        for (int x = 0; x < xSize; x++) {
            long[] lastSeen = new long[10]; //0 to 9
            Arrays.fill(lastSeen, -1L);
            for (int y = trees.length - 1; y >= 0; y--) {
                incrementLastSeenDistances(lastSeen);
                long scenicScore = getScenicScore(trees[y][x], lastSeen, trees.length - 1 - y);
                scenicScores[y][x] *= scenicScore;
                updateLastSeenWithCurrentHeight(lastSeen, trees[y][x]);
            }
        }
    }

    private static void updateScenicScoreFromUp(int[][] trees, long[][] scenicScores, int xSize) {
        for (int x = 0; x < xSize; x++) {
            long[] lastSeen = new long[10]; //0 to 9
            Arrays.fill(lastSeen, -1L);
            for (int y = 0; y < trees.length; y++) {
                incrementLastSeenDistances(lastSeen);
                long scenicScore = getScenicScore(trees[y][x], lastSeen, y);
                scenicScores[y][x] *= scenicScore;
                updateLastSeenWithCurrentHeight(lastSeen, trees[y][x]);
            }
        }
    }

    private static void updateScenicScoreFromRight(int[][] trees, long[][] scenicScores) {
        for (int y = 0; y < trees.length; y++) {
            long[] lastSeen = new long[10]; //0 to 9
            Arrays.fill(lastSeen, -1L);
            for (int x = trees[y].length - 1; x >= 0; x--) {
                incrementLastSeenDistances(lastSeen);
                long scenicScore = getScenicScore(trees[y][x], lastSeen, trees[y].length - 1 - x);
                scenicScores[y][x] *= scenicScore;
                updateLastSeenWithCurrentHeight(lastSeen, trees[y][x]);
            }
        }
    }

    private static void updateScenicScoreFromLeft(int[][] trees, long[][] scenicScores) {
        for (int y = 0; y < trees.length; y++) {
            long[] lastSeen = new long[10]; //0 to 9
            Arrays.fill(lastSeen, -1L);
            for (int x = 0; x < trees[y].length; x++) {
                incrementLastSeenDistances(lastSeen);
                long scenicScore = getScenicScore(trees[y][x], lastSeen, x);
                scenicScores[y][x] *= scenicScore;
                updateLastSeenWithCurrentHeight(lastSeen, trees[y][x]);
            }
        }
    }

    private static void updateLastSeenWithCurrentHeight(long[] lastSeen, int currentHeight){
        for (int i = 0; i < currentHeight; i++) {
            lastSeen[i] = -1;
        }
        lastSeen[currentHeight] = 0;
    }

    private static long getScenicScore(int currentHeight, long[] lastSeen, int valueWhenViewNotBlocked) {
        for (int i = currentHeight; i <= 9; i++) {
            long dist = lastSeen[i];
            if (dist != -1) {
                return dist;
            }
        }
        return valueWhenViewNotBlocked;
    }

    private static void incrementLastSeenDistances(long[] lastSeen) {
        for (int i = 0; i < lastSeen.length; i++) {
            if (lastSeen[i] != -1) {
                lastSeen[i] += 1;
            }
        }
    }

    private static long[][] initScenicScoresMap(int[][] trees) {
        long[][] longMap = new long[trees.length][];
        for (int i = 0; i < trees.length; i++) {
            longMap[i] = new long[trees[i].length];
            Arrays.fill(longMap[i], 1); //1 as the neutral element of multiplication
        }
        return longMap;
    }


}
