package visitor;

import syntaxtree.*;
import symbol.*;

public class BuildSymbolTableVisitor extends TypeDepthFirstVisitor {
  private SymbolTable table;
  private ErrorMsg error;
  private ClassInfo currClass = null;
  private MethodInfo currMethod = null;

  public BuildSymbolTableVisitor(SymbolTable table, ErrorMsg error) {
    this.table = table;
    this.error = error;
  }

  // MainClass m; ClassDeclList cl;
  public Type visit(Program n) {
    n.m.accept(this);
    for (int i = 0; i < n.cl.size(); i++) {
      n.cl.elementAt(i).accept(this);
    }
    return null;
  }

  // Identifier i1,i2; Statement s;
  public Type visit(MainClass n) {
    table.addClass(new ClassInfo(n.i1.toString(), null));
    return null;
  }

  // Identifier i; VarDeclList vl; MethodDeclList ml;
  public Type visit(ClassDeclSimple n) {
    String id = n.i.toString();
    currClass = new ClassInfo(id, null);
    if (!table.addClass(currClass))
      error.complain("Class " + id + " is already defined");
    visitMembers(n.vl, n.ml);
    currClass = null;
    return null;
  }

  // Identifier i; Identifier j; VarDeclList vl; MethodDeclList ml;
  public Type visit(ClassDeclExtends n) {
    String id = n.i.toString();
    currClass = new ClassInfo(id, n.j.toString());   // j = 부모 클래스 이름
    if (!table.addClass(currClass))
      error.complain("Class " + id + " is already defined");
    visitMembers(n.vl, n.ml);
    currClass = null;
    return null;
  }

  private void visitMembers(VarDeclList vl, MethodDeclList ml) {
    for (int i = 0; i < vl.size(); i++) vl.elementAt(i).accept(this);
    for (int i = 0; i < ml.size(); i++) ml.elementAt(i).accept(this);
  }

  // Type t; Identifier i;   (교재 Program 5.8)
  public Type visit(VarDecl n) {
    Type t = n.t.accept(this);
    String id = n.i.toString();
    if (currMethod == null) {
      if (!currClass.addVar(id, t))
        error.complain(id + " is already defined in " + currClass.getId());
    } else if (!currMethod.addVar(id, t)) {
      error.complain(id + " is already defined in "
                     + currClass.getId() + "." + currMethod.getId());
    }
    return null;
  }

  // Type t; Identifier i; FormalList fl; VarDeclList vl; StatementList sl; Exp e;
  public Type visit(MethodDecl n) {
    Type t = n.t.accept(this);
    String id = n.i.toString();
    currMethod = new MethodInfo(id, t);
    if (!currClass.addMethod(currMethod))
      error.complain("Method " + id + " is already defined in " + currClass.getId());
    for (int i = 0; i < n.fl.size(); i++) n.fl.elementAt(i).accept(this);
    for (int i = 0; i < n.vl.size(); i++) n.vl.elementAt(i).accept(this);
    currMethod = null;
    return null;
  }

  // Type t; Identifier i;
  public Type visit(Formal n) {
    Type t = n.t.accept(this);
    String id = n.i.toString();
    if (!currMethod.addParam(id, t))
      error.complain(id + " is already defined in "
                     + currClass.getId() + "." + currMethod.getId());
    return null;
  }

  // 타입 노드는 자기 자신을 돌려준다
  public Type visit(IntArrayType n)   { return n; }
  public Type visit(BooleanType n)    { return n; }
  public Type visit(IntegerType n)    { return n; }
  public Type visit(IdentifierType n) { return n; }
}