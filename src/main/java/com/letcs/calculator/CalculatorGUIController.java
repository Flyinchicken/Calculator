package com.letcs.calculator;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalculatorGUIController {
    final static Logger logger = LoggerFactory.getLogger(CalculatorGUIController.class);

    @FXML
    private Label mainLabel;
    @FXML

    private Label upperBinaryLabel;
    @FXML
    private Label lowerBinaryLabel;

    private ButtonClickedListener listener;

    public void setListener(ButtonClickedListener listener) {
        this.listener = listener;
    }

    @FXML
    private void onClickButton(MouseEvent event) {
        String buttonID = ((ButtonBase) event.getSource()).getId();
        CalculatorButton btn = CalculatorButton.valueOf(buttonID.toUpperCase());
        if (btn.getType() == CalculatorButton.BtnType.mode) {
            logger.debug("You select [" + btn.getLiteral() + "] mode");
        }
        else {
            logger.debug("You clicked <" + btn.getType() + "> button [" + btn.getLiteral() + "]");
        }
        if (listener != null) {
            listener.onButtonClicked(btn);
        }
    }

    public void changeDisplay(String mainDisplay, String upperBinaryDisplay, String lowerBinaryDisplay) {
        mainLabel.setText(mainDisplay);
        upperBinaryLabel.setText(upperBinaryDisplay);
        lowerBinaryLabel.setText(lowerBinaryDisplay);
    }
}