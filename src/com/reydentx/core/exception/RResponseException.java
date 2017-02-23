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
        
        public static RResponseException newInvalidDataEx() {
                return new RResponseException("Thao tác thất bại, dữ liệu không hợp lệ.");
        }
        
        public static RResponseException newNotExistDataEx() {
                return new RResponseException("Thao tác thất bại, dữ liệu không tồn tại.");
        }
        
        public static RResponseException newPermissionDeniEx() {
                return new RResponseException("Bạn không có quyền thực hiện thao tác này.");
        }
        
        public static RResponseException newActionFailEx(int err) {
                return new RResponseException("Thao tác thất bại, vui lòng thử lại sau (" + err + ")");
        }
        
        public static RResponseException newEx(String message) {
                return new RResponseException(message);
        }
        
        private RResponseException(String message) {
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
