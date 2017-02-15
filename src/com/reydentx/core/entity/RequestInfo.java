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
public class RequestInfo {

        private String path;
        private RequestMethod requestMethod;

        public RequestInfo() {
        }

        public RequestInfo(String path, RequestMethod type) {
                this.path = path;
                this.requestMethod = type;
        }

        public String getPath() {
                return path;
        }

        public void setPath(String path) {
                this.path = path;
        }

        public RequestMethod getRequestMethod() {
                return requestMethod;
        }

        public void setRequestMethod(RequestMethod type) {
                this.requestMethod = type;
        }
}
