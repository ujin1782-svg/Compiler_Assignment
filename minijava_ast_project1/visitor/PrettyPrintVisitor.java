package visitor;
import syntaxtree.*;

public class PrettyPrintVisitor implements Visitor {

  public void visit(Program n) {
    n.m.accept(this);
    for (int i = 0; i < n.cl.size(); i++) {
      System.out.println();
      n.cl.elementAt(i).accept(this);
    }
  }

  public void visit(MainClass n) {
    System.out.print("class ");
    n.i1.accept(this);
    System.out.println(" {");
    System.out.print("  public static void main (String [] ");
    n.i2.accept(this);
    System.out.println(") {");
    System.out.print("    ");
    n.s.accept(this);
    System.out.println("  }");
    System.out.println("}");
  }

  public void visit(ClassDeclSimple n) {
    System.out.print("class ");
    n.i.accept(this);
    System.out.println(" { ");
    for (int i = 0; i < n.vl.size(); i++) {
      System.out.print("  ");
      n.vl.elementAt(i).accept(this);
      if (i + 1 < n.vl.size()) { System.out.println(); }
    }
    for (int i = 0; i < n.ml.size(); i++) {
      System.out.println();
      n.ml.elementAt(i).accept(this);
    }
    System.out.println();
    System.out.println("}");
  }

  public void visit(ClassDeclExtends n) {
    System.out.print("class ");
    n.i.accept(this);
    System.out.println(" extends ");
    n.j.accept(this);
    System.out.println(" { ");
    for (int i = 0; i < n.vl.size(); i++) {
      System.out.print("  ");
      n.vl.elementAt(i).accept(this);
      if (i + 1 < n.vl.size()) { System.out.println(); }
    }
    for (int i = 0; i < n.ml.size(); i++) {
      System.out.println();
      n.ml.elementAt(i).accept(this);
    }
    System.out.println();
    System.out.println("}");
  }

  public void visit(VarDecl n) {
    n.t.accept(this);
    System.out.print(" ");
    n.i.accept(this);
    System.out.print(";");
  }

  public void visit(MethodDecl n) {
    System.out.print("  public ");
    n.t.accept(this);
    System.out.print(" ");
    n.i.accept(this);
    System.out.print(" (");
    for (int i = 0; i < n.fl.size(); i++) {
      n.fl.elementAt(i).accept(this);
      if (i + 1 < n.fl.size()) { System.out.print(", "); }
    }
    System.out.println(") { ");
    for (int i = 0; i < n.vl.size(); i++) {
      System.out.print("    ");
      n.vl.elementAt(i).accept(this);
      System.out.println("");
    }
    for (int i = 0; i < n.sl.size(); i++) {
      System.out.print("    ");
      n.sl.elementAt(i).accept(this);
      if (i < n.sl.size()) { System.out.println(""); }
    }
    System.out.print("    return ");
    n.e.accept(this);
    System.out.println(";");
    System.out.print("  }");
  }

  public void visit(Formal n) {
    n.t.accept(this);
    System.out.print(" ");
    n.i.accept(this);
  }

  public void visit(IntArrayType n) {
    System.out.print("int []");
  }

  public void visit(BooleanType n) {
    System.out.print("boolean");
  }

  public void visit(IntegerType n) {
    System.out.print("int");
  }

  public void visit(IdentifierType n) {
    System.out.print(n.s);
  }

  public void visit(Block n) {
    System.out.println("{ ");
    for (int i = 0; i < n.sl.size(); i++) {
      System.out.print("      ");
      n.sl.elementAt(i).accept(this);
      System.out.println();
    }
    System.out.print("    } ");
  }

  public void visit(If n) {
    System.out.print("if (");
    n.e.accept(this);
    System.out.println(") ");
    System.out.print("    ");
    n.s1.accept(this);
    System.out.println();
    System.out.print("    else ");
    n.s2.accept(this);
  }

  public void visit(While n) {
    System.out.print("while (");
    n.e.accept(this);
    System.out.print(") ");
    n.s.accept(this);
  }

  public void visit(Print n) {
    System.out.print("System.out.println(");
    n.e.accept(this);
    System.out.print(");");
  }

  public void visit(Assign n) {
    n.i.accept(this);
    System.out.print(" = ");
    n.e.accept(this);
    System.out.print(";");
  }

  public void visit(ArrayAssign n) {
    n.i.accept(this);
    System.out.print("[");
    n.e1.accept(this);
    System.out.print("] = ");
    n.e2.accept(this);
    System.out.print(";");
  }

  public void visit(And n) {
    System.out.print("(");
    n.e1.accept(this);
    System.out.print(" && ");
    n.e2.accept(this);
    System.out.print(")");
  }

  public void visit(LessThan n) {
    System.out.print("(");
    n.e1.accept(this);
    System.out.print(" < ");
    n.e2.accept(this);
    System.out.print(")");
  }

  public void visit(Plus n) {
    System.out.print("(");
    n.e1.accept(this);
    System.out.print(" + ");
    n.e2.accept(this);
    System.out.print(")");
  }

  public void visit(Minus n) {
    System.out.print("(");
    n.e1.accept(this);
    System.out.print(" - ");
    n.e2.accept(this);
    System.out.print(")");
  }

  public void visit(Times n) {
    System.out.print("(");
    n.e1.accept(this);
    System.out.print(" * ");
    n.e2.accept(this);
    System.out.print(")");
  }

  public void visit(ArrayLookup n) {
    n.e1.accept(this);
    System.out.print("[");
    n.e2.accept(this);
    System.out.print("]");
  }

  public void visit(ArrayLength n) {
    n.e.accept(this);
    System.out.print(".length");
  }

  public void visit(Call n) {
    n.e.accept(this);
    System.out.print(".");
    n.i.accept(this);
    System.out.print("(");
    for (int i = 0; i < n.el.size(); i++) {
      n.el.elementAt(i).accept(this);
      if (i + 1 < n.el.size()) { System.out.print(", "); }
    }
    System.out.print(")");
  }

  public void visit(IntegerLiteral n) {
    System.out.print(n.i);
  }

  public void visit(True n) {
    System.out.print("true");
  }

  public void visit(False n) {
    System.out.print("false");
  }

  public void visit(IdentifierExp n) {
    System.out.print(n.s);
  }

  public void visit(This n) {
    System.out.print("this");
  }

  public void visit(NewArray n) {
    System.out.print("new int [");
    n.e.accept(this);
    System.out.print("]");
  }

  public void visit(NewObject n) {
    System.out.print("new ");
    System.out.print(n.i.s);
    System.out.print("()");
  }

  public void visit(Not n) {
    System.out.print("!");
    n.e.accept(this);
  }

  public void visit(Identifier n) {
    System.out.print(n.s);
  }
}
