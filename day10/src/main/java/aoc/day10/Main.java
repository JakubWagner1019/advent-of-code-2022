package aoc.day10;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        partOne();
        partTwo();
    }

    private static void partTwo() throws IOException {
        CRT crt = new CRT();
        Path path = Paths.get("day10.data.txt");
        for (String line : Files.readAllLines(path)) {
            if (line.startsWith("noop")) {
                crt.noop();
            } else if (line.startsWith("addx")) {
                crt.addx(Integer.parseInt(line.substring(5)));
            }
            crt.applyRegisterChange();
        }
        System.out.println(crt.prettyPrint());
    }

    private static class CPU {
        protected int cycle = 0;
        protected int register = 1;
        private int registerChange;

        void noop() {
            nextCycle();
        }

        protected void nextCycle() {
            cycle += 1;
        }

        void addx(int val) {
            nextCycle();
            nextCycle();
            registerChange = val;
        }

        void applyRegisterChange() {
            register += registerChange;
            registerChange = 0;
        }

        public int getCycle() {
            return cycle;
        }

        public int getRegister() {
            return register;
        }
    }

    private static class CRT extends CPU {
        boolean[] pixels = new boolean[40 * 6];

        @Override
        protected void nextCycle() {
            if (cycle < pixels.length) {
                pixels[cycle] = Math.abs(register - cycle % 40) <= 1;
            }
            super.nextCycle();
        }

        public String prettyPrint() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 40; j++) {
                    if (pixels[i * 40 + j]) {
                        sb.append('#');
                    } else {
                        sb.append('.');
                    }
                }
                sb.append('\n');
            }
            return sb.toString();
        }
    }


    private static void partOne() throws IOException {
        CPU cpu = new CPU();
        int sum = 0;
        int[] checkpoints = new int[]{20, 60, 100, 140, 180, 220};
        int nextCheckpoint = 0;
        Path path = Paths.get("day10.data.txt");
        for (String line : Files.readAllLines(path)) {
            if (line.startsWith("noop")) {
                cpu.noop();
            } else if (line.startsWith("addx")) {
                cpu.addx(Integer.parseInt(line.substring(5)));
            }
            if (cpu.getCycle() >= checkpoints[nextCheckpoint]) {
                sum += checkpoints[nextCheckpoint] * cpu.getRegister();
                System.out.printf("%d * %d = %d\n", checkpoints[nextCheckpoint], cpu.getRegister(), checkpoints[nextCheckpoint] * cpu.getRegister());
                if (++nextCheckpoint == checkpoints.length) break;
            }
            cpu.applyRegisterChange();
        }
        System.out.println(sum);
    }
}
