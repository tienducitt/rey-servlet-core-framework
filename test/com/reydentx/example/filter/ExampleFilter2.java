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

/**
 *
 * @author ducnt3
 */
public class ExampleFilter2 implements Filter{

        @Override
        public void init(FilterConfig fc) throws ServletException {
                
        }

        @Override
        public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc) throws IOException, ServletException {
                
                System.out.println("Before filter 2");
                fc.doFilter(sr, sr1);
                System.out.println("After filter 2");
        }

        @Override
        public void destroy() {
                
        }
        
}
