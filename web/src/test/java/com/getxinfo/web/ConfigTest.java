package com.getxinfo.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Base64;

import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.getxinfo.util.HmacUtil;
import com.getxinfo.util.Md5Util;
import com.getxinfo.wsdl.RegisterService;
import com.getxinfo.wsdl.RegisterServiceLocator;
import com.getxinfo.wsdl.Register_PortType;
import com.getxinfo.wsdl.SendSMSService;
import com.getxinfo.wsdl.SendSMSServiceLocator;
import com.getxinfo.wsdl.SendSMS_PortType;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;


public class ConfigTest {
	
	@Test
	public void testGetRegister() throws Exception{
		String uc = "";
		String pwd = "";
		String message = "发送消息";
		String callbackUrl = "http://210.21.97.30:8080/axis1/services/myWebService";
		int msgID = 99999;
		
		String cont = Base64.getEncoder().encodeToString(message.getBytes("GBK"));
		
		RegisterService service = new RegisterServiceLocator(); 
		Register_PortType register = service.getRegister();
		String rand = register.getRandom();		
		System.out.println("rand="+rand);
		
		String pw = Md5Util.encode(rand + pwd + pwd);
			
		String connID = "810284186940176432";
		//String connID = register.setCallBackAddrV2(uc, pw, rand, callbackUrl, "2.0");
		//System.out.println("connID="+connID);
		
		SendSMSService smsService = new SendSMSServiceLocator();
		SendSMS_PortType sendSms = smsService.getSendSMS();
		String str = sendSms.sendSMSV2(uc, pw, rand, new String[]{"13658422301", "13389600105"}, "1", cont, msgID, connID, 15);		
		System.out.println("sms_return="+str);
	}	
	
	@Test
	public void testHmac(){
		String key = HmacUtil.getHmaSHA256key();
		System.out.println(key);		
		String str = HmacUtils.hmacSha256Hex(key, "123");
		System.out.println(str);
		String str1 = HmacUtils.hmacSha256Hex(key, "123");
		System.out.println(str1);
	}
		
	@Test
	public void testConfig(){
		Config conf = ConfigFactory.load();
		int bar1 = conf.getInt("foo.bar");
		Assert.assertEquals(10, bar1);
	}
	
	@Test
	public void testInt(){
		int i = 1049999990;
		float f = (float)i;
		String str = String.format("%f", f);
		System.out.println(str);
	}
	
	
    @Test
    public void testGetAvatar() throws Exception{
    	File file = new File("C:/Users/pgy/Desktop/下载图片/url.txt");
        WebDriver driver = new HtmlUnitDriver();
        for(int i = 0; i < 1300; i++){
        	if(i == 0){
        		driver.get("http://www.woyaogexing.com/touxiang/nv/index.html");
        	} else {
        		driver.get("http://www.woyaogexing.com/touxiang/nv/index_" + i + ".html");
        	}	        
	        String source = driver.getPageSource();
	        Document doc = Jsoup.parse(source);
	        Elements eles = doc.select(".txList img");
	        for (Element ele : eles) {
	            String src = ele.attr("src") + "\n";
	            System.out.println(src);
	            
	            FileUtils.writeStringToFile(file, src, Charset.forName("UTF-8"), true);
	            
				/*URL website = new URL(src);
				ReadableByteChannel rbc = Channels.newChannel(website.openStream());
				FileOutputStream fos = new FileOutputStream("C:/Users/pgy/Desktop/" + UUID.randomUUID().toString() + ".jpg");
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);*/
	            
	            // downloadUsingStream(src, "C:/Users/pgy/Desktop/" + UUID.randomUUID().toString() + ".jpg");
	        }           
        }
    }
    
    private static void downloadUsingStream(String urlStr, String file) throws IOException{
        URL url = new URL(urlStr);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }

}
