/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itb;

/**
 *
 * @author Artem
 */
public class InvalidInputException extends Exception {
    
    static private String errorInfo;
    static private int errorCode;
    
    public InvalidInputException(String reason, String errorInfo, int errorCode) {
        super(reason);
        this.errorInfo = errorInfo;
        this.errorCode = errorCode;
    }
    
    public String getFaultInfo() {
        return errorInfo;
    }
    
        public int  getFaultCode() {
        return errorCode;
    }
    
}
