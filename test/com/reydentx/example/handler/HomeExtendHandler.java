/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.example.handler;

import com.reydentx.core.annotation.RController;
import com.reydentx.core.annotation.RServletMapping;
import com.reydentx.core.entity.RequestMethod;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ducnt3
 */
@RController(url = "home")
public class HomeExtendHandler {

        @RServletMapping(url = "/another", method = RequestMethod.GET)
        public void home(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                resp.getWriter().write("Hello world, this is my custome framework");
                resp.getWriter().close();
        }
}
