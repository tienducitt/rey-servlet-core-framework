/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.example.handler;

import com.reydentx.core.annotation.RController;
import com.reydentx.core.annotation.RResponseBody;
import com.reydentx.core.annotation.RServletMapping;
import com.reydentx.core.entity.ErrorCode;
import com.reydentx.core.entity.RequestMethod;
import com.reydentx.core.exception.RResponseException;
import com.reydentx.core.servlet.BaseHandler;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ducnt3
 */
@RController(url = "home")
public class ExampleHandler extends BaseHandler {

        @RServletMapping(url = "/getJSONData", method = RequestMethod.GET)
        public String renderExample(HttpServletRequest req, HttpServletResponse resp) throws IOException, RResponseException {
                
                return "Hello world, this is my custome framework";
        }
        
        @RServletMapping(url = "/renderHtml", method = RequestMethod.GET)
        public void renderHtml(HttpServletRequest req, HttpServletResponse resp) {
                printHTML("Some HTML go here", resp);
        }
        
        @RServletMapping(url = "/doSomethingWithError", method = RequestMethod.GET)
        public void doSomethingWithError(HttpServletRequest req, HttpServletResponse resp) throws IOException, RResponseException {
                throw new RResponseException("invalid data", ErrorCode.FAIL.getValue());
        }
        
        @RServletMapping(url = "/notThrowException", method = RequestMethod.GET)
        public void defaultWillBeSuccess(HttpServletRequest req, HttpServletResponse resp) throws IOException, RResponseException {
                
        }
        
        @RResponseBody
        @RServletMapping(url = "/doSomething", method = RequestMethod.POST)
        public void doSomething(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                
                resp.getWriter().write("Hello world, this is my custome framework");
                resp.getWriter().close();
        }
}
