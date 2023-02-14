package com.letcs.calculator;

import com.letcs.calculator.operation.IntOperand;
import com.letcs.calculator.operation.LongOperand;
import com.letcs.calculator.operation.Operand;
import com.letcs.calculator.operation.Operation;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.Properties;

public class CalculatorContext {
    /*
     * State of the calculator
     */
    public Operand newOperand() {
        if (operandSizeInByte == 4) {
            return new IntOperand();
        } else if (operandSizeInByte == 8) {
            return new LongOperand();
        } else {
            throw new Error(String.format("Not supported operand size %d", operandSizeInByte));
        }
    }

    public Operation getOperation(String operator) {
        if (operandSizeInByte == 4) {
            return IntOperand.Operations.lookup(operator);
        } else if (operandSizeInByte == 8) {
            return LongOperand.Operations.lookup(operator);
        }
        else {
            throw new Error(String.format("Not supported operand size %d", operandSizeInByte));
        }
    }

    /*
     * Config Parser
     */
    private final Properties properties = new Properties();
    private int operandSizeInByte = 0;
    private String version = null;

    private void setupProperties() {
        operandSizeInByte = Integer.parseInt(properties.getProperty("operand_size", "4"));
        version = properties.getProperty("version", "1.0");
    }

    public String getVersion() {
        return "v" + version;
    }

    public void setOperandSizeInByte(int operandSizeInByte) {
        this.operandSizeInByte = operandSizeInByte;
    }

    /*
     * GUI Control
     */
    private CalculatorGUIController controller;
    private Scene scene;
    private boolean isDisplayOff = false;

    /**
     * Change the display content in the GUI interface
     *
     * @param currentOperand  the operand being handled currently whose value needs to be updated in both decimal(main)
     *                        and binary displays
     */
    public void submitDisplayChange(Operand currentOperand) {
        if (isDisplayOff) return;
        controller.changeDisplay(currentOperand.toDisplay(), currentOperand.toUpperBinaryDisplay(), currentOperand.toLowerBinaryDisplay());
    }



    /**
     * Disable a digit button, other types of button should not be disabled at any time
     *
     * @param button the button need to be disabled
     */
    public void disableButton(CalculatorButton button) {
        if (button.getType() == CalculatorButton.BtnType.digit) {
            Button btn = (Button) scene.lookup("#" + button);
            btn.setDisable(true);
        }
    }

    /**
     * Enable a button in the GUI interface
     *
     * @param button the button need to be enabled
     */
    public void enableButton(CalculatorButton button) {
        Button btn = (Button) scene.lookup("#" + button);
        btn.setDisable(false);
    }

    public void setController(CalculatorGUIController controller) {
        if (this.controller != null) return;
        this.controller = controller;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void forceDisplayOff() {
        isDisplayOff = true;
    }

    /*
     * Singleton the CalculatorContext
     */
    public static CalculatorContext getInstance() {
        return Instance.context;
    }

    private CalculatorContext() {
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("calculator.config"));
            setupProperties();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class Instance {
        private static final CalculatorContext context = new CalculatorContext();
    }
}