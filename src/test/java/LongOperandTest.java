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

public class LongOperandTest {

    CalculatorCore core = new CalculatorCore();

    List<CalculatorButton> btnFromString(String btnSeq) {
        return Arrays.stream(btnSeq.split(" "))
                .map(CalculatorButton::fromLiteral)
                .collect(Collectors.toList());
    }

    String longToBtnSeq(long i) {
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
        CalculatorContext.getInstance().setOperandSizeInByte(8);
    }

    @Test
    public void overflow1() {
        long maxInt = Integer.MAX_VALUE;
        List<CalculatorButton> btns = btnFromString(longToBtnSeq(maxInt));
        btns.forEach(btn -> core.onButtonClicked(btn));
        Operand actual = core.getCurrentOperand();
        assertEquals(maxInt, (long) actual.getValue());
    }

    @Test
    public void overflow2() {
        BigInteger maxInt = BigInteger.valueOf(Integer.MAX_VALUE);
        BigInteger maxIntPlusOne = maxInt.add(BigInteger.valueOf(1));
        String str = maxIntPlusOne.toString();
        List<CalculatorButton> btns = btnFromString(strToBtnSeq(str));

        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < str.length(); k++) {
            sb.append(str.charAt(k));
        }
        String expected = sb.toString();

        btns.forEach(btn -> core.onButtonClicked(btn));
        Operand actual = core.getCurrentOperand();
        assertEquals(Long.valueOf(expected), (long) actual.getValue());
    }

    @Test
    public void overflow3() {
        long maxLong = Long.MAX_VALUE;
        List<CalculatorButton> btns = btnFromString(longToBtnSeq(maxLong));
        btns.forEach(btn -> core.onButtonClicked(btn));
        Operand actual = core.getCurrentOperand();
        assertEquals(maxLong, (long) actual.getValue());
    }

    @Test
    public void overflow4() {
        BigInteger maxLong = BigInteger.valueOf(Long.MAX_VALUE);
        BigInteger maxLongPlusOne = maxLong.add(BigInteger.valueOf(1));
        String str = maxLongPlusOne.toString();
        List<CalculatorButton> btns = btnFromString(strToBtnSeq(str));

        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < str.length() - 1; k++) {
            sb.append(str.charAt(k));
        }
        String expected = sb.toString();

        btns.forEach(btn -> core.onButtonClicked(btn));
        Operand actual = core.getCurrentOperand();
        assertEquals(Long.valueOf(expected), (long) actual.getValue());
    }

    @Test
    public void simple() {
        String btnSeq = "1 + 6 =";
        long expected = 7;
        List<CalculatorButton> btns = btnFromString(btnSeq);
        btns.forEach(btn -> core.onButtonClicked(btn));
        Operand actual = core.getCurrentResult();
        assertEquals(expected, (long) actual.getValue());
    }

    @Test
    public void addMultipleTimes1() {
        String btnSeq = "1 + 2 = + 3 =";
        long expected = 6;
        List<CalculatorButton> btns = btnFromString(btnSeq);
        btns.forEach(btn -> core.onButtonClicked(btn));
        Operand actual = core.getCurrentResult();
        assertEquals(expected, (long) actual.getValue());
    }

    @Test
    public void addMultipleTimes2() {
        String btnSeq = "1 + 2 = + 3 +";
        long expected = 6;
        List<CalculatorButton> btns = btnFromString(btnSeq);
        btns.forEach(btn -> core.onButtonClicked(btn));
        Operand actual = core.getCurrentResult();
        assertEquals(expected, (long) actual.getValue());
    }

    @Test
    public void singleOperandAndMultipleEqualOperation() {
        String btnSeq = "1 + = = = =";
        long expected = 5;
        List<CalculatorButton> btns = btnFromString(btnSeq);
        btns.forEach(btn -> core.onButtonClicked(btn));
        Operand actual = core.getCurrentResult();
        assertEquals(expected, (long) actual.getValue());
    }

    @Test
    public void multipleEqualOperation2() {
        String btnSeq = "2 + 3 = = = =";
        long expected = 14;
        List<CalculatorButton> btns = btnFromString(btnSeq);
        btns.forEach(btn -> core.onButtonClicked(btn));
        Operand actual = core.getCurrentResult();
        assertEquals(expected, (long) actual.getValue());
    }

    @Test
    public void twoCompletedAddition() {
        String btnSeq = "1 + 3 = 1 + 3 =";
        long expected = 4;
        List<CalculatorButton> btns = btnFromString(btnSeq);
        btns.forEach(btn -> core.onButtonClicked(btn));
        Operand actual = core.getCurrentResult();
        assertEquals(expected, (long) actual.getValue());
    }

    @Test
    public void additionAndMultiplication() {
        String btnSeq = "1 + 3 = * =";
        long expected = 16;
        List<CalculatorButton> btns = btnFromString(btnSeq);
        btns.forEach(btn -> core.onButtonClicked(btn));
        Operand actual = core.getCurrentResult();
        assertEquals(expected, (long) actual.getValue());
    }

    @Test
    public void changeOperator() {
        String btnSeq = "1 + * + * 3 =";
        long expected = 3;
        List<CalculatorButton> btns = btnFromString(btnSeq);
        btns.forEach(btn -> core.onButtonClicked(btn));
        Operand actual = core.getCurrentResult();
        assertEquals(expected, (long) actual.getValue());
    }

    @Test
    public void sequenceOfDiffOperations() {
        String btnSeq = "1 + 3 * 2 - 5 +";
        long expected = 3;
        List<CalculatorButton> btns = btnFromString(btnSeq);
        btns.forEach(btn -> core.onButtonClicked(btn));
        Operand actual = core.getCurrentResult();
        assertEquals(expected, (long) actual.getValue());
    }

    @Test
    public void sequenceOfSameOperators() {
        String btnSeq = "1 + + + + +";
        long expected = 5;
        List<CalculatorButton> btns = btnFromString(btnSeq);
        btns.forEach(btn -> core.onButtonClicked(btn));
        Operand actual = core.getCurrentResult();
        assertEquals(expected, (long) actual.getValue());
    }
}