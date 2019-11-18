package com.fishdong.helloworld;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

@SpringBootApplication
@RestController
public class HelloworldApplication {
	
	private Logger log=LoggerFactory.getLogger(this.getClass());

    public static void main(String[] args) {
        SpringApplication.run(HelloworldApplication.class, args);
    }

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String sayHello()
    {
        return "Hello World.";
    }
    
    @RequestMapping(value = "/getWxConfig",method = RequestMethod.GET)
    public String getWxConfig(){
    	String resultStr=getConfitStr();
        return resultStr;
    }
   
    
    /**
     * 解决跨域请求数据
     * @param response
     * @param callbackName 前端回调函数名
     * @return
     */
    @RequestMapping(value = "getWxConfig1")
    public void getWxConfig1(HttpServletResponse response, @RequestParam String callbackName) {
    	
    	String resultStr=getConfitStr();
      response.setContentType("text/javascript");
      Writer writer = null;
      try {
       writer = response.getWriter();
       writer.write(callbackName + "(");
       writer.write(resultStr);
       writer.write(");");
      } catch (IOException e) {
       log.error("jsonp响应写入失败！ 数据：" + resultStr, e);
      } finally {
       if (writer != null) {
       try {
        writer.close();
       } catch (IOException e) {
        log.error("输出流关闭异常！", e);
       }
       writer = null;
       }
      }
    }
    
    public String getConfitStr() {
    	String token=WxUtils.getToken();
		String ticket=WxUtils.getJsapiTicket(token);
		String noncestr=WxUtils.CreatenNonceStr();
		String url="http://www.sibeis.cn";
		long timestamp=System.currentTimeMillis();
		String timestampStr=String.valueOf(timestamp).substring(0,10);
		log.info("timestampStr...{}",timestampStr);
		String result=WxUtils.shar1(ticket, noncestr, timestampStr, url);
		log.info("--------------------");
		log.info("ticket....{}",result);
		String agentConfigTicket=WxUtils.getAcJsapiTicket(token);
		String agentConfigShar1=WxUtils.shar1(agentConfigTicket, noncestr, timestampStr, url);
		JSONObject json=new JSONObject();
		json.put("timestamp", timestampStr);
		json.put("noncestr", noncestr);
		json.put("signature", result);
		json.put("agsignature", agentConfigShar1);
		json.put("ticket", ticket);
		json.put("agentConfigTicket", agentConfigTicket);
		String resultStr=json.toString();
		log.info("resultStr....{}",resultStr);
		
		return resultStr;
    }
}
