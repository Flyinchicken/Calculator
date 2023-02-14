package com.letcs.calculator.operation;

import com.letcs.calculator.CalculatorMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntOperand implements Operand {
    final static Logger logger = LoggerFactory.getLogger(IntOperand.class);

    private int value = 0;
    private boolean isInit = false;

    public IntOperand() {
    }

    public IntOperand(int value) {
        this.value = value;
        this.isInit = true;
    }

    @Override
    public String toString() {
        return toDisplay();
    }

    @Override
    public boolean tryToAppend(String input, CalculatorMode mode) {
        int i;
        switch(mode){
            case decimal:
                i = Integer.parseInt(input);
                break;
            case octal:
                i = Integer.parseInt(input,8);
                break;
            case hexadecimal:
                i = Integer.parseInt(input,16);
                break;
            default:
                throw new RuntimeException("Not supported calculator mode!");
        }

        if (willMulOverflow(10, value)) {
            logger.warn(String.format("Operand overflow, current: %d, try to append: %s", value, input));
            return false;
        }
        else {
            int temp = 10 * value;
            if (willAddOverflow(temp, i)) {
                logger.warn(String.format("Operand overflow, current: %d, try to append: %s", value, input));
                return false;
            }
            else {
                value = temp + i;
                isInit = true;
                return true;
            }
        }
    }

    @Override
    public void back() {
        int r = value % 10;
        value = (value - r) / 10;
    }

    @Override
    public void onesComp() {this.value = Integer.MAX_VALUE - this.value;}

    @Override
    public void twosComp() {
        this.value = Integer.MAX_VALUE - this.value + 1;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String toDisplay() {
        return String.valueOf(value);
    }

    @Override
    public String toUpperBinaryDisplay() {
        return " ";
    }

    @Override
    public String toLowerBinaryDisplay() {
        StringBuilder temp = new StringBuilder();
        for (int i = 31; i >= 0; i--) {
            temp.append(((value & (1 << i)) == 0)?"0":"1");
            if(i == 16) temp.append(" ");
        }
        return temp.toString();
    }

    @Override
    public Operand copy() {
        return new IntOperand(value);
    }

    @Override
    public boolean isInit() {
        return isInit;
    }

    private boolean willAddOverflow(int a, int b) {
        int r = a + b;
        return ((a ^ r) & (b ^ r)) < 0;
    }

    private boolean willMulOverflow(int a, int b) {
        long r = (long) a * (long) b;
        return (int) r != r;
    }

    public enum Operations implements Operation {
        ADD() {
            @Override
            public Operand execute(Operand op1, Operand op2) {
                int res = (int) op1.getValue() + (int) op2.getValue();
                return new IntOperand(res);
            }
        },
        SUB() {
            @Override
            public Operand execute(Operand op1, Operand op2) {
                int res = (int) op1.getValue() - (int) op2.getValue();
                return new IntOperand(res);
            }
        },
        MUL() {
            @Override
            public Operand execute(Operand op1, Operand op2) {
                int res = (int) op1.getValue() * (int) op2.getValue();
                return new IntOperand(res);
            }
        },
        DIV() {
            @Override
            public Operand execute(Operand op1, Operand op2) {
                int res = (int) op1.getValue() / (int) op2.getValue();
                return new IntOperand(res);
            }
        };

        public static Operation lookup(String operator) {
            switch (operator) {
                case "+":
                    return ADD;
                case "-":
                    return SUB;
                case "*":
                    return MUL;
                case "/":
                    return DIV;
                default:
                    throw new RuntimeException("Not supported operator: " + operator);
            }
        }
    }
}