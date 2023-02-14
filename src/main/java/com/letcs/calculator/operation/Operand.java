package com.letcs.calculator.operation;

import com.letcs.calculator.CalculatorMode;

public interface Operand {
    boolean tryToAppend(String input, CalculatorMode mode);

    void back();

    void onesComp();

    void twosComp();

    Object getValue();

    boolean isInit();

    String toDisplay();

    String toUpperBinaryDisplay();

    String toLowerBinaryDisplay();

    Operand copy();
}