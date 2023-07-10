package com.efimchick.ifmo.io.filetree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FileTreeImpl implements FileTree {

    private static final String BYTES = "bytes";
    private static final String WHITESPACE = " ";
    private static final String IN_LIST = "├─ ";
    private static final String LAST_IN_LIST = "└─ ";
    private static final char BRANCH = '│';
    private static final char OPENING_BRANCH = '├';
    private static final char CLOSING_BRANCH = '└';
    private static final char WHITESPACE_CHAR = ' ';
    private static final String INDENTATION = "   ";
    private static final String LINE_FEED = "\n";
    private static final int NO_NESTING = 0;
    private final Set<Integer> openedBranches = new HashSet<>();

    @Override
    public Optional<String> tree(Path path) {
        if (path == null || Files.notExists(path)) {
            return Optional.empty();
        } else {
            StringBuilder output = new StringBuilder();
            PrintPath printPath = wrapPath(path, NO_NESTING);
            getTree(printPath, output);
            return Optional.of(output.toString());
        }
    }

    private void getTree(PrintPath printPath, StringBuilder output) {
        if (Files.isRegularFile(printPath.path())) {
            getFileOutputString(printPath, output);
        } else if (Files.isDirectory(printPath.path())) {
            getFileOutputString(printPath, output);
            List<Path> paths = getFilesListSorted(printPath.path());
            List<PrintPath> printPaths = warpList(paths, printPath.nesting() + 1);
            for (PrintPath file : printPaths) {
                getTree(file, output);
            }
        }
    }

    private PrintPath wrapPath(Path path, int nesting) {
        return new PrintPath(path, nesting);
    }

    private List<PrintPath> warpList(List<Path> paths, int nesting) {
        List<PrintPath> printPaths = new ArrayList<>();
        for (Path path : paths) {
            printPaths.add(wrapPath(path, nesting));
        }
        markLastElement(printPaths);
        return printPaths;
    }

    private void markLastElement(List<PrintPath> printPaths) {
        printPaths.get(printPaths.size() - 1).makeLastInList();
    }

    private List<Path> getFilesListSorted(Path path) {
        List<Path> files = new ArrayList<>();
        try {
            files = Files.list(path)
                    .sorted((p1, p2) -> p1.getFileName().toString()
                            .compareToIgnoreCase(p2.getFileName().toString()))
                    .sorted((p1, p2) -> {
                        int p1isDir = Files.isDirectory(p1) ? 1 : 0;
                        int p2isDir = Files.isDirectory(p2) ? 1 : 0;
                        return p2isDir - p1isDir;
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

    private void getFileOutputString(PrintPath path, StringBuilder output) {
        if (path.isNested()) {
            addIndentation(path, output);
        }
        try {
            long size = Files.isDirectory(path.path()) ?
                    getDirSize(path.path()) : Files.size(path.path());
            output.append(path.path().getFileName())
                    .append(WHITESPACE)
                    .append(size)
                    .append(WHITESPACE)
                    .append(BYTES)
                    .append(LINE_FEED);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addIndentation(PrintPath path, StringBuilder output) {
        String indentation = createIndentation(path);
        refreshListOfOpenedBranches(indentation);
        indentation = addOpenedBranches(indentation);
        output.append(indentation);
    }

    private String createIndentation(PrintPath printPath) {
        StringBuilder indentation = new StringBuilder()
                .append(INDENTATION.repeat(Math.max(0, printPath.nesting - 1)));
        if (printPath.isLastInList()) {
            indentation.append(LAST_IN_LIST);
        } else {
            indentation.append(IN_LIST);
        }
        return indentation.toString();
    }

    private void refreshListOfOpenedBranches(String indentation) {
        char[] chars = indentation.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == OPENING_BRANCH) {
                openedBranches.add(i);
            } else if (chars[i] == CLOSING_BRANCH) {
                openedBranches.remove(i);
            }
        }
    }

    private String addOpenedBranches(String indentation) {
        char[] chars = indentation.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (openedBranches.contains(i)
                    && chars[i] == WHITESPACE_CHAR) {
                chars[i] = BRANCH;
            }
        }
        return String.valueOf(chars);
    }

    private long getDirSize(Path path) throws IOException {
        List<Path> files = Files.list(path).collect(Collectors.toList());
        long size = 0;
        for (Path file : files) {
            if (Files.isRegularFile(file)) {
                size += Files.size(file);
            } else if (Files.isDirectory(file)) {
                size += getDirSize(file);
            }
        }
        return size;
    }
}