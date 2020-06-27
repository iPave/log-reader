package com.plarium.filereader.processing;

public interface Process<I, O> {

    O process(I input);

}
