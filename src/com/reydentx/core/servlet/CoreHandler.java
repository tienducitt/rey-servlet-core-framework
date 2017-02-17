/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.core.servlet;

import com.reydentx.core.common.ServletUtils;
import com.reydentx.core.container.ServletMappingContainer;
import com.reydentx.core.container.ServletMethod;
import com.reydentx.core.entity.RResultWrapper;
import com.reydentx.core.entity.RequestInfo;
import com.reydentx.core.exception.RNotExistHandler;
import com.reydentx.core.exception.RResponseException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ducnt3
 */
public class CoreHandler extends BaseHandler {

        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                RResultWrapper result = handle(req, resp);
                if (result != null) {
                        printObjectAsJSON(result, resp);
                }
        }

        public RResultWrapper handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                try {
                        RequestInfo reqInfo = ServletUtils.getRequestInfo(req, CONTEXT_PATH);
                        ServletMethod servletMethod = ServletMappingContainer.getServletMethod(reqInfo);

                        Object serviceResult = servletMethod.invoke(req, resp);
                        if (!servletMethod.isResponseBody()) {
                                return RResultWrapper.newSuccessResult(serviceResult);
                        }
                } catch (RNotExistHandler ex) {
                        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                        return RResultWrapper.newErrorResult(ex.getMessage());
                } catch (RResponseException ex) {
                        return RResultWrapper.newErrorResult(ex.getErrorCode(), ex.getMessage());
                } catch (Throwable ex) {
                        _LOG.error(ex.getMessage(), ex);
                }

                return null;
        }
}
