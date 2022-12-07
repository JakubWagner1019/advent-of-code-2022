package aoc.day07;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {
        partOne();
        partTwo();
    }

    private static void partTwo() throws IOException {
        Path path = Paths.get("day07.data.txt");
        List<String> lines = Files.readAllLines(path);
        Map<String, Long> dirSize = new HashMap<>();
        Map<String, Set<String>> childrenDirs = new HashMap<>();
        commonPart(lines, dirSize, childrenDirs);
        Map<String, Long> totalSize = countTotalSize(dirSize, childrenDirs);
        long totalMemory = 70_000_000L;
        long requiredMemoryForUpdate = 30_000_000L;
        long usedMemory = totalSize.get("/");
        long unusedMemory = totalMemory - usedMemory;
        long additionalMemoryNeeded = requiredMemoryForUpdate - unusedMemory;
        System.out.println(totalSize.values().stream().mapToLong(Long::longValue).filter(val -> val > additionalMemoryNeeded).sorted().findFirst().getAsLong());
    }

    private static void partOne() throws IOException {
        Path path = Paths.get("day07.data.txt");
        List<String> lines = Files.readAllLines(path);
        Map<String, Long> dirSize = new HashMap<>();
        Map<String, Set<String>> childrenDirs = new HashMap<>();
        commonPart(lines, dirSize, childrenDirs);
        Map<String, Long> totalSize = countTotalSize(dirSize, childrenDirs);
        System.out.println(totalSize.values().stream().mapToLong(Long::longValue).filter(val -> val > 100_000).sum());
    }

    private static void commonPart(List<String> lines, Map<String, Long> dirSize, Map<String, Set<String>> childrenDirs) {
        ChangeDirectory cd = new ChangeDirectory();
        boolean ignoreFiles = false;
        for (String line : lines) {
            if (line.startsWith("$")) {
                String command = line.substring(2); //remove dollar and whitespace
                if (command.startsWith("ls")) {
                    Long alreadyMeasured = dirSize.putIfAbsent(cd.getCurrent(), 0L);
                    ignoreFiles = alreadyMeasured != null;
                } else if (command.startsWith("cd")) {
                    String directory = command.substring(3).trim(); //remove 'cd' and whitespace
                    if (directory.equals("..")) {
                        cd.goUp();
                    } else if (directory.equals("/")) {
                        cd.goRoot();
                    } else {
                        String parent = cd.getCurrent();
                        cd.goTo(directory);
                        String child = cd.getCurrent();
                        childrenDirs.computeIfAbsent(parent, s -> new HashSet<>()).add(child);
                    }
                }
            } else {
                if (ignoreFiles) continue;

                if (line.startsWith("dir")) {
                    continue;
                }

                int whiteSpaceIndex = line.indexOf(" ");
                long size = Long.parseLong(line.substring(0, whiteSpaceIndex));
                dirSize.merge(cd.getCurrent(), size, Long::sum);
            }
        }
    }

    private static Map<String, Long> countTotalSize(Map<String, Long> dirSize, Map<String, Set<String>> childrenDirs) {
        Map<String, Long> totalSize = new HashMap<>();
        countTotalSizeRecursively("/", dirSize, childrenDirs, totalSize);
        return totalSize;
    }

    private static Long countTotalSizeRecursively(String currentDir, Map<String, Long> dirSize, Map<String, Set<String>> childrenDirs, Map<String, Long> totalSize) {
        Long result = totalSize.get(currentDir);
        if (result != null) return result;

        result = dirSize.getOrDefault(currentDir, 0L);

        Set<String> children = childrenDirs.get(currentDir);
        if (children != null) {
            for (String child : children) {
                result += countTotalSizeRecursively(child, dirSize, childrenDirs, totalSize);
            }
        }

        totalSize.put(currentDir, result);
        return result;
    }

    private static class ChangeDirectory {

        private final List<String> directory = new ArrayList<>();

        void goUp() {
            directory.remove(directory.size() - 1);
        }

        void goTo(String dir) {
            directory.add(dir);
        }

        void goRoot() {
            directory.clear();
        }

        String getCurrent() {
            return "/" + String.join("/", directory);
        }
    }
}
