package coursehub.practice3.rules;

public sealed interface Task15RuleResult permits Task15RuleResult.Passed, Task15RuleResult.Violated {

    record Passed() implements Task15RuleResult {
    }

    record Violated(String code, String message) implements Task15RuleResult {

        public Violated {
            if (code == null || code.isBlank()) {
                throw new IllegalArgumentException("code must not be blank");
            }
            if (message == null || message.isBlank()) {
                throw new IllegalArgumentException("message must not be blank");
            }
        }
    }

    static Task15RuleResult pass() {
        return new Passed();
    }

    static Task15RuleResult violated(String code, String message) {
        return new Violated(code, message);
    }
}
