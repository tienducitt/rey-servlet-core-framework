/*
 * This shjt belong to ReydentX
 */
package com.reydentx.core.common;

/**
 *
 * @author Duc
 */
public enum ECode {
    FAIL(-1), SUCCESS(0);

    private final int value;

    ECode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
