package org.yinwang.rubysonar.unused;

import org.jetbrains.annotations.NotNull;


public class Result implements Comparable<Result> {
    @NotNull
    public String file;
    public int line;
    @NotNull
    public String method;


    public Result(@NotNull String file, int line, @NotNull String method) {
        this.file = file;
        this.line = line;
        this.method = method;
    }

    @Override
    public int compareTo(@NotNull Result result) {
        int f = file.compareTo(result.file);
        if (f != 0) {
            return f;
        }

        int l = Integer.compare(line, result.line);
        if (l != 0) {
            return l;
        }

        int m = method.compareTo(result.method);
        if (m != 0) {
            return m;
        }

        return 0;
    }


    @NotNull
    @Override
    public String toString() {
        return file + ":" + line + " " + method;
    }
}
