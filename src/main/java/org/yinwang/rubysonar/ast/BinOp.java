package org.yinwang.rubysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.yinwang.rubysonar.State;
import org.yinwang.rubysonar.types.Type;
import org.yinwang.rubysonar.types.StrType;
import org.yinwang.rubysonar.types.IntType;


public class BinOp extends Node {

    @NotNull
    public Node left;
    @NotNull
    public Node right;
    @NotNull
    public Op op;


    public BinOp(@NotNull Op op, @NotNull Node left, @NotNull Node right, String file, int start, int end) {
        super(file, start, end);
        this.left = left;
        this.right = right;
        this.op = op;
        addChildren(left, right);
    }


    @NotNull
    @Override
    public Type transform(State s) {
        Type ltype = transformExpr(left, s);
        Type rtype = transformExpr(right, s);

        if (op == Op.Add) {
            if (ltype instanceof StrType && rtype instanceof StrType) {
                return new StrType(((StrType) ltype).value + ((StrType) rtype).value);
            }
        }

        if (ltype != Type.UNKNOWN) {
            return ltype;
        } else if (rtype != Type.UNKNOWN) {
            return rtype;
        } else {
            return Type.UNKNOWN;
        }
    }


    @NotNull
    @Override
    public String toString() {
        return "(" + left + " " + op + " " + right + ")";
    }

}
