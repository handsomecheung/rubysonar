package org.yinwang.rubysonar.unsued;

import org.jetbrains.annotations.NotNull;
import org.yinwang.rubysonar.Analyzer;
import org.yinwang.rubysonar.Binding;
import org.yinwang.rubysonar.Options;
import org.yinwang.rubysonar._;
import org.yinwang.rubysonar.ast.Attribute;
import org.yinwang.rubysonar.types.ClassType;
import org.yinwang.rubysonar.types.FunType;
import org.yinwang.rubysonar.types.ModuleType;

import java.io.File;
import java.util.Map;
import java.util.List;


public class Unused {
    private Analyzer analyzer;

    private void start(@NotNull String fileOrDir, Map<String, Object> options) throws Exception {
        File f = new File(fileOrDir);
        File rootDir = f.isFile() ? f.getParentFile() : f;
        try {
            _.unifyPath(rootDir);
        } catch (Exception e) {
            _.die("File not found: " + f);
        }

        analyzer = new Analyzer(options);
        analyzer.analyze(f.getPath());

        for (Binding b : analyzer.allBindings) {
            if (b.refs.isEmpty()) {
                String message;
                if (b.type instanceof FunType) {
                    FunType funT = (FunType) b.type;
                    if (funT.cls != null) {
                        if (funT.isClassMethod) {
                            message = "Unused Method: " + funT.cls.name + "::" + b.node.name;
                        } else {
                            message = "Unused Method: " + funT.cls.name + "#" + b.node.name;
                        }
                    } else {
                        message = "Unused Method: " + b.node.name;
                    }
                } else if (b.type instanceof ClassType) {
                    message = "Unused Class: " + b.node.name;
                } else if (b.type instanceof ModuleType) {
                    message = "Unused Module: " + b.node.name;
                } else {
                    message = "Unused Variable: " + b.node.name;
                }

                Analyzer.self.putProblem(b.node, message);
                _.msg(message);

            }
        }

        analyzer.close();
    }


    public static void main(@NotNull String[] args) throws Exception {
        Options options = new Options(args);
        List<String> argList = options.getArgs();
        String fileOrDir = argList.get(0);

        new Unused().start(fileOrDir, options.getOptionsMap());
        _.msg("done");
    }
}
