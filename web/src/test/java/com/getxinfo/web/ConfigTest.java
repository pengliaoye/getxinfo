package com.getxinfo.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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
import org.springframework.http.HttpEntity;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.getxinfo.util.HmacUtil;
import com.getxinfo.util.Md5Util;
import com.getxinfo.web.ConfigTest.MiliyoResponse.MiliyoUser;
import com.getxinfo.ws.RegisterProxy;
import com.getxinfo.ws.SendSMSProxy;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import okhttp3.OkHttpClient;


public class ConfigTest {
	
	@Test
	public void testGetRegister() throws Exception{
		String uc = "";
		String pwd = "";
		String message = "发送消息";
		String callbackUrl = "http://210.21.97.30:8080/axis1/services/myWebService";
		int msgID = 99999;
		
		String cont = Base64.getEncoder().encodeToString(message.getBytes("GBK"));
		
		RegisterProxy registerProxy = new RegisterProxy(); 
		String rand = registerProxy.getRandom();		
		System.out.println("rand="+rand);
		
		String pw = Md5Util.encode(rand + pwd + pwd);
			
		//String connID = "810284186940176432";
		String connID = registerProxy.setCallBackAddrV2(uc, pw, rand, callbackUrl, "2.0");
		System.out.println("connID="+connID);
		
		SendSMSProxy smsProxy = new SendSMSProxy();
		String str = smsProxy.sendSMSV2(uc, pw, rand, new String[]{"13658422301", "13389600105"}, "1", cont, msgID, connID, 15);		
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
	public void getMiliyoAvatar() throws InterruptedException{
		String lastId = "1480657127391004";
		miliyoAvatar(lastId);
	}
	
	public void miliyoAvatar(String lastId) throws InterruptedException{
		OkHttpClient client = new OkHttpClient();
		OkHttp3ClientHttpRequestFactory requestFactory = new OkHttp3ClientHttpRequestFactory(client);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("User-Agent", "Android/NIUPAIClient V69 Screen[1440x2560]_ua");		
		List<String> urls = new ArrayList<>();
		for(int i=0; i < 10; i++){			
			Thread.sleep(5000);
			headers.add("cookie", "_mly_lang=cn; PHPSESSID=oavbfbja15c67j8ihvef6lggi0; app_install_0=1; ldi=99000579371965; uid=17471626; uca=dbb3e9e38c19c6e4bfa0040c6dc30e99; t=1480652761; route=8fdf6ba1ee2cad35a6a2ddc1daea3025; ses=c3bcf2cb74c9c05e07494ae20c6c861f;");
			HttpEntity<String> request = new HttpEntity<String>("last_id="+lastId+"&country_short=CN&loc_city=%E9%87", headers);
			MiliyoResponse resp = restTemplate.postForObject("http://mapi.miliyo.com/search/online?isMiui=0&_ua=a|6.0.1|0|69|qq|99000579371965|1440|2560|0|cn|c89001e8a84209e9b6abde363861e3d7&mac=02:00:00:00:00:00", request, MiliyoResponse.class);			
			List<MiliyoUser> users = resp.getList();
			for(MiliyoUser usr : users){
				urls.add(usr.getFace_url());
				System.out.println(usr.getFace_url());
				try {
					FileUtils.writeStringToFile(new File("C:/Users/pgy/Desktop/下载图片/miliyo.txt"), usr.getFace_url() + "\n", true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if( resp.getLast_id() != null){
				miliyoAvatar(resp.getLast_id());
			}
		}		
	}
	
	public static class MiliyoResponse {
		private String last_id;
		private List<MiliyoUser> list;
		public String getLast_id() {
			return last_id;
		}
		public void setLast_id(String last_id) {
			this.last_id = last_id;
		}
		public List<MiliyoUser> getList() {
			return list;
		}
		public void setList(List<MiliyoUser> list) {
			this.list = list;
		}
		public static class MiliyoUser {
			private String face_url;

			public String getFace_url() {
				return face_url;
			}

			public void setFace_url(String face_url) {
				this.face_url = face_url;
			}
		}
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
