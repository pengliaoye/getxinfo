/**
 * SendSMSService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.getxinfo.wsdl;

public interface SendSMSService extends javax.xml.rpc.Service {
    public java.lang.String getSendSMSAddress();

    public com.getxinfo.wsdl.SendSMS_PortType getSendSMS() throws javax.xml.rpc.ServiceException;

    public com.getxinfo.wsdl.SendSMS_PortType getSendSMS(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
