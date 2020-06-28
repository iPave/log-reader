package com.plarium.logreader.processing;

import java.util.ArrayList;
import java.util.List;

public class Chain {

    private List<Process<?, ?>> processChain = new ArrayList<>();

    private Chain() {
    }

    public static Chain createStart(Process<?, ?> process) {
        Chain chain = new Chain();
        chain.processChain.add(process);
        return chain;
    }

    public Chain append(Process<?, ?> process) {
        this.processChain.add(process);
        return this;
    }

    public Object start(Object source) throws Exception {
        Object target = null;
        for (Process process : processChain) {
            target = process.process(source);
            source = target;
        }
        return target;
    }

}
