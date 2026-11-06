class TypeErrors {
    public static void main(String[] a) {
        System.out.println(new A().run(true));
    }
}

class A {
    int x;
    int x;
    public int run(int n) {
        boolean b;
        int[] arr;
        B obj;
        b = 1 + true;
        x = b;
        arr[b] = 3;
        y = 5;
        obj = new A();
        if (n) x = 1; else x = 2;
        return this.foo(1, 2);
    }
}

class C extends Missing {
    public boolean f() { return 1; }
}