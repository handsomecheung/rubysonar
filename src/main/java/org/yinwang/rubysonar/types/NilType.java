package org.yinwang.rubysonar.types;

import org.yinwang.rubysonar.Supers;


public class NilType extends Type {

    public NilType() {
        setSuper(Supers.NIL_CLASS);
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof NilType);
    }


    @Override
    protected String printType(CyclicTypeRecorder ctr) {
        return "nil";
    }
}
