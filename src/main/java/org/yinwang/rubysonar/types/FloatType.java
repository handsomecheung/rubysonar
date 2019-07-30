package org.yinwang.rubysonar.types;

public class FloatType extends Type {
	
    public double value;
    
    public FloatType() {
        this.value = 0;
    }

    public FloatType(double value) {
        this.value = value;
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
