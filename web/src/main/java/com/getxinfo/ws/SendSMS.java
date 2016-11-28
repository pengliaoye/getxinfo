/**
 * SendSMS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.getxinfo.ws;

public interface SendSMS extends java.rmi.Remote {
    public java.lang.String sendSMS(java.lang.String uc, java.lang.String pw, java.lang.String rand, java.lang.String[] callee, java.lang.String isreturn, java.lang.String cont, int msgid, java.lang.String connID) throws java.rmi.RemoteException;
    public java.lang.String sendSMSV2(java.lang.String uc, java.lang.String pw, java.lang.String rand, java.lang.String[] callee, java.lang.String isreturn, java.lang.String cont, int msgID, java.lang.String connID, int charset) throws java.rmi.RemoteException;
    public java.lang.String sendSMSV3(java.lang.String uc, java.lang.String pw, java.lang.String rand, java.lang.String[] callee, java.lang.String isreturn, java.lang.String cont, int msgID, java.lang.String connID, int charset, java.lang.String signCont) throws java.rmi.RemoteException;
    public java.lang.String qryOffLineSMS(java.lang.String ucNumber, java.lang.String ucPinNum, java.lang.String rand, java.lang.String connID) throws java.rmi.RemoteException;
    public java.lang.String qrySendSMSStat(java.lang.String ucNumber, int smsFlag, java.lang.String ucPinNum, java.lang.String rand, java.lang.String connID) throws java.rmi.RemoteException;
}
