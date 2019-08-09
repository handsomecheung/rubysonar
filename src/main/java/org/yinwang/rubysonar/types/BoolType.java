package org.yinwang.rubysonar.types;

import org.yinwang.rubysonar.Analyzer;
import org.yinwang.rubysonar.Supers;


public class BoolType extends Type {

    public enum Value {
        True,
        False
    }

    public Value value;

    public BoolType(Value value) {
        this.value = value;
        if (value == BoolType.Value.True) {
            setSuper(Supers.BOOL_TRUE);
        } else if (value == BoolType.Value.False) {
            setSuper(Supers.BOOL_FALSE);
        }
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof BoolType);
    }


    @Override
    protected String printType(CyclicTypeRecorder ctr) {
        if (Analyzer.self.hasOption("debug")) {
            return "bool(" + value + ")";
        } else {
            return "bool";
        }
    }
}
