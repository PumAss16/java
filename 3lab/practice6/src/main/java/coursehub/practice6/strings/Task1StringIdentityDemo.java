package coursehub.practice6.strings;

class StringIdentityDemo {

    static boolean sameReference(String left, String right) {
        return left == right;
    }

    static boolean sameValue(String left, String right) {
        return left.equals(right);
    }

    static String copy(String text) {
        return new String(text);
    }

    static String interned(String text) {
        return text.intern();
    }

    static String builtAtRuntime(String part) {
        return "JA" + part;
    }
}
