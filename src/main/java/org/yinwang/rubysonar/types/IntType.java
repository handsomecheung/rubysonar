package org.yinwang.rubysonar.types;

import org.yinwang.rubysonar.Supers;


public class IntType extends Type {

    public IntType() {
        setSuper(Supers.INTEGER);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof IntType;
    }


    @Override
    protected String printType(Type.CyclicTypeRecorder ctr) {
        return "int";
    }
}
