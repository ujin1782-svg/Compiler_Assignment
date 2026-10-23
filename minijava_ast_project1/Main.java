import syntaxtree.*;
import visitor.*;

public class Main {
   public static void main(String [] args) {
      try {
         java.io.InputStream in = (args.length > 0) ? new java.io.FileInputStream(args[0]) : System.in;
         Program root = new MiniJavaParser(in).Goal();
         root.accept(new PrettyPrintVisitor());
      }
      catch (Exception e) {
         System.out.println(e.toString());
      }
   }
}
