package visitor;

import syntaxtree.*;
import symbol.*;

public class TypeCheckVisitor extends TypeDepthFirstVisitor {
    private SymbolTable table;
    private ErrorMsg error;
    private ClassInfo currClass = null;
    private MethodInfo currMethod = null;

    public TypeCheckVisitor(SymbolTable table, ErrorMsg error) {
        this.table = table;
        this.error = error;
    }

    // 오류 메시지에 "클래스.메소드" 위치를 붙이기 위한 함수
    private String where() {
        String s = (currClass == null) ? "" : currClass.getId();
        return (currMethod == null) ? s : s + "." + currMethod.getId();
    }

    // 식 e 를 검사하고, 그 타입이 expected 자리에 들어갈 수 없으면 오류
    private Type expect(Exp e, Type expected, String what) {
        Type t = e.accept(this);
        if (!table.isAssignable(expected, t))
        error.complain(what + " must be of type " + SymbolTable.name(expected)
                        + " but was " + SymbolTable.name(t) + " (in " + where() + ")");
        return t;
    }

    // 변수를 찾고, 없으면 오류
    private Type lookupVar(String id) {
        Type t = table.lookupVar(currClass, currMethod, id);
        if (t == null) error.complain("Undeclared variable " + id + " (in " + where() + ")");
        return t;
    }

        // MainClass m; ClassDeclList cl;
    public Type visit(Program n) {
        n.m.accept(this);
        for (int i = 0; i < n.cl.size(); i++) n.cl.elementAt(i).accept(this);
        return null;
    }

    // Identifier i1,i2; Statement s;
    public Type visit(MainClass n) {
        currClass = table.getClass(n.i1.toString());
        currMethod = null;
        n.s.accept(this);
        currClass = null;
        return null;
    }

    // Identifier i; VarDeclList vl; MethodDeclList ml;
    public Type visit(ClassDeclSimple n) {
        currClass = table.getClass(n.i.toString());
        checkMembers(n.vl, n.ml);
        currClass = null;
        return null;
    }

    // Identifier i; Identifier j; VarDeclList vl; MethodDeclList ml;
    public Type visit(ClassDeclExtends n) {
        currClass = table.getClass(n.i.toString());
        String parent = n.j.toString();
        if (table.getClass(parent) == null)
        error.complain("Superclass " + parent + " of " + n.i + " is not defined");
        else if (inheritsFromItself(n.i.toString()))
        error.complain("Cyclic inheritance involving " + n.i);
        checkMembers(n.vl, n.ml);
        currClass = null;
        return null;
    }

    // A extends B, B extends A 같은 순환 상속 검사
    private boolean inheritsFromItself(String id) {
        String p = table.getClass(id).getParent();
        for (int steps = 0; p != null && steps < 10000; steps++) {
        if (p.equals(id)) return true;
        ClassInfo c = table.getClass(p);
        p = (c == null) ? null : c.getParent();
        }
        return false;
    }

    private void checkMembers(VarDeclList vl, MethodDeclList ml) {
        for (int i = 0; i < vl.size(); i++) vl.elementAt(i).accept(this);
        for (int i = 0; i < ml.size(); i++) ml.elementAt(i).accept(this);
    }

    // 선언에 쓴 클래스 타입이 실제로 있는지만 확인 (등록은 1단계에서 끝남)
    public Type visit(VarDecl n) { n.t.accept(this); return null; }
    public Type visit(Formal n)  { n.t.accept(this); return null; }

    // Type t; Identifier i; FormalList fl; VarDeclList vl; StatementList sl; Exp e;
    public Type visit(MethodDecl n) {
        currMethod = currClass.getMethod(n.i.toString());
        n.t.accept(this);
        for (int i = 0; i < n.fl.size(); i++) n.fl.elementAt(i).accept(this);
        for (int i = 0; i < n.vl.size(); i++) n.vl.elementAt(i).accept(this);
        for (int i = 0; i < n.sl.size(); i++) n.sl.elementAt(i).accept(this);
        expect(n.e, n.t, "Return value of " + n.i);
        currMethod = null;
        return null;
    }
    public Type visit(IntArrayType n) { return n; }
    public Type visit(BooleanType n)  { return n; }
    public Type visit(IntegerType n)  { return n; }

    public Type visit(IdentifierType n) {
        if (table.getClass(n.s) == null) {
        error.complain("Cannot find class " + n.s + " (in " + where() + ")");
        return null;
        }
        return n;
    }

    // StatementList sl;
    public Type visit(Block n) {
        for (int i = 0; i < n.sl.size(); i++) n.sl.elementAt(i).accept(this);
        return null;
    }

    // Exp e; Statement s1,s2;
    public Type visit(If n) {
        expect(n.e, new BooleanType(), "Condition of if");
        n.s1.accept(this);
        n.s2.accept(this);
        return null;
    }

    // Exp e; Statement s;
    public Type visit(While n) {
        expect(n.e, new BooleanType(), "Condition of while");
        n.s.accept(this);
        return null;
    }

    // Exp e;
    public Type visit(Print n) {
        expect(n.e, new IntegerType(), "Argument of System.out.println");
        return null;
    }

    // Identifier i; Exp e;
    public Type visit(Assign n) {
        Type lhs = lookupVar(n.i.toString());
        expect(n.e, lhs, "Right side of assignment to " + n.i);
        return null;
    }

    // Identifier i; Exp e1,e2;    i[e1] = e2;
    public Type visit(ArrayAssign n) {
        Type t = lookupVar(n.i.toString());
        if (t != null && !(t instanceof IntArrayType))
        error.complain(n.i + " is not an int[] (in " + where() + ")");
        expect(n.e1, new IntegerType(), "Array index");
        expect(n.e2, new IntegerType(), "Right side of array assignment");
        return null;
    }

    // Exp e1,e2;
    public Type visit(And n) {
        expect(n.e1, new BooleanType(), "Left side of &&");
        expect(n.e2, new BooleanType(), "Right side of &&");
        return new BooleanType();
    }

    public Type visit(LessThan n) {
        expect(n.e1, new IntegerType(), "Left side of <");
        expect(n.e2, new IntegerType(), "Right side of <");
        return new BooleanType();
    }

    public Type visit(Plus n) {
        expect(n.e1, new IntegerType(), "Left side of +");
        expect(n.e2, new IntegerType(), "Right side of +");
        return new IntegerType();
    }

    public Type visit(Minus n) {
        expect(n.e1, new IntegerType(), "Left side of -");
        expect(n.e2, new IntegerType(), "Right side of -");
        return new IntegerType();
    }

    public Type visit(Times n) {
        expect(n.e1, new IntegerType(), "Left side of *");
        expect(n.e2, new IntegerType(), "Right side of *");
        return new IntegerType();
    }

    // e1[e2]
    public Type visit(ArrayLookup n) {
        expect(n.e1, new IntArrayType(), "Array expression");
        expect(n.e2, new IntegerType(), "Array index");
        return new IntegerType();
    }

    // e.length
    public Type visit(ArrayLength n) {
        expect(n.e, new IntArrayType(), "Operand of .length");
        return new IntegerType();
    }

    public Type visit(IntegerLiteral n) { return new IntegerType(); }
    public Type visit(True n)           { return new BooleanType(); }
    public Type visit(False n)          { return new BooleanType(); }

    // String s;
    public Type visit(IdentifierExp n) { return lookupVar(n.s); }

    public Type visit(This n) {
        if (currMethod == null) {
        error.complain("this cannot be used in main (in " + where() + ")");
        return null;
        }
        return new IdentifierType(currClass.getId());
    }

    // new int[e]
    public Type visit(NewArray n) {
        expect(n.e, new IntegerType(), "Size of new int[]");
        return new IntArrayType();
    }

    // new i()  -> 클래스가 있는지 IdentifierType 방문으로 확인
    public Type visit(NewObject n) {
        return new IdentifierType(n.i.toString()).accept(this);
    }

    // !e
    public Type visit(Not n) {
        expect(n.e, new BooleanType(), "Operand of !");
        return new BooleanType();
    }

    // Exp e; Identifier i; ExpList el;     e.i(el)
    public Type visit(Call n) {
        Type objType = n.e.accept(this);
        MethodInfo m = null;
        if (objType != null && !(objType instanceof IdentifierType)) {
        error.complain("Cannot call method " + n.i + " on type "
                        + SymbolTable.name(objType) + " (in " + where() + ")");
        } else if (objType != null) {
        String cls = ((IdentifierType) objType).s;
        m = table.lookupMethod(cls, n.i.toString());
        if (m == null)
            error.complain("Method " + n.i + " is not defined in class " + cls
                        + " (in " + where() + ")");
        }

        if (m == null) {   // 메소드를 모르면 인자 식만 검사하고 끝
        for (int i = 0; i < n.el.size(); i++) n.el.elementAt(i).accept(this);
        return null;
        }
        if (m.paramCount() != n.el.size()) {
        error.complain("Method " + n.i + " expects " + m.paramCount()
                        + " argument(s) but got " + n.el.size() + " (in " + where() + ")");
        for (int i = 0; i < n.el.size(); i++) n.el.elementAt(i).accept(this);
        } else {
        for (int i = 0; i < n.el.size(); i++)
            expect(n.el.elementAt(i), m.paramTypeAt(i), "Argument " + (i + 1) + " of " + n.i);
        }
        return m.getReturnType();
    }
}