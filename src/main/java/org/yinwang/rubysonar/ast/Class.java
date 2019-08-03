package org.yinwang.rubysonar.ast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yinwang.rubysonar.*;
import org.yinwang.rubysonar.types.ClassType;
import org.yinwang.rubysonar.types.ModuleType;
import org.yinwang.rubysonar.types.Type;

import java.util.List;


public class Class extends Node {
    private static int classCounter = 0;

    @Nullable
    public Node locator;
    public Name name;
    public Node base;
    public Node body;
    public Str docstring;
    public boolean isStatic;


    public Class(@Nullable Node locator, Node base, Node body, Str docstring, boolean isStatic, String file, int start,
                 int end)
    {
        super(file, start, end);

        // set name
        if (locator instanceof Attribute) {
            this.name = ((Attribute) locator).attr;
        } else if (locator instanceof Name) {
            this.name = (Name) locator;
        } else {
            this.name = new Name(genClassName(), file, start, start + 1);
            addChildren(this.name);
        }

        this.locator = locator;
        this.base = base;
        this.body = body;
        this.docstring = docstring;
        this.isStatic = isStatic;
        addChildren(this.locator, this.body, this.base, this.docstring);
    }


    @NotNull
    public static String genClassName() {
        classCounter = classCounter + 1;
        return "class%" + classCounter;
    }


    @NotNull
    @Override
    public Type transform(@NotNull State s) {
        boolean reopen = false;
        Type reopened;
        State env;

        if (locator instanceof Attribute) {
            Attribute aloc = (Attribute) locator;
            Type targetcls = lookupClassLocal(aloc.target, s);

            if (!(targetcls instanceof ClassType || targetcls instanceof ModuleType)) {
                Analyzer.self.putProblem(aloc.target, aloc.target + " is not Class or Module");
                return Type.CONT;
            }

            env = targetcls.table;
            reopened = lookupClassLocal(aloc.attr, env);
        } else if (locator instanceof Name) {
            env = s;
            reopened = lookupClassLocal(locator, env);
            if (isStatic) {
                if (body != null) {
                    boolean wasStatic = Analyzer.self.staticContext;
                    Analyzer.self.setStaticContext(true);
                    transformExpr(body, reopened.table);
                    Analyzer.self.setStaticContext(wasStatic);
                }
                return Type.CONT;
            }
        } else {
            Analyzer.self.putProblem(locator, locator + " is not Attribute or Name");
            return Type.CONT;
        }

        ClassType classType;
        if (reopened instanceof ClassType) {
            classType = (ClassType) reopened;
            reopen = true;
        } else {
            classType = new ClassType(name.id, env);
            classType.table.setParent(env);
        }

        if (base != null) {
            Type baseType = null;
            if (base instanceof Attribute) {
                Attribute abase = (Attribute) base;
                Type basetcls = lookupClassLocal(abase.target, s);
                if (basetcls instanceof ClassType || basetcls instanceof ModuleType) {
                    baseType = lookupClassLocal(abase.attr, basetcls.table);
                } else {
                    Analyzer.self.putProblem(abase.target, abase.target + " is not Class or Module");
                }
            } else if (base instanceof Name) {
                baseType = lookupClassLocal(base, s);
            } else {
                Analyzer.self.putProblem(base, base + " is not Attribute or Name");
            }

            if (baseType instanceof ClassType) {
                classType.addSuper(baseType);
            } else {
                Analyzer.self.putProblem(base, base + " is not a class");
            }
        }

        if (!reopen) {
            // Bind ClassType to name here before resolving the body because the
            // methods need this type as self.
            Binder.bind(env, name, classType, Binding.Kind.CLASS);
            classType.table.insert(Constants.SELFNAME, name, classType, Binding.Kind.SCOPE);
        }

        if (body != null) {
            transformExpr(body, classType.table);
        }
        return Type.CONT;
    }


    private Type lookupClassLocal(Node n, State s) {
        if (n instanceof Name) {
            Name name = (Name) n;
            List<Binding> b = s.lookupLocal(name.id);
            if (b != null) {
                Analyzer.self.putRef(name, b);
                Analyzer.self.resolved.add(name);
                Analyzer.self.unresolved.remove(name);
                return State.makeUnion(b);
            }
        } else {
            Analyzer.self.putProblem(n, n + " should be a Name");
        }

        return Type.CONT;
    }


    @NotNull
    @Override
    public String toString() {
        return "(class:" + name.id + ")";
    }

}
