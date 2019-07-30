package org.yinwang.rubysonar.types;

import java.math.BigInteger;


public class IntType extends Type {
	
    public BigInteger value;
	
    public IntType(BigInteger value) {
        this.value = value;
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
