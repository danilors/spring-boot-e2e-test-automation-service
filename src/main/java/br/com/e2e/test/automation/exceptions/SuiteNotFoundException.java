package br.com.e2e.test.automation.exceptions;

public class SuiteNotFoundException extends RuntimeException {
    public SuiteNotFoundException(String message) {
        super(message);
    }
}
