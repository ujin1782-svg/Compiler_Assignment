package symbol;

import java.util.HashMap;
import syntaxtree.Type;

public class ClassInfo {
    private String id;
    private String parent;
    private HashMap<String, Type> fields = new HashMap<String, Type>();
    private HashMap<String, MethodInfo> methods = new HashMap<String, MethodInfo> ();

    public ClassInfo(String id, String parent) {
        this.id = id;
        this.parent = parent;
    }

    public String getId() { return id; }
    public String getParent() { return parent; }

    public boolean addVar(String name, Type t) {
        if (fields.containsKey(name)) return false;
        fields.put(name, t);
        return true;
    }

    public boolean addMethod(MethodInfo m) {
        if (methods.containsKey(m)) return false;
        methods.put(m.getId(), m);
        return true;
    }

    public Type getVar(String name) { return fields.get(name); }
    public MethodInfo getMethod(String name) { return methods.get(name); }
}
