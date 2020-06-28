package com.plarium.logreader.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class LogRequestDto {

    @NotNull
    @NotEmpty
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
