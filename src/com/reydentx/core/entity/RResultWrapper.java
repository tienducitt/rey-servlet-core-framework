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
public class RResultWrapper {
        private int errCode;
        private String message;
        private Object data;

        //~~~~~~~~~ Factory method ~~~~~~~~
        
        public static RResultWrapper newSuccessResult() {
                return new RResultWrapper(ErrorCode.SUCCESS.getValue(), null, null);
        }
        
        public static RResultWrapper newSuccessResult(Object data) {
                return new RResultWrapper(ErrorCode.SUCCESS.getValue(), null, data);
        }
        
        public static RResultWrapper newSuccessResult(int errCode, Object data) {
                return new RResultWrapper(errCode, null, data);
        }
        
        public static RResultWrapper newErrorResult() {
                return new RResultWrapper(ErrorCode.FAIL.getValue(), null, null);
        }
        
        public static RResultWrapper newErrorResult(String message) {
                return new RResultWrapper(ErrorCode.FAIL.getValue(), message, null);
        }
        
        public static RResultWrapper newErrorResult(int errCode, String message) {
                return new RResultWrapper(errCode, message, null);
        }
        
        //~~~~~~~~~ Constructors ~~~~~~~~~~
        public RResultWrapper() {
        }

        public RResultWrapper(int errCode, String message, Object data) {
                this.errCode = errCode;
                this.message = message;
                this.data = data;
        }
        
        //~~~~~~~~~ getter & setter ~~~~~~~
        public int getErrCode() {
                return errCode;
        }

        public void setErrCode(int errCode) {
                this.errCode = errCode;
        }

        public String getMessage() {
                return message;
        }

        public void setMessage(String message) {
                this.message = message;
        }

        public Object getData() {
                return data;
        }

        public void setData(Object data) {
                this.data = data;
        }
}
