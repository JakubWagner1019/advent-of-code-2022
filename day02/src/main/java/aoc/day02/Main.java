package aoc.day02;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {
        partOne();
        partTwo();
    }

    private static void partOne() throws IOException {
        Path path = Paths.get("day02.data.txt");
        Map<String, Shape> enemyMovesMap = new HashMap<>();
        enemyMovesMap.put("A", Shape.ROCK);
        enemyMovesMap.put("B", Shape.PAPER);
        enemyMovesMap.put("C", Shape.SCISSORS);
        Map<String, Shape> myMovesMap = new HashMap<>();
        myMovesMap.put("X", Shape.ROCK);
        myMovesMap.put("Y", Shape.PAPER);
        myMovesMap.put("Z", Shape.SCISSORS);
        List<Shape> enemyMoves = new ArrayList<>();
        List<Shape> myMoves = new ArrayList<>();
        Files.lines(path).forEachOrdered(line -> {
            String[] s = line.trim().split(" ");
            if (s.length != 2) {
                throw new RuntimeException("Error parsing line " + line);
            }
            enemyMoves.add(enemyMovesMap.get(s[0]));
            myMoves.add(myMovesMap.get(s[1]));
        });
        assert enemyMoves.size() == myMoves.size();
        int score = 0;
        for (int i = 0; i < myMoves.size(); i++) {
            Shape enemyMove = enemyMoves.get(i);
            Shape myMove = myMoves.get(i);
            score += playRound(enemyMove, myMove);
        }
        System.out.println(score);
    }

    private static void partTwo() throws IOException {
        Path path = Paths.get("day02.data.txt");
        Map<String, Shape> enemyMovesMap = new HashMap<>();
        enemyMovesMap.put("A", Shape.ROCK);
        enemyMovesMap.put("B", Shape.PAPER);
        enemyMovesMap.put("C", Shape.SCISSORS);
        Map<String, Result> resultMap = new HashMap<>();
        resultMap.put("X", Result.LOSE);
        resultMap.put("Y", Result.DRAW);
        resultMap.put("Z", Result.WIN);
        List<Shape> enemyMoves = new ArrayList<>();
        List<Result> results = new ArrayList<>();
        Files.lines(path).forEachOrdered(line -> {
            String[] s = line.trim().split(" ");
            if (s.length != 2) {
                throw new RuntimeException("Error parsing line " + line);
            }
            enemyMoves.add(enemyMovesMap.get(s[0]));
            results.add(resultMap.get(s[1]));
        });
        assert enemyMoves.size() == results.size();
        int score = 0;
        for (int i = 0; i < enemyMoves.size(); i++) {
            Shape enemyMove = enemyMoves.get(i);
            Result result = results.get(i);
            Shape myMove = determineMyShape(enemyMove, result);
            score += playRound(enemyMove, myMove);
        }
        System.out.println(score);
    }

    private static Shape determineMyShape(Shape enemyShape, Result result) {
        switch (result) {
            case DRAW:
                return enemyShape;
            case WIN:
                return enemyShape.losesTo();
            case LOSE:
                return enemyShape.defeats();
            default:
                throw new IllegalArgumentException("No such shape " + enemyShape);
        }
    }
    private static int playRound(Shape enemyMove, Shape myMove) {
        int fight = myMove.fight(enemyMove);
        return fight + myMove.getScore();
    }
    private static enum Result {
        LOSE,
        DRAW,
        WIN;

    }

    private static enum Shape {
        ROCK(1),
        PAPER(2),
        SCISSORS(3);

        private static final Map<Shape, Shape> DEFEATS_MAP = new HashMap<>();

        static {
            DEFEATS_MAP.put(ROCK, SCISSORS);
            DEFEATS_MAP.put(SCISSORS, PAPER);
            DEFEATS_MAP.put(PAPER, ROCK);
        }

        private static final Map<Shape, Shape> LOSES_MAP = new HashMap<>();

        static {
            LOSES_MAP.put(ROCK, PAPER);
            LOSES_MAP.put(SCISSORS, ROCK);
            LOSES_MAP.put(PAPER, SCISSORS);
        }

        private final int score;

        Shape(int score) {
            this.score = score;
        }

        public int getScore() {
            return score;
        }

        public int fight(Shape otherShape) {
            if (this == otherShape) return 3;
            if (DEFEATS_MAP.get(this) == otherShape) {
                return 6;
            }
            return 0;
        }

        public Shape defeats() {
            return DEFEATS_MAP.get(this);
        }

        public Shape losesTo() {
            return LOSES_MAP.get(this);
        }
    }
}
