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
	
	private String token=null;
	
	private String ticket=null;
	
	private String agTicket=null;
	
	private String url="http://www.sibeis.cn/";

    public static void main(String[] args) {
        SpringApplication.run(HelloworldApplication.class, args);
    }

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String sayHello()
    {
        return "Hello World.";
    }
    
   
    /**
     * 解决跨域请求数据
     * @param response
     * @param callbackName 前端回调函数名
     * @return
     */
    @RequestMapping(value = "getWxAgConfig")
    public void getWxAgConfig(HttpServletResponse response, @RequestParam String callbackName) {
    	
    	if(token==null)
        	token=WxUtils.getToken();
    		
    		if(agTicket==null)
    			agTicket=WxUtils.getAcJsapiTicket(token);
    		String agnoncestr=WxUtils.CreatenNonceStr();
    		long agtimestamp=System.currentTimeMillis();
    		String agtimestampStr=String.valueOf(agtimestamp).substring(0,10);
    		String agentConfigShar1=WxUtils.shar1(agTicket, agnoncestr, agtimestampStr, url);
    		JSONObject json=new JSONObject();
    		json.put("agtimestampStr", agtimestampStr);
    		json.put("agnoncestr", agnoncestr);
    		json.put("agsignature", agentConfigShar1);
    		json.put("agentConfigTicket", agTicket);
    		String resultStr=json.toString();
    	
    	
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
    
    /**
     * 解决跨域请求数据
     * @param response
     * @param callbackName 前端回调函数名
     * @return
     */
    @RequestMapping(value = "getWxConfig")
    public void getWxConfig(HttpServletResponse response, @RequestParam String callbackName) {
    	
    	if(token==null)
        	token=WxUtils.getToken();
        	if(ticket==null)
    		ticket=WxUtils.getJsapiTicket(token);
    		String noncestr=WxUtils.CreatenNonceStr();
    		
    		long timestamp=System.currentTimeMillis();
    		String timestampStr=String.valueOf(timestamp).substring(0,10);
    		log.info("timestampStr...{}",timestampStr);
    		String result=WxUtils.shar1(ticket, noncestr, timestampStr, url);
    		log.info("--------------------");
    		log.info("ticket....{}",result);
    	
    		JSONObject json=new JSONObject();
    		json.put("timestamp", timestampStr);
    		json.put("noncestr", noncestr);
    		json.put("signature", result);
    		json.put("ticket", ticket);
    		String resultStr=json.toString();
    	
    	
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
    	if(token==null)
    	token=WxUtils.getToken();
    	if(ticket==null)
		ticket=WxUtils.getJsapiTicket(token);
		String noncestr=WxUtils.CreatenNonceStr();
		long timestamp=System.currentTimeMillis();
		String timestampStr=String.valueOf(timestamp).substring(0,10);
		log.info("timestampStr...{}",timestampStr);
		String result=WxUtils.shar1(ticket, noncestr, timestampStr, url);
		log.info("--------------------");
		log.info("ticket....{}",result);
		
		if(agTicket==null)
			agTicket=WxUtils.getAcJsapiTicket(token);
		String agnoncestr=WxUtils.CreatenNonceStr();
		long agtimestamp=System.currentTimeMillis();
		String agtimestampStr=String.valueOf(agtimestamp).substring(0,10);
		String agentConfigShar1=WxUtils.shar1(agTicket, agnoncestr, agtimestampStr, url);
		JSONObject json=new JSONObject();
		json.put("timestamp", timestampStr);
		json.put("noncestr", noncestr);
		json.put("signature", result);
		json.put("ticket", ticket);
		
		json.put("agtimestampStr", agtimestampStr);
		json.put("agnoncestr", agnoncestr);
		json.put("agsignature", agentConfigShar1);
		json.put("agentConfigTicket", agTicket);
		String resultStr=json.toString();
		log.info("resultStr....{}",resultStr);
		
		return resultStr;
    }
}
