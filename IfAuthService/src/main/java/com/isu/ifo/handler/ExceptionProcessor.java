package com.isu.ifo.handler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ExceptionProcessor {
    
    public void makeExceptionResponse(HttpServletRequest request, HttpServletResponse response, Exception exception) throws IOException;
    
}


