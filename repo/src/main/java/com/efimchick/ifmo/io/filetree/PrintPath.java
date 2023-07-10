package com.efimchick.ifmo.io.filetree;

import java.nio.file.Path;

public class PrintPath {
    Path path;
    int nesting;
    boolean lastInList;

    public PrintPath(Path path, int nesting) {
        this.path = path;
        this.nesting = nesting;
    }

    public boolean isLastInList() {
        return lastInList;
    }

    public boolean isNested() {
        return nesting > 0;
    }

    public Path path() {
        return path;
    }

    public int nesting() {
        return nesting;
    }

    public void makeLastInList() {
        lastInList = true;
    }
}