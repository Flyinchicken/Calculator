package com.letcs.calculator;

import com.letcs.calculator.operation.Operand;
import com.letcs.calculator.operation.OperandStack;
import com.letcs.calculator.operation.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalculatorCore implements ButtonClickedListener {
    final static Logger logger = LoggerFactory.getLogger(CalculatorCore.class);
    protected CalculatorContext context = CalculatorContext.getInstance();

    private CalculatorMode currentMode = CalculatorMode.decimal;
    private final OperandStack operandStack = new OperandStack();
    private Operand currentOperand = context.newOperand();
    private String currentOperator = null;
    private Operand savedOperand = null;
    private String savedOperator = null;
    private CalculatorButton.BtnType lastPressed = null;

    public void changeMode(CalculatorMode mode) {
        currentMode = mode;
        switch(mode){
            case decimal:
                context.disableButton(CalculatorButton.BTN_A);
                context.disableButton(CalculatorButton.BTN_B);
                context.disableButton(CalculatorButton.BTN_C);
                context.disableButton(CalculatorButton.BTN_D);
                context.disableButton(CalculatorButton.BTN_E);
                context.disableButton(CalculatorButton.BTN_F);
                context.enableButton(CalculatorButton.BTN_9);
                context.enableButton(CalculatorButton.BTN_8);
                break;
            case hexadecimal:
                context.enableButton(CalculatorButton.BTN_A);
                context.enableButton(CalculatorButton.BTN_B);
                context.enableButton(CalculatorButton.BTN_C);
                context.enableButton(CalculatorButton.BTN_D);
                context.enableButton(CalculatorButton.BTN_E);
                context.enableButton(CalculatorButton.BTN_F);
                context.enableButton(CalculatorButton.BTN_9);
                context.enableButton(CalculatorButton.BTN_8);
                break;
            case octal:
                context.disableButton(CalculatorButton.BTN_A);
                context.disableButton(CalculatorButton.BTN_B);
                context.disableButton(CalculatorButton.BTN_C);
                context.disableButton(CalculatorButton.BTN_D);
                context.disableButton(CalculatorButton.BTN_E);
                context.disableButton(CalculatorButton.BTN_F);
                context.disableButton(CalculatorButton.BTN_9);
                context.disableButton(CalculatorButton.BTN_8);
                break;
        }
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
                if("=".equals(currentOperator)){
                    operandStack.clear();
                    savedOperator = null;
                    savedOperand = null;
                }
                if (currentOperand.tryToAppend(input, currentMode)) {
                    context.submitDisplayChange(currentOperand);
                }
                break;
            case functional:
                if (button == CalculatorButton.BTN_AC) {
                    currentOperand = context.newOperand();
                    currentOperator = null;
                    savedOperand = null;
                    operandStack.clear();
                    context.submitDisplayChange(currentOperand);
                } else if (button == CalculatorButton.BTN_BACK) {
                    currentOperand.back();
                    context.submitDisplayChange(currentOperand);
                } else if (button == CalculatorButton.BTN_1SCOMP) {
                    if (operandStack.size() != 0) {
                        currentOperand = operandStack.top().copy();
                    }
                    currentOperand.onesComp();
                    context.submitDisplayChange(currentOperand);
                } else if (button == CalculatorButton.BTN_2SCOMP) {
                    if (operandStack.size() != 0) {
                        currentOperand = operandStack.top().copy();
                    }
                    currentOperand.twosComp();
                    context.submitDisplayChange(currentOperand);
                } else {
                    throw new RuntimeException(String.format("Unknown functional button: [%s]", button));
                }
                break;

            case equal:
                currentOperator = button.getLiteral();
                if (operandStack.size() == 0) {
                    operandStack.push(currentOperand);
                } else {
                    if (lastPressed == CalculatorButton.BtnType.operator){
                        if (savedOperand == null) {
                            savedOperand = operandStack.top().copy();
                        }
                        operandStack.push(savedOperand);
                        performOperation();
                    }
                    else if (lastPressed == CalculatorButton.BtnType.digit) {
                        operandStack.push(currentOperand);
                        savedOperand = currentOperand;
                        performOperation();
                    }
                    else if (lastPressed == CalculatorButton.BtnType.equal){
                        if (savedOperand == null) {
                            savedOperand = operandStack.top().copy();
                        }
                        operandStack.push(savedOperand);
                        performOperation();

                    }
                }
                currentOperand = context.newOperand();
                break;

            case operator:
                String operator = button.getLiteral();
                currentOperator = operator;
                if(operandStack.size() == 0){
                    operandStack.push(currentOperand);
                    savedOperator = operator;

                } else {
                    if (lastPressed == CalculatorButton.BtnType.digit) {
                        operandStack.push(currentOperand);
                        performOperation();
                        savedOperator = operator;

                    } else if (lastPressed == CalculatorButton.BtnType.equal) {
                        savedOperand = operandStack.top().copy();
                        savedOperator = operator;
                    } else if (lastPressed == CalculatorButton.BtnType.operator) {
                        if (!savedOperator.equals(operator)) {
                            savedOperand = null;
                            savedOperator = operator;
                        } else {
                            if (savedOperand == null) {
                                savedOperand = operandStack.top().copy();
                            }
                            operandStack.push(savedOperand);
                            performOperation();
                        }
                    }
                }
                currentOperand = context.newOperand();
                break;


            default:
                throw new RuntimeException(String.format("Not supported button type: %s", type));
        }
        lastPressed = type;
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
        Operand result = context.getOperation(savedOperator).execute(op1, op2);
        logger.debug(String.format("trigger operation %s %s %s = %s", op1, savedOperator, op2, result));
        context.submitDisplayChange(result);
        operandStack.clear();
        operandStack.push(result);
        savedOperand = op2;
    }
}