package com.letcs.calculator.operation;

public interface Operand {
    boolean tryToAppend(String input);

    void back();

    Object getValue();

    boolean isInit();

    String toDisplay();

    Operand copy();
}