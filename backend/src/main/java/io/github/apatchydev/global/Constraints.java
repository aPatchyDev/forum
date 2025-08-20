package io.github.apatchydev.global;

public abstract class Constraints {
    public static final String PRINTABLE_ASCII = "[\\x20-\\x7E]";
    public static final String PASSWORD = PRINTABLE_ASCII + "+";
}
