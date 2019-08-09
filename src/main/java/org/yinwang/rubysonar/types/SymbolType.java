package org.yinwang.rubysonar.types;

import org.jetbrains.annotations.NotNull;
import org.yinwang.rubysonar.Supers;


public class SymbolType extends Type {

    public String name;


    public SymbolType(@NotNull String name) {
        this.name = name;
        setSuper(Supers.SYMBOL);
    }


    @Override
    public boolean equals(Object other) {
        return other instanceof SymbolType;
    }


    @Override
    protected String printType(CyclicTypeRecorder ctr) {
        return "symbol";
    }
}
