package aoc.day11;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        partOne();
        partTwo();
    }

    private static void partOne() throws IOException {
        List<Monkey> monkeyList = getMonkeys();
        long divisorsMultiplied = monkeyList.stream().mapToLong(Monkey::getDivisor).reduce((a, b) -> a * b).getAsLong();
        Monkey.divisorsMultiplied = divisorsMultiplied;
        int rounds = 20;
        for (int i = 0; i < rounds; i++) {
            for (Monkey monkey : monkeyList) {
                monkey.takeTurn(monkeyList, true);
            }
        }
        OptionalInt monkeyBusiness = monkeyList.stream().mapToInt(Monkey::getInspectionCount).map(val -> val * -1).sorted().limit(2).map(val -> val * -1).reduce((v1, v2) -> v1 * v2);
        System.out.println(monkeyBusiness.orElseThrow(RuntimeException::new));
    }

    private static void partTwo() throws IOException {
        List<Monkey> monkeyList = getMonkeys();
        long divisorsMultiplied = monkeyList.stream().mapToLong(Monkey::getDivisor).reduce((a, b) -> a * b).getAsLong();
        Monkey.divisorsMultiplied = divisorsMultiplied;
        int rounds = 10000;
        for (int i = 0; i < rounds; i++) {
            for (Monkey monkey : monkeyList) {
                monkey.takeTurn(monkeyList, false);
            }
        }
        for (Monkey monkey : monkeyList) {
            System.out.println(monkey.getInspectionCount());
        }

        OptionalLong monkeyBusiness = monkeyList.stream().mapToInt(Monkey::getInspectionCount).asLongStream().map(val -> val * -1).sorted().limit(2).map(val -> val * -1).reduce((v1, v2) -> v1 * v2);
        System.out.println(monkeyBusiness.orElseThrow(RuntimeException::new));
    }

    private static List<Monkey> getMonkeys() throws IOException {
        Path path = Paths.get("day11.data.txt");
        List<Monkey> monkeyList = new ArrayList<>();
        BufferedReader bufferedReader = Files.newBufferedReader(path);
        MonkeyParser monkeyParser = new MonkeyParser(monkeyList, bufferedReader);
        monkeyParser.parse();
        bufferedReader.close();
        return monkeyList;
    }

    private static class MonkeyParser {

        private static final String STARTING_ITEMS = "Starting items: ";
        private static final String OPERATION = "Operation: new = ";
        private static final String TEST = "Test: divisible by ";

        private static final String THROW_TO_MONKEY = "throw to monkey ";

        private static final Map<String, LongBinaryOperator> operatorMap = new HashMap<>();
        static {
            operatorMap.put("+", (a,b) -> a+b);
            operatorMap.put("-", (a,b) -> a-b);
            operatorMap.put("*", (a,b) -> a*b);
            operatorMap.put("/", (a,b) -> a/b);
        }
        private final List<Monkey> monkeyList;
        private final BufferedReader bufferedReader;

        private MonkeyBuilder currentMonkey;

        public MonkeyParser(List<Monkey> monkeyList, BufferedReader bufferedReader) {
            this.monkeyList = monkeyList;
            this.bufferedReader = bufferedReader;
        }

        void parse() throws IOException {
            while (newMonkey()) {
                readItems();
                readOperation();
                readDivisor();
                readTestSuccessfulIndex();
                readTestFailedIndex();
                emitMonkey();
            }
        }
        private void readTestSuccessfulIndex() throws IOException {
            String line = bufferedReader.readLine();
            int index = line.indexOf(THROW_TO_MONKEY);
            String strIndex = line.substring(index + THROW_TO_MONKEY.length()).trim();
            int parsed = Integer.parseInt(strIndex);
            currentMonkey.testSuccessfulIndex = parsed;
        }

        private void readTestFailedIndex() throws IOException {
            String line = bufferedReader.readLine();
            int index = line.indexOf(THROW_TO_MONKEY);
            String strIndex = line.substring(index + THROW_TO_MONKEY.length()).trim();
            int parsed = Integer.parseInt(strIndex);
            currentMonkey.testFailedIndex = parsed;
        }

        private void emitMonkey() {
            monkeyList.add(currentMonkey.build());
            currentMonkey = null;
        }

        private void readDivisor() throws IOException {
            String testLine = bufferedReader.readLine();
            int index = testLine.indexOf(TEST);
            String stringDivisor = testLine.substring(index + TEST.length()).trim();
            long parsed = Long.parseLong(stringDivisor);
            currentMonkey.divisor = parsed;
        }

        private void readOperation() throws IOException {
            String operationLine = bufferedReader.readLine();
            int index = operationLine.indexOf(OPERATION);
            String stringOperation = operationLine.substring(index + OPERATION.length());
            LongUnaryOperator operation = parseOperation(stringOperation);
            currentMonkey.operation = operation;
        }

        private LongUnaryOperator parseOperation(String stringOperation) {
            Optional<String> foundOperator = operatorMap.keySet().stream().filter(op -> stringOperation.contains(op)).findFirst();
            if (!foundOperator.isPresent()) {
                throw new RuntimeException("Operator not found in "+stringOperation);
            }
            String operator = foundOperator.get();
            LongBinaryOperator longBinaryOperator = operatorMap.get(operator);
            String leftSide = stringOperation.substring(0, stringOperation.indexOf(operator)).trim();
            LongUnaryOperator leftValue = getLongUnaryOperator(leftSide);
            String rightSide = stringOperation.substring(stringOperation.indexOf(operator) + operator.length()).trim();
            LongUnaryOperator rightValue = getLongUnaryOperator(rightSide);
            return val -> longBinaryOperator.applyAsLong(leftValue.applyAsLong(val), rightValue.applyAsLong(val));
        }

        private static LongUnaryOperator getLongUnaryOperator(String leftSide) {
            LongUnaryOperator longUnaryOperator;
            if(leftSide.equals("old")) {
                longUnaryOperator = val -> val;
            } else {
                long parsed = Long.parseLong(leftSide);
                longUnaryOperator = val -> parsed;
            }
            return longUnaryOperator;
        }

        private void readItems() throws IOException {
            String itemsLine = bufferedReader.readLine();
            int index = itemsLine.indexOf(STARTING_ITEMS);
            String[] stringItems = itemsLine.substring(index + STARTING_ITEMS.length()).split(",");
            List<Long> items = Arrays.stream(stringItems).map(String::trim).mapToLong(Long::parseLong).boxed().collect(Collectors.toList());
            currentMonkey.items = items;
        }

        private boolean newMonkey() throws IOException {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("Monkey")) {
                    currentMonkey = new MonkeyBuilder();
                    return true;
                }
            }
            return false;
        }

        private static class MonkeyBuilder {
            private List<Long> items;
            private LongUnaryOperator operation;
            private long divisor;
            private int testSuccessfulIndex;
            private int testFailedIndex;

            public Monkey build() {
                return new Monkey(items, operation, divisor, testSuccessfulIndex, testFailedIndex);
            }
        }
    }

    private static class Monkey {

        static long divisorsMultiplied = 0;
        private final Deque<Long> items = new ArrayDeque<>();
        private final LongUnaryOperator operation;
        private final long divisor;
        private final int testSuccessfulIndex;
        private final int testFailedIndex;

        private int inspectionCount = 0;

        private Monkey(List<Long> items, LongUnaryOperator operation, long divisor, int testSuccessfulIndex, int testFailedIndex) {
            this.items.addAll(items);
            this.operation = operation;
            this.divisor = divisor;
            this.testSuccessfulIndex = testSuccessfulIndex;
            this.testFailedIndex = testFailedIndex;
        }

        public void takeTurn(List<Monkey> monkeyList, boolean partOne) {
            Long item;
            while ((item = items.poll()) != null) {
                long afterInspection = operation.applyAsLong(item);
                inspectionCount++;
                long reduced;
                if(partOne) {
                    reduced = reducePartOne(afterInspection);
                    reduced = reducePartTwo(reduced);
                } else {
                    reduced = reducePartTwo(afterInspection);
                }
                Monkey monkey;
                if (reduced % divisor == 0) {
                    monkey = monkeyList.get(testSuccessfulIndex);
                } else {
                    monkey = monkeyList.get(testFailedIndex);
                }
                monkey.items.add(reduced);
            }
        }

        public int getInspectionCount() {
            return inspectionCount;
        }

        public long getDivisor() {
            return divisor;
        }
    }

    private static long reducePartOne(long val) {
        return worryDecrease.applyAsLong(val);
    }

    private static long reducePartTwo(long val) {
        return val % Monkey.divisorsMultiplied;
    }
    private static final LongUnaryOperator worryDecrease = val -> Math.floorDiv(val, 3L);
}
