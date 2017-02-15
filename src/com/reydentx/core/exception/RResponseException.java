/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.core.exception;

/**
 *
 * @author ducnt3
 */
public class RResponseException extends Exception {
        private int errorCode = -1;
        
        public RResponseException(String message) {
                super(message);
        }
        
        public RResponseException(String message, int errorCode) {
                super(message);
                this.errorCode = errorCode;
        }

        public int getErrorCode() {
                return errorCode;
        }

        public void setErrorCode(int errorCode) {
                this.errorCode = errorCode;
        }
}
