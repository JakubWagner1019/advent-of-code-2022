package aoc.day04;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        partOne();
        partTwo();
    }

    private static void partOne() throws IOException {
        Path path = Paths.get("day04.data.txt");
        System.out.println(Files.lines(path).filter(Main::oneFullyContainsAnother).count());
    }

    private static void partTwo() throws IOException {
        Path path = Paths.get("day04.data.txt");
        System.out.println(Files.lines(path).filter(Main::rangesOverlap).count());
    }

    private static boolean rangesOverlap(String line) {
        String[] split = line.split(",");
        Range first = Range.from(split[0]);
        Range second = Range.from(split[1]);
        return first.overlaps(second);
    }

    private static boolean oneFullyContainsAnother(String line) {
        String[] split = line.split(",");
        Range first = Range.from(split[0]);
        Range second = Range.from(split[1]);
        return first.fullyContains(second) || second.fullyContains(first);
    }

    private static class Range {
        private final int start;
        private final int end;

        private Range(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public static Range from(String s) {
            String[] split = s.split("-");
            return new Range(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        }

        public boolean fullyContains(Range range) {
            return this.start <= range.start && this.end >= range.end;
        }

        public boolean overlaps(Range range) {
            return this.start <= range.end && this.end >= range.start;
        }
    }
}
