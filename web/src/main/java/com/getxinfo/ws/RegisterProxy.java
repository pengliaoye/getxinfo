package com.getxinfo.ws;

public class RegisterProxy implements com.getxinfo.ws.Register {
  private String _endpoint = null;
  private com.getxinfo.ws.Register register = null;
  
  public RegisterProxy() {
    _initRegisterProxy();
  }
  
  public RegisterProxy(String endpoint) {
    _endpoint = endpoint;
    _initRegisterProxy();
  }
  
  private void _initRegisterProxy() {
    try {
      register = (new com.getxinfo.ws.RegisterServiceLocator()).getRegister();
      if (register != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)register)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)register)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (register != null)
      ((javax.xml.rpc.Stub)register)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.getxinfo.ws.Register getRegister() {
    if (register == null)
      _initRegisterProxy();
    return register;
  }
  
  public java.lang.String getRandom() throws java.rmi.RemoteException{
    if (register == null)
      _initRegisterProxy();
    return register.getRandom();
  }
  
  public java.lang.String setCallBackAddr(java.lang.String uc, java.lang.String pw, java.lang.String rand, java.lang.String url) throws java.rmi.RemoteException{
    if (register == null)
      _initRegisterProxy();
    return register.setCallBackAddr(uc, pw, rand, url);
  }
  
  public java.lang.String setCallBackAddrV2(java.lang.String uc, java.lang.String pw, java.lang.String rand, java.lang.String url, java.lang.String version) throws java.rmi.RemoteException{
    if (register == null)
      _initRegisterProxy();
    return register.setCallBackAddrV2(uc, pw, rand, url, version);
  }
  
  
}