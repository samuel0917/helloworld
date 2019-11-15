package com.fishdong.helloworld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    public JSONObject getWxConfig(){
    	String token=WxUtils.getToken();
		String ticket=WxUtils.getJsapiTicket(token);
		String noncestr=WxUtils.CreatenNonceStr();
		String url="http://www.sibeis.cn";
		long timestamp=System.currentTimeMillis();
		log.debug(""+timestamp);
		String result=WxUtils.shar1(ticket, noncestr, timestamp, url);
		log.debug("--------------------");
		log.debug(result);
		JSONObject json=new JSONObject();
		json.put("timestamp", timestamp);
		json.put("noncestr", noncestr);
		json.put("signature", result);
        return json;
    }
    @RequestMapping(value = "/getWxConfigStr",method = RequestMethod.GET)
    public String getWxConfigStr(){
    	String token=WxUtils.getToken();
		String ticket=WxUtils.getJsapiTicket(token);
		String noncestr=WxUtils.CreatenNonceStr();
		String url="http://www.sibeis.cn";
		long timestamp=System.currentTimeMillis();
		log.debug(""+timestamp);
		String result=WxUtils.shar1(ticket, noncestr, timestamp, url);
		log.debug("--------------------");
		log.debug(result);
		JSONObject json=new JSONObject();
		json.put("timestamp", timestamp);
		json.put("noncestr", noncestr);
		json.put("signature", result);
        return json.toJSONString();
    }
    
    @RequestMapping(value = "/getWxConfigStr2",method = RequestMethod.GET)
    public String getWxConfigStr2(){
    	String token=WxUtils.getToken();
		String ticket=WxUtils.getJsapiTicket(token);
		String noncestr=WxUtils.CreatenNonceStr();
		String url="http://www.sibeis.cn";
		long timestamp=System.currentTimeMillis();
		log.debug(""+timestamp);
		String result=WxUtils.shar1(ticket, noncestr, timestamp, url);
		log.debug("--------------------");
		log.debug(result);
		JSONObject json=new JSONObject();
		json.put("timestamp", timestamp);
		json.put("noncestr", noncestr);
		json.put("signature", result);
        return json.toString();
    }

}
