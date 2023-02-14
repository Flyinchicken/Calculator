import com.letcs.calculator.CalculatorButton;
import com.letcs.calculator.CalculatorContext;
import com.letcs.calculator.CalculatorCore;
import com.letcs.calculator.operation.Operand;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntOperandTest {

    CalculatorCore core = new CalculatorCore();

    List<CalculatorButton> btnFromString(String btnSeq) {
        return Arrays.stream(btnSeq.split(" "))
                .map(CalculatorButton::fromLiteral)
                .collect(Collectors.toList());
    }

    String intToBtnSeq(int i) {
        StringBuilder sb = new StringBuilder();
        String str = String.valueOf(i);
        for (int k = 0; k < str.length(); k++) {
            char ch = str.charAt(k);
            sb.append(ch).append(" ");
        }
        return sb.toString();
    }

    String strToBtnSeq(String str) {
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < str.length(); k++) {
            char ch = str.charAt(k);
            sb.append(ch).append(" ");
        }
        return sb.toString();
    }

    @BeforeAll
    static void setup() {
        CalculatorContext.getInstance().forceDisplayOff();
    }

    @Test
    public void overflow1() {
        int maxInt = Integer.MAX_VALUE;
        List<CalculatorButton> btns = btnFromString(intToBtnSeq(maxInt));
        btns.forEach(btn -> core.onButtonClicked(btn));
        Operand actual = core.getCurrentOperand();
        assertEquals(maxInt, (int) actual.getValue());
    }

    @Test
    public void overflow2() {
        BigInteger maxInt = BigInteger.valueOf(Integer.MAX_VALUE);
        BigInteger maxIntPlusOne = maxInt.add(BigInteger.valueOf(1));
        String str = maxIntPlusOne.toString();
        List<CalculatorButton> btns = btnFromString(strToBtnSeq(str));

        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < str.length() - 1; k++) {
            sb.append(str.charAt(k));
        }
        String expected = sb.toString();

        btns.forEach(btn -> core.onButtonClicked(btn));
        Operand actual = core.getCurrentOperand();
        assertEquals(Integer.valueOf(expected), (int) actual.getValue());
    }

    @Test
    public void simple() {
        String btnSeq = "1 + 6 =";
        int expected = 7;
        List<CalculatorButton> btns = btnFromString(btnSeq);
        btns.forEach(btn -> core.onButtonClicked(btn));
        Operand actual = core.getCurrentResult();
        assertEquals(expected, (int) actual.getValue());
    }

    @Test
    public void addMultipleTimes1() {
        String btnSeq = "1 + 2 = + 3 =";
        int expected = 6;
        List<CalculatorButton> btns = btnFromString(btnSeq);
        btns.forEach(btn -> core.onButtonClicked(btn));
        Operand actual = core.getCurrentResult();
        assertEquals(expected, (int) actual.getValue());
    }

    @Test
    public void addMultipleTimes2() {
        String btnSeq = "1 + 2 = + 3 +";
        int expected = 6;
        List<CalculatorButton> btns = btnFromString(btnSeq);
        btns.forEach(btn -> core.onButtonClicked(btn));
        Operand actual = core.getCurrentResult();
        assertEquals(expected, (int) actual.getValue());
    }

    @Test
    public void singleOperandAndMultipleEqualOperation() {
        String btnSeq = "1 + = = = =";
        int expected = 5;
        List<CalculatorButton> btns = btnFromString(btnSeq);
        btns.forEach(btn -> core.onButtonClicked(btn));
        Operand actual = core.getCurrentResult();
        assertEquals(expected, (int) actual.getValue());
    }

    @Test
    public void multipleEqualOperation2() {
        String btnSeq = "2 + 3 = = = =";
        int expected = 14;
        List<CalculatorButton> btns = btnFromString(btnSeq);
        btns.forEach(btn -> core.onButtonClicked(btn));
        Operand actual = core.getCurrentResult();
        assertEquals(expected, (int) actual.getValue());
    }

    @Test
    public void twoCompletedAddition() {
        String btnSeq = "1 + 3 = 1 + 3";
        int expected = 4;
        List<CalculatorButton> btns = btnFromString(btnSeq);
        btns.forEach(btn -> core.onButtonClicked(btn));
        Operand actual = core.getCurrentResult();
        assertEquals(expected, (int) actual.getValue());
    }

    @Test
    public void additionAndMultiplication() {
        String btnSeq = "1 + 3 = * =";
        int expected = 16;
        List<CalculatorButton> btns = btnFromString(btnSeq);
        btns.forEach(btn -> core.onButtonClicked(btn));
        Operand actual = core.getCurrentResult();
        assertEquals(expected, (int) actual.getValue());
    }
}