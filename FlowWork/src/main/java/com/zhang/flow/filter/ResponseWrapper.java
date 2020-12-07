package com.zhang.flow.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author zhang
 */
public class ResponseWrapper extends HttpServletResponseWrapper {

    private ByteArrayOutputStream byteArrayOutputStream;
    private ServletOutputStream servletOutputStream;
    private String responseBody;


    public ResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);
        byteArrayOutputStream = new ByteArrayOutputStream();
        servletOutputStream = new MyServletOutputStream(byteArrayOutputStream);
//        if (Objects.nonNull(response.getOutputStream())) {
//            responseBody = response.getOutputStream().toString();
//        }
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return servletOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8));
    }

    @Override
    public void flushBuffer() {
        if (servletOutputStream != null) {
            try {
                servletOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getResponseBody() {
        flushBuffer();
        String string = new String(byteArrayOutputStream.toByteArray());
        return string;
    }

    public byte[] getResponseBodyByte() {
        flushBuffer();
        return byteArrayOutputStream.toByteArray();
    }

    class MyServletOutputStream extends ServletOutputStream {
        private ByteArrayOutputStream byteArrayOutputStream;

        public MyServletOutputStream(ByteArrayOutputStream byteArrayOutputStream) {
            this.byteArrayOutputStream = byteArrayOutputStream;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener listener) {
        }

        @Override
        public void write(int b) throws IOException {
            byteArrayOutputStream.write(b);
        }
    }
}