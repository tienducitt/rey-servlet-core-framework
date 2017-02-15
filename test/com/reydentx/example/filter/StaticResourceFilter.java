/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.example.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ducnt3
 */
public class StaticResourceFilter implements Filter{

        @Override
        public void init(FilterConfig fc) throws ServletException {
                
        }

        @Override
        public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc) throws IOException, ServletException {
                HttpServletRequest req = (HttpServletRequest) sr;
                HttpServletResponse resp = (HttpServletResponse) sr1;
                if(!isNotFoundStaticResource(req.getRequestURI())) { 
                        fc.doFilter(sr, sr1);
                } else {
                        //throw 404
                        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
        }
        
        public boolean isNotFoundStaticResource(String uri) {
                if(uri.startsWith("/public") || uri.endsWith(".ico")) {
                        return true;
                }
                
                return false;
        }
        
        @Override
        public void destroy() {
                
        }
        
}
