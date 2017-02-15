/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.core.entity;

/**
 *
 * @author ducnt3
 */
public enum ErrorCode {
        
        FAIL(-1),
        SUCCESS(0);
        
        private int value;
        
        ErrorCode(int value) {
                this.value = value;
        }
        
        public int getValue() {
                return value;
        }
}
