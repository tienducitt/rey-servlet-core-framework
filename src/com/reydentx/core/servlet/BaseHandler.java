/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.core.servlet;

import com.reydentx.core.common.JSONUtils;
import com.reydentx.core.server.RWebServer;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author ducnt3
 */
public class BaseHandler extends HttpServlet {

        protected static final String CONTEXT_PATH = RWebServer.INSTANCE.getContextPath();

        protected static final Logger _LOG = Logger.getLogger(BaseHandler.class);

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        protected void printObjectAsJSON(Object object, HttpServletResponse resp) {
                try {
                        if (object != null) {
                                resp.setCharacterEncoding("UTF-8");
                                resp.getWriter().print(JSONUtils.toJson(object));
                        }
                } catch (Exception ex) {
                        _LOG.error(ex.getMessage(), ex);
                }
        }

        protected void printJSON(Object json, HttpServletResponse resp) {
                PrintWriter out = null;
                try {
                        resp.setContentType("text/json;charset=UTF-8");
                        resp.setHeader("Connection", "Close");
                        resp.setStatus(HttpServletResponse.SC_OK);
                        out = resp.getWriter();
                        out.print(json);
                } catch (IOException ex) {
                        _LOG.error(ex.getMessage(), ex);
                } finally {
                        if (out != null) {
                                out.close();
                        }
                }
        }

        protected void printHTML(Object obj, HttpServletResponse response) {
                print(obj, "text/html;charset=UTF-8", null, response);
        }

        protected void printHTMLWithGZip(byte[] obj, HttpServletResponse response) {
                printWithGZip(obj, null, "text/html;charset=UTF-8", response);
        }

        protected void printHTMLWithGZip(byte[] obj, String etag, HttpServletResponse response) {
                printWithGZip(obj, etag, "text/html;charset=UTF-8", response);
        }

        protected void printJSONWithGZip(byte[] obj, HttpServletResponse response) {
                printWithGZip(obj, null, "text/json;charset=UTF-8", response);
        }

        private void printWithGZip(byte[] obj, String etag, String contentType, HttpServletResponse response) {
                try {
                        response.setContentType(contentType);
                        response.setCharacterEncoding("UTF-8");
                        response.setHeader("Connection", "Close");
                        response.setHeader("Content-Encoding", "gzip");
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setContentLength(obj.length);
                        if (etag != null) {
                                response.setHeader("ETag", etag);
                        }

                        ServletOutputStream stream = null;
                        BufferedInputStream buf = null;
                        stream = response.getOutputStream();

                        ByteArrayInputStream arrInput = new ByteArrayInputStream(obj);
                        buf = new BufferedInputStream(arrInput);

                        byte[] bb = new byte[response.getBufferSize()];
                        int readByte;
                        while ((readByte = buf.read(bb, 0, bb.length)) != -1) {
                                stream.write(bb, 0, readByte);
                        }
                        buf.close();
                        stream.flush();
                        stream.close();
                } catch (IOException ex) {
                        _LOG.error(ex.getMessage(), ex);
                }
        }

        protected void printHTMLNotModified(HttpServletResponse resp, String etag) {
                printNotModified(resp, etag, "text/html;charset=UTF-8");
        }

        protected void printJSONNotModified(HttpServletResponse resp, String etag) {
                printNotModified(resp, etag, "text/json;charset=UTF-8");
        }

        private void printNotModified(HttpServletResponse resp, String etag, String contentType) {
                resp.setContentType(contentType);
                resp.setHeader("Connection", "Close");
                resp.setHeader("ETag", etag);
                resp.setHeader("Content-Length", "0");
                resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }

        protected void printHTMLWithEtag(Object obj, String etag, HttpServletResponse response) {
                print(obj, "text/html;charset=UTF-8", etag, response);
        }

        protected void print(Object obj, String contentType, String etag, HttpServletResponse response) {
                PrintWriter out = null;
                try {
                        response.setContentType(contentType);
                        response.setCharacterEncoding("UTF-8");
                        response.setHeader("Connection", "Close");
                        if (etag != null) {
                                response.setHeader("ETag", etag);
                        }
                        response.setStatus(HttpServletResponse.SC_OK);
                        out = response.getWriter();
                        out.print(obj);
                        out.flush();
                } catch (IOException ex) {
                        _LOG.error(ex.getMessage(), ex);
                } finally {
                        if (out != null) {
                                out.close();
                        }
                }
        }

}
