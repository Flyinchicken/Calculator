package com.letcs.calculator;

public interface ButtonClickedListener {
    /**
     * When a button is clicked in the GUI interface, this callback will be invoked
     * @param button the button being clicked
     */
    void onButtonClicked(CalculatorButton button);
}