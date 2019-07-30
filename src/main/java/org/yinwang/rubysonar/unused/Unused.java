package org.yinwang.rubysonar.unused;

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

        _.msg("\nAnalysis Results:\n");

        for (Binding b : analyzer.allBindings) {
            if (b.node.file == null || !b.refs.isEmpty()) {
                continue;
            }

            if (!(b.type instanceof FunType)) {
                continue;
            }
            FunType fun = (FunType) b.type;
            if (fun.func.isLamba) {
                continue;
            }

            String message = b.node.file + ": ";
            if (fun.cls != null) {
                if (fun.isClassMethod) {
                    message += "Unused Method: " + fun.cls.name + "::" + b.node.name;
                } else {
                    message += "Unused Method: " + fun.cls.name + "#" + b.node.name;
                }
            } else {
                message += "Unused Method: " + b.node.name;
            }

            Analyzer.self.putProblem(b.node, message);
            _.msg(message);
        }

        _.msg("\nAnalysis Done");

        analyzer.close();
    }


    public static void main(@NotNull String[] args) throws Exception {
        Options options = new Options(args);
        new Unused().start(options.getArgs(), options.getOptionsMap());
    }
}
