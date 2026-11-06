package symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import syntaxtree.Type; //TypeVisitor()

public class MethodInfo {
    private String id;
    private Type returnType;
    // parameter name 나열
    private List<String> paramNames = new ArrayList<String>();
    // parameter type & local variable type
    private HashMap<String, Type> params = new HashMap<String, Type>();
    private HashMap<String, Type> locals = new HashMap<String, Type>();

    public MethodInfo(String id, Type returnType) {
        this.id = id;
        this.returnType = returnType;
    }

    public String getId() { return id; }
    public Type getReturnType() { return returnType; }

    // parameter 추가 | 입력: 변수명, 타입 | 출력 : 매개변수 추가 성공 여부 (T/F)
    public boolean addParam(String name, Type t) {
        if (params.containsKey(name)) return false; //이미 같은 이름의 매개변수가 존재하면, false 반환
        paramNames.add(name);
        params.put(name, t);
        return true;
    }
    // variable 추가 | 입력: 변수명, 타입 | 출력 : T/F
    // 지역 변수 이름이 매개변수와 같아도 중복 선언으로 본다. (매개변수가 먼저 선언된다.)
    public boolean addVar(String name, Type t) {
        if (params.containsKey(name) || locals.containsKey(name)) return false;
        locals.put(name, t);
        return true;
    }

    // 지역변수 -> 매개변수 순으로 찾는다. 없으면 null
    public Type getVar(String name) {
        if (locals.containsKey(name)) return locals.get(name);
        return params.get(name);
    }

    // parameter 개수 (Name 개수)
    public int paramCount() { return paramNames.size(); }
    // paramNames에서 i번째 매개변수의 Name으로 params에 저장된 Type 반환
    public Type paramTypeAt(int i) { return params.get(paramNames.get(i)); }
}
