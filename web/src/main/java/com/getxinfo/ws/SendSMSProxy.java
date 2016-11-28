package com.getxinfo.ws;

public class SendSMSProxy implements com.getxinfo.ws.SendSMS {
  private String _endpoint = null;
  private com.getxinfo.ws.SendSMS sendSMS = null;
  
  public SendSMSProxy() {
    _initSendSMSProxy();
  }
  
  public SendSMSProxy(String endpoint) {
    _endpoint = endpoint;
    _initSendSMSProxy();
  }
  
  private void _initSendSMSProxy() {
    try {
      sendSMS = (new com.getxinfo.ws.SendSMSServiceLocator()).getSendSMS();
      if (sendSMS != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)sendSMS)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)sendSMS)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (sendSMS != null)
      ((javax.xml.rpc.Stub)sendSMS)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.getxinfo.ws.SendSMS getSendSMS() {
    if (sendSMS == null)
      _initSendSMSProxy();
    return sendSMS;
  }
  
  public java.lang.String sendSMS(java.lang.String uc, java.lang.String pw, java.lang.String rand, java.lang.String[] callee, java.lang.String isreturn, java.lang.String cont, int msgid, java.lang.String connID) throws java.rmi.RemoteException{
    if (sendSMS == null)
      _initSendSMSProxy();
    return sendSMS.sendSMS(uc, pw, rand, callee, isreturn, cont, msgid, connID);
  }
  
  public java.lang.String sendSMSV2(java.lang.String uc, java.lang.String pw, java.lang.String rand, java.lang.String[] callee, java.lang.String isreturn, java.lang.String cont, int msgID, java.lang.String connID, int charset) throws java.rmi.RemoteException{
    if (sendSMS == null)
      _initSendSMSProxy();
    return sendSMS.sendSMSV2(uc, pw, rand, callee, isreturn, cont, msgID, connID, charset);
  }
  
  public java.lang.String sendSMSV3(java.lang.String uc, java.lang.String pw, java.lang.String rand, java.lang.String[] callee, java.lang.String isreturn, java.lang.String cont, int msgID, java.lang.String connID, int charset, java.lang.String signCont) throws java.rmi.RemoteException{
    if (sendSMS == null)
      _initSendSMSProxy();
    return sendSMS.sendSMSV3(uc, pw, rand, callee, isreturn, cont, msgID, connID, charset, signCont);
  }
  
  public java.lang.String qryOffLineSMS(java.lang.String ucNumber, java.lang.String ucPinNum, java.lang.String rand, java.lang.String connID) throws java.rmi.RemoteException{
    if (sendSMS == null)
      _initSendSMSProxy();
    return sendSMS.qryOffLineSMS(ucNumber, ucPinNum, rand, connID);
  }
  
  public java.lang.String qrySendSMSStat(java.lang.String ucNumber, int smsFlag, java.lang.String ucPinNum, java.lang.String rand, java.lang.String connID) throws java.rmi.RemoteException{
    if (sendSMS == null)
      _initSendSMSProxy();
    return sendSMS.qrySendSMSStat(ucNumber, smsFlag, ucPinNum, rand, connID);
  }
  
  
}