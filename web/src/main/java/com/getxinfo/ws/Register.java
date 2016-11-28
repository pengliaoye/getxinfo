/**
 * Register.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.getxinfo.ws;

public interface Register extends java.rmi.Remote {
    public java.lang.String getRandom() throws java.rmi.RemoteException;
    public java.lang.String setCallBackAddr(java.lang.String uc, java.lang.String pw, java.lang.String rand, java.lang.String url) throws java.rmi.RemoteException;
    public java.lang.String setCallBackAddrV2(java.lang.String uc, java.lang.String pw, java.lang.String rand, java.lang.String url, java.lang.String version) throws java.rmi.RemoteException;
}
