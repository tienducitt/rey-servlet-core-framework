/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.core.entity;

import java.util.Objects;

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

        @Override
        public int hashCode() {
                int hash = 7;
                return hash;
        }

        @Override
        public boolean equals(Object obj) {
                if (obj == null) {
                        return false;
                }
                if (getClass() != obj.getClass()) {
                        return false;
                }
                final RequestInfo other = (RequestInfo) obj;
                if (!Objects.equals(this.path, other.path)) {
                        return false;
                }
                if (this.requestMethod != other.requestMethod) {
                        return false;
                }
                return true;
        }
        
        
}
