package org.yinwang.rubysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.rubysonar.State;
import org.yinwang.rubysonar.types.Type;
import org.yinwang.rubysonar.types.IntType;
import org.yinwang.rubysonar.types.StrType;
import org.yinwang.rubysonar.types.FloatType;


public class StrEmbed extends Node {

    public Node value;


    public StrEmbed(@NotNull Node value, String file, int start, int end) {
        super(file, start, end);
        this.value = value;
    }


    @NotNull
    @Override
    public Type transform(State s) {
        Type t = value.transform(s);

        String str = "";
        if (t instanceof StrType) {
            str = ((StrType) t).value;
        } else if (t instanceof IntType) {
            str = ((IntType) t).value.toString();
        } else if (t instanceof FloatType) {
            str = String.valueOf(((FloatType) t).value);
        }

        return new StrType(str);
    }


    @NotNull
    @Override
    public String toString() {
        return "#{" + value + "}";
    }

}
