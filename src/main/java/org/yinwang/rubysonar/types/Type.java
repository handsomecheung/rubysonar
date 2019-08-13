package org.yinwang.rubysonar.types;

import org.jetbrains.annotations.NotNull;
import org.yinwang.rubysonar.Analyzer;
import org.yinwang.rubysonar.Binder;
import org.yinwang.rubysonar.State;
import org.yinwang.rubysonar.TypeStack;
import org.yinwang.rubysonar.Binding;
import org.yinwang.rubysonar.Supers;
import org.yinwang.rubysonar.ast.Name;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;


public abstract class Type {

    @NotNull
    public State table = new State(Analyzer.self.globaltable, State.StateType.INSTANCE);
    public String file = null;
    public boolean mutated = false;


    @NotNull
    protected static TypeStack typeStack = new TypeStack();


    public Type() {
    }


    public void setTable(@NotNull State table) {
        this.table = table;
    }


    public void setFile(String file) {
        this.file = file;
    }


    public boolean isMutated() {
        return mutated;
    }


    public void setMutated(boolean mutated) {
        this.mutated = mutated;
    }

    public boolean isNumType() {
        return this instanceof IntType || this instanceof FloatType;
    }


    public boolean isStrType() {
        return this == STR;
    }


    public boolean isUnknownType() {
        return this == Type.UNKNOWN;
    }

    private static ClassType getSuper(String clsname) {
        ClassType supercls = null;
        Name name = new Name(clsname);
        List<Binding> b = Analyzer.self.globaltable.lookupLocal(name.id);
        if (b != null) {
            Analyzer.self.putRef(name, b);
            Analyzer.self.resolved.add(name);
            Analyzer.self.unresolved.remove(name);
            Type t = State.makeUnion(b);
            if (t instanceof ClassType) {
                supercls = (ClassType) t;
            }
        }

        if (!(supercls instanceof ClassType)) {
            supercls  = new ClassType(clsname, Analyzer.self.globaltable);
            Binder.bind(Analyzer.self.globaltable, new Name(clsname), supercls, Binding.Kind.CLASS);
        }

        return supercls;
    }


    public void setSuper(String clsname) {
        ClassType supercls = getSuper(clsname);
        setTable(supercls.table);
    }


    /**
     * Internal class to support printing in the presence of type-graph cycles.
     */
    protected class CyclicTypeRecorder {
        int count = 0;
        @NotNull
        private Map<Type, Integer> elements = new HashMap<>();
        @NotNull
        private Set<Type> used = new HashSet<>();


        public Integer push(Type t) {
            count += 1;
            elements.put(t, count);
            return count;
        }


        public void pop(Type t) {
            elements.remove(t);
            used.remove(t);
        }


        public Integer visit(Type t) {
            Integer i = elements.get(t);
            if (i != null) {
                used.add(t);
            }
            return i;
        }


        public boolean isUsed(Type t) {
            return used.contains(t);
        }
    }


    protected abstract String printType(CyclicTypeRecorder ctr);


    @NotNull
    @Override
    public String toString() {
        return printType(new CyclicTypeRecorder());
    }


    public static InstanceType UNKNOWN = new InstanceType(new ClassType("?", null, getSuper(Supers.OBJECT)));
    public static InstanceType CONT = new InstanceType(new ClassType("nil", null, getSuper(Supers.OBJECT)));
    public static InstanceType NIL = new InstanceType(new ClassType("nil", null, getSuper(Supers.OBJECT)));
    public static StrType STR = new StrType(null);
    public static IntType INT = new IntType();
    public static FloatType FLOAT = new FloatType();
    public static BoolType TRUE = new BoolType(BoolType.Value.True);
    public static BoolType FALSE = new BoolType(BoolType.Value.False);
}
