import syntaxtree.*;
import visitor.*;
import symbol.*;

public class TypeCheckMain {
  public static void main(String[] args) {
    try {
      java.io.InputStream in = (args.length > 0) ? new java.io.FileInputStream(args[0]) : System.in;
      Program root = new MiniJavaParser(in).Goal();

      ErrorMsg error = new ErrorMsg();
      SymbolTable table = new SymbolTable();
      root.accept(new BuildSymbolTableVisitor(table, error));   // 1단계: 심볼 테이블
      root.accept(new TypeCheckVisitor(table, error));       // 2단계: 

      if (error.anyErrors) {
        System.out.println("Type check failed.");
        System.exit(1);
      }
      System.out.println("Type check OK.");
    } catch (Exception e) {
      System.out.println(e.toString());
      System.exit(1);
    }
  }
}