package com.letcs.calculator.operation;

import com.letcs.calculator.CalculatorMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LongOperand implements Operand {
    final static Logger logger = LoggerFactory.getLogger(LongOperand.class);

    private long value = 0L;
    private boolean isInit = false;

    public LongOperand() {
    }

    public LongOperand(long value) {
        this.value = value;
        this.isInit = true;
    }

    @Override
    public String toString() {
        return toDisplay();
    }

    @Override
    public boolean tryToAppend(String input, CalculatorMode mode) {
        long i;
        switch(mode){
            case decimal:
                i = Long.parseLong(input);
                break;
            case octal:
                i = Long.parseLong(input,8);
                break;
            case hexadecimal:
                i = Long.parseLong(input,16);
                break;
            default:
                throw new RuntimeException("Not supported calculator mode!");
        }
        if (willMulOverflow(10, value)) {
            logger.warn(String.format("Operand overflow, current: %d, try to append: %s", value, input));
            return false;
        }
        else {
            long temp = 10 * value;
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
        long r = value % 10;
        value = (value - r) / 10;
    }

    @Override
    public void onesComp() {
        this.value = Long.MAX_VALUE - this.value;
    }

    @Override
    public void twosComp() {
        this.value = Long.MAX_VALUE - this.value + 1;
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
        StringBuilder temp = new StringBuilder();
        for (int i = 63; i >= 32; i--) {
            temp.append(((value & (1L << i)) == 0)?"0":"1");
            if(i == 48) temp.append(" ");
        }
        temp.append(" ");
        return temp.toString();
    }

    @Override
    public String toLowerBinaryDisplay() {
        StringBuilder temp = new StringBuilder();
        for (int i = 31; i >= 0; i--) {
            temp.append(((value & (1L << i)) == 0)?"0":"1");
            if(i == 16) temp.append(" ");
        }
        temp.append(" ");
        return temp.toString();
    }

    @Override
    public Operand copy() {
        return new LongOperand(value);
    }

    @Override
    public boolean isInit() {
        return isInit;
    }

    private boolean willAddOverflow(long a, long b) {
        long r = a + b;
        return ((a ^ r) & (b ^ r)) < 0;
    }

    private boolean willMulOverflow(long x, long y) {
        long r = x * y;
        long ax = Math.abs(x);
        long ay = Math.abs(y);
        if (((ax | ay) >>> 31 != 0)) {
            // Some bits greater than 2^31 that might cause overflow
            // Check the result using the divide operator
            // and check for the special case of Long.MIN_VALUE * -1
            return ((y != 0) && (r / y != x)) ||
                    (x == Long.MIN_VALUE && y == -1);
        }
        return false;
    }

    public enum Operations implements Operation {
        ADD() {
            @Override
            public Operand execute(Operand op1, Operand op2) {
                long res = (long) op1.getValue() + (long) op2.getValue();
                return new LongOperand(res);
            }
        },
        SUB() {
            @Override
            public Operand execute(Operand op1, Operand op2) {
                long res = (long) op1.getValue() - (long) op2.getValue();
                return new LongOperand(res);
            }
        },
        MUL() {
            @Override
            public Operand execute(Operand op1, Operand op2) {
                    long res = (long) op1.getValue() * (long) op2.getValue();
                    return new LongOperand(res);

            }
        },
        DIV() {
            @Override
            public Operand execute(Operand op1, Operand op2) {
                long res = (long) op1.getValue() / (long) op2.getValue();
                return new LongOperand(res);
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