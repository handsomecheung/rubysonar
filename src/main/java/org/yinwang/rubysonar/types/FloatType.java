package org.yinwang.rubysonar.types;

import org.yinwang.rubysonar.Supers;


public class FloatType extends Type {

    public FloatType() {
        setSuper(Supers.FLOAT);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof FloatType;
    }


    @Override
    protected String printType(CyclicTypeRecorder ctr) {
        return "float";
    }
}
