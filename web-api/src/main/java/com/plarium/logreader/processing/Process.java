package com.plarium.logreader.processing;

public interface Process<I, O> {

    O process(I input) throws Exception;

}
