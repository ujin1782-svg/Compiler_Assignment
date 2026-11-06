package symbol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import syntaxtree.*;

public class SymbolTable {
    private HashMap<String, ClassInfo> classes = new HashMap<String, ClassInfo>();

    // Class 추가
    public boolean addClass(ClassInfo c) {
        if (classes.containsKey(c.getId())) return false;
        classes.put(c.getId(), c);
        return true;
    }
    
    public ClassInfo getClass(String name) { return classes.get(name); }

    // 변수 찾기: 메소드 지역/매개변수 -> 현재 클래스 필드 -> 부모 클래스 필드
    public Type lookupVar(ClassInfo cls, MethodInfo m, String name) {
        // 현재 속한 메서드에서 찾기
        if (m != null) {
            Type t = m.getVar(name);
            if (t != null) return t;
        }
        Set<String> seen = new HashSet<String>();
        // while문으로 자식에서 부모 순으로 방문하면서 변수를 찾는다.
        while (cls != null && seen.add(cls.getId())) {
            Type t = cls.getVar(name);
            if (t != null) return t;
            cls = (cls.getParent() == null) ? null : classes.get(cls.getParent());
        }
        return null;
    }
    
    // 메서드 찾기: 현재 클래스 -> 부모 클래스
    public MethodInfo lookupMethod(String className, String methodName) {
        Set<String> seen = new HashSet<String>();
        ClassInfo cls = classes.get(className);
        // 자식에서 부모 순으로 탐색하면서 메서드 찾기
        while (cls != null && seen.add(cls.getId())) {
            MethodInfo m = cls.getMethod(methodName);
            if (m != null) return m;
            cls = (cls.getParent() == null) ? null : classes.get(cls.getParent());
        }
        return null;
    }

    // 오류 메시지에 쓸 타입 이름
    public static String name(Type t) {
        if (t == null) return "?";
        if (t instanceof IntegerType) return "int";
        if (t instanceof BooleanType) return "boolean";
        if (t instanceof IntArrayType) return "int[]";
        return ((IdentifierType) t).s;
    }

    // actual 타입의 값을 expected 자리에 넣을 수 있는가?
    // 어느 한쪽이 null이면 이미 다른 오류가 보고된 것이므로 true (연속적인 오류를 방지한다.)
    public boolean isAssignable(Type expected, Type actual) {
        if (expected == null || actual == null) return true;
        if (expected instanceof IdentifierType && actual instanceof IdentifierType) {
            String target = ((IdentifierType) expected).s;
            String cur = ((IdentifierType) actual).s; //current
            Set<String> seen = new HashSet<String>();

            while (cur != null && seen.add(cur)) {
                if (cur.equals(target)) return true; //기대와 현재 값이 같으면 true
                ClassInfo c = classes.get(cur);
                cur = (c == null) ? null : c.getParent(); //부모를 탐색하면서 기대(target)과 같은 타입이 있는지 검사
            }
            return false;
        }
        return expected.getClass() == actual.getClass();
    }
}
