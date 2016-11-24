package com.getxinfo.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;


public class ConfigTest {
		
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
