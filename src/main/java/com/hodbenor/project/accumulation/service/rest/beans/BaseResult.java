package com.hodbenor.project.accumulation.service.rest.beans;

import lombok.Getter;

@Getter
public class BaseResult {
    public BaseResult(int errorCode) {
        this(errorCode, "Success");
    }

    public BaseResult(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    private final int errorCode;
    private final String errorMessage;
}
