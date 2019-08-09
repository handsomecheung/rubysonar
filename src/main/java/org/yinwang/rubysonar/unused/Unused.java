package org.yinwang.rubysonar.unused;


import org.jetbrains.annotations.NotNull;
import org.yinwang.rubysonar.Analyzer;
import org.yinwang.rubysonar.Binding;
import org.yinwang.rubysonar.Options;
import org.yinwang.rubysonar._;
import org.yinwang.rubysonar.types.FunType;

import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class Unused {
    private Analyzer analyzer;

    private void start(@NotNull List<String> fileOrDirs, Map<String, Object> options) throws Exception {
        analyzer = new Analyzer(options);

        for(String fileOrDir: fileOrDirs){
            File f = new File(fileOrDir);
            File rootDir = f.isFile() ? f.getParentFile() : f;
            try {
                _.unifyPath(rootDir);
            } catch (Exception e) {
                _.die("File not found: " + f);
            }

            analyzer.analyze(f.getPath());
        }

        for(String fileOrDir: fileOrDirs){
            analyzer.analyze(new File(fileOrDir).getPath());
        }

        // assume all block and lambda be called
        analyzer.applyUncalledLambda();

        _.msg("\nAnalysis Results:\n");

        Set<String> resultKeys = new HashSet<>();
        List<Result> results = new ArrayList<>();
        for (Binding b : analyzer.allBindings) {
            if (b.node.file == null || !b.refs.isEmpty()) {
                continue;
            }

            if (b.node.file.equals(analyzer.builtinMethodPath)) {
                continue;
            }

            if (!(b.type instanceof FunType)) {
                continue;
            }
            FunType fun = (FunType) b.type;
            if (fun.func.isLamba) {
                continue;
            }

            String method;
            if (fun.cls != null) {
                if (fun.isClassMethod) {
                    method = fun.cls.name + "::" + b.node.name;
                } else {
                    method = fun.cls.name + "#" + b.node.name;
                }
            } else {
                method = b.node.name;
            }
            Result result = new Result(b.node.file, b.node.start, method);
            String resultKey = result.toString();
            if (!resultKeys.contains(resultKey)) {
                results.add(result);
                resultKeys.add(resultKey);
            }
            Analyzer.self.putProblem(b.node, result.toString());
        }

        Collections.sort(results);
        for (Result result : results) {
            _.msg(result.toString());
        }
        _.msg("\nAnalysis Done");

        analyzer.close();
    }


    public static void main(@NotNull String[] args) throws Exception {
        Options options = new Options(args);
        new Unused().start(options.getArgs(), options.getOptionsMap());
    }
}
