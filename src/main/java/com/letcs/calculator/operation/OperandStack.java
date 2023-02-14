package com.letcs.calculator.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class OperandStack {
    final static Logger logger = LoggerFactory.getLogger(OperandStack.class);

    private final Deque<Operand> stack = new ArrayDeque<>();

    public void clear() {
        stack.clear();
    }

    public Operand top() {
        return stack.getFirst();
    }

    public int size() {
        return stack.size();
    }

    public void push(Operand operand) {
        stack.push(operand);
        logger.debug(String.format("push operand %s to operand stack (size: %d)", operand, size()));
    }

    public Operand pop() {
        return stack.pop();
    }

    public Operand duplicate() {
        Operand another = top().copy();
        stack.push(another);
        return another;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Iterator<Operand> it = stack.descendingIterator();
        while(it.hasNext()) {
            sb.append(it.next().toString()).append(", ");
        }
        sb.append("] <- stack top");
        return sb.toString();
    }
}