package com.letcs.calculator;

import com.letcs.calculator.operation.Operand;
import com.letcs.calculator.operation.OperandStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalculatorCore implements ButtonClickedListener {
    final static Logger logger = LoggerFactory.getLogger(CalculatorCore.class);
    protected CalculatorContext context = CalculatorContext.getInstance();

    private CalculatorMode currentMode = CalculatorMode.decimal;
    private final OperandStack operandStack = new OperandStack();
    private Operand currentOperand = context.newOperand();
    private String currentOperator = null;
    private Operand savedOperand;

    public void changeMode(CalculatorMode mode) {
        currentMode = mode;
    }

    @Override
    public void onButtonClicked(CalculatorButton button) {
        CalculatorButton.BtnType type = button.getType();
        switch (type) {
            case mode:
                changeMode(CalculatorMode.valueOf(button.getLiteral()));
                break;
            case digit:
                String input = button.getLiteral();
                if (currentOperand.tryToAppend(input)) {
                    context.submitDisplayChange(CalculatorContext.DisplayType.MAIN, currentOperand.toDisplay());
                }
                break;
            case functional:
                if (button == CalculatorButton.BTN_AC) {
                    currentOperand = context.newOperand();
                    currentOperator = null;
                    savedOperand = null;
                    operandStack.clear();
                    context.submitDisplayChange(CalculatorContext.DisplayType.MAIN, currentOperand.toDisplay());
                } else if (button == CalculatorButton.BTN_BACK) {
                    currentOperand.back();
                    context.submitDisplayChange(CalculatorContext.DisplayType.MAIN, currentOperand.toDisplay());
                } else {
                    throw new RuntimeException(String.format("Unknown functional button: [%s]", button));
                }
                break;
            case operator:
                String operator = button.getLiteral();
                // hit "=" operator
                //   1. push current operand into the stack
                //      a. if current operand is not initialized, which mean user do something like "1 + ="
                //      b. if current operand contains user input, then user is doing something like "1 + 2 ="
                //   2. perform operation
                if ("=".equals(operator)) {
                    if (currentOperand.isInit()) {
                        operandStack.push(currentOperand);
                    } else {
                        if (savedOperand == null) {
                            savedOperand = operandStack.duplicate();
                        }
                        operandStack.push(savedOperand);
                    }
                    performOperation();
                    currentOperand = context.newOperand();
                }
                // hit operator rather than "="
                //   1. save the current operand to stack
                //   2. if stack size is 2, perform operation and save the result back to stack, wait for new operand
                else {
                    operandStack.push(currentOperand);
                    if (operandStack.size() == 2) {
                        performOperation();
                    }
                    currentOperand = context.newOperand();
                    currentOperator = operator;
                }
                break;
            default:
                throw new RuntimeException(String.format("Not supported button type: %s", type));
        }
    }


    public Operand getCurrentResult() {
        return operandStack.top();
    }

    public Operand getCurrentOperand() {
        return currentOperand;
    }

    private void performOperation() {
        Operand op2 = operandStack.pop();
        Operand op1 = operandStack.pop();
        Operand result = context.getOperation(currentOperator).execute(op1, op2);
        logger.debug(String.format("trigger operation %s %s %s = %s", op1, currentOperator, op2, result));
        context.submitDisplayChange(CalculatorContext.DisplayType.MAIN, result.toDisplay());
        operandStack.clear();
        operandStack.push(result);
        savedOperand = op2;
    }
}