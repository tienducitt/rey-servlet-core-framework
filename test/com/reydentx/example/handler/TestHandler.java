/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.example.handler;

import com.reydentx.core.annotation.RController;
import com.reydentx.core.annotation.RServletMapping;
import com.reydentx.core.entity.RequestMethod;
import com.reydentx.core.servlet.BaseHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ducnt3
 */
@RController(url = "/test")
public class TestHandler extends BaseHandler {
        
        @RServletMapping(url = "/*", method = RequestMethod.GET)
        public void renderHtml0(HttpServletRequest req, HttpServletResponse resp) {
                printHTML("Test", resp);
        }
        
        @RServletMapping(url = "/detail", method = RequestMethod.GET)
        public void renderHtml(HttpServletRequest req, HttpServletResponse resp) {
                printHTML("Test detail", resp);
        }
        
        @RServletMapping(url = "/detail/more", method = RequestMethod.GET)
        public void renderHtml2(HttpServletRequest req, HttpServletResponse resp) {
                printHTML("Test more detail", resp);
        }
}
