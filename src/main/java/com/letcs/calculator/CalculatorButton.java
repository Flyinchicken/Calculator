package com.letcs.calculator;

import java.util.HashMap;
import java.util.Map;

public enum CalculatorButton {
    BTN_0("0", BtnType.digit),
    BTN_1("1", BtnType.digit),
    BTN_2("2", BtnType.digit),
    BTN_3("3", BtnType.digit),
    BTN_4("4", BtnType.digit),
    BTN_5("5", BtnType.digit),
    BTN_6("6", BtnType.digit),
    BTN_7("7", BtnType.digit),
    BTN_8("8", BtnType.digit),
    BTN_9("9", BtnType.digit),
    BTN_A("A", BtnType.digit),
    BTN_B("B", BtnType.digit),
    BTN_C("C", BtnType.digit),
    BTN_D("D", BtnType.digit),
    BTN_E("E", BtnType.digit),
    BTN_F("F", BtnType.digit),
    BTN_00("00", BtnType.digit),
    BTN_FF("FF", BtnType.digit),

    BTN_AC("AC", BtnType.functional),
    BTN_BACK("BACK", BtnType.functional),
    BTN_1SCOMP("1's", BtnType.functional),
    BTN_2SCOMP("2's", BtnType.functional),

    BTN_PLUS("+", BtnType.operator),
    BTN_MINUS("-", BtnType.operator),
    BTN_MULTI("*", BtnType.operator),
    BTN_DIV("/", BtnType.operator),

    BTN_EQ("=", BtnType.equal),

    BTN_OCTAL("octal", BtnType.mode),
    BTN_DECIMAL("decimal", BtnType.mode),
    BTN_HEXADECIMAL("hexadecimal", BtnType.mode);

    enum BtnType {digit, mode, operator, functional, equal}

    private String literal;
    private BtnType type;

    CalculatorButton(String literal, BtnType type) {
        this.literal = literal;
        this.type = type;
    }

    public String getLiteral() {
        return literal;
    }

    public BtnType getType() {
        return type;
    }

    private static Map<String, CalculatorButton> literal2Button = new HashMap<>();

    static {
        for (CalculatorButton b : values()) {
            literal2Button.put(b.literal, b);
        }
    }

    public static CalculatorButton fromLiteral(String literal) {
        return literal2Button.get(literal);
    }
}