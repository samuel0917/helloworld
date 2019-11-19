package com.fishdong.helloworld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.alibaba.fastjson.JSON;
import com.fishdong.helloworld.wx.AesException;
import com.fishdong.helloworld.wx.MessageUtil;
import com.fishdong.helloworld.wx.WXBizMsgCrypt;

@RestController
public class WxNotification {
	
	 public final static String token = "usJvjizOebHzV";
	    // encodingAESKey
	 public final static String encodingAESKey = "GQzIcAdTACXhXJeyU6KWrDa8omMsojGUtXokXk2SHR3";
	    //企业ID
	 public final static String corpId = "wxc99729d223a63c12";
	    //应用的凭证密钥
	 public final static String corpsecret = "asT_0RWy2FP6MNBid3dSAmVomqw676uzHI0z7Sbfiws";
	 
	 private Logger log=LoggerFactory.getLogger(this.getClass());
	
	@GetMapping("approveResult")
	public void approveResult(HttpServletRequest request, HttpServletResponse response) {
		log.info("approveResult get sart......");
		// 微信加密签名  
        String msg_signature = request.getParameter("msg_signature");  
        log.info("msg_signature......{}",msg_signature);
        // 时间戳  
        String timestamp = request.getParameter("timestamp");  
        log.info("timestamp......{}",timestamp);
        // 随机数  
        String nonce = request.getParameter("nonce");  
        log.info("nonce......{}",nonce);
        // 随机字符串  
        String echostr = request.getParameter("echostr");  
        log.info("echostr......{}",echostr);

        log.info("request=" + request.getRequestURL());  

        PrintWriter out=null;
		try {
			out = response.getWriter();
			  // 通过检验msg_signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败  
	        String result = null;  
	        try {
	        	WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token,encodingAESKey, corpId);  
	            result = wxcpt.VerifyURL(msg_signature, timestamp, nonce, echostr);  
	            log.info("VerifyURL result......{}",result);
	        } catch (AesException e) {  
	            e.printStackTrace();  
	        }  
	        if (result == null) {  
	            result = token;  
	        }  
	        out.print(result);  
	        
		} catch (IOException e1) {
			e1.printStackTrace();
		}  finally {
			if(out!=null) out.close(); 
		}
      
		
	}
	
	@PostMapping("approveResult")
	public void approveResultPost(HttpServletRequest request) {
		log.info("approveResult post sart......");
		// 微信加密签名  
        String msg_signature = request.getParameter("msg_signature");  
        log.info("msg_signature......{}",msg_signature);
        // 时间戳  
        String timestamp = request.getParameter("timestamp");  
        log.info("timestamp......{}",timestamp);
        // 随机数  
        String nonce = request.getParameter("nonce");  
        log.info("nonce......{}",nonce);
        // 随机字符串  
        String echostr = request.getParameter("echostr");  
        log.info("echostr......{}",echostr);
        log.info("request...{}",request.getRequestURL());  
		try {
			  // 通过检验msg_signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败  
	        String result = null;  
	        	InputStream inputStream = request.getInputStream();
		        String sPostData = IOUtils.toString(inputStream,"UTF-8");
		        log.info("sPostData......{}",sPostData);
	        	WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token,encodingAESKey, corpId);  
//	            result = wxcpt.VerifyURL(msg_signature, timestamp, nonce, echostr);  
	        	result = wxcpt.DecryptMsg(msg_signature,timestamp,nonce,sPostData);
	            log.info("DecryptMsg result......{}",result);
	            /*
	            企业收到post请求之后应该		1.解析出url上的参数，包括消息体签名(msg_signature)，时间戳(timestamp)以及随机数字串(nonce)
	    		2.验证消息体签名的正确性。
	    		3.将post请求的数据进行xml解析，并将<Encrypt>标签的内容进行解密，解密出来的明文即是用户回复消息的明文，明文格式请参考官方文档
	    		第2，3步可以用企业微信提供的库函数DecryptMsg来实现。
	    		*/
	    		// post请求的密文数据
	    		// sReqData = HttpUtils.PostData();
//	    		String sReqData = "<xml><ToUserName><![CDATA[wx5823bf96d3bd56c7]]></ToUserName><Encrypt><![CDATA[RypEvHKD8QQKFhvQ6QleEB4J58tiPdvo+rtK1I9qca6aM/wvqnLSV5zEPeusUiX5L5X/0lWfrf0QADHHhGd3QczcdCUpj911L3vg3W/sYYvuJTs3TUUkSUXxaccAS0qhxchrRYt66wiSpGLYL42aM6A8dTT+6k4aSknmPj48kzJs8qLjvd4Xgpue06DOdnLxAUHzM6+kDZ+HMZfJYuR+LtwGc2hgf5gsijff0ekUNXZiqATP7PF5mZxZ3Izoun1s4zG4LUMnvw2r+KqCKIw+3IQH03v+BCA9nMELNqbSf6tiWSrXJB3LAVGUcallcrw8V2t9EL4EhzJWrQUax5wLVMNS0+rUPA3k22Ncx4XXZS9o0MBH27Bo6BpNelZpS+/uh9KsNlY6bHCmJU9p8g7m3fVKn28H3KDYA5Pl/T8Z1ptDAVe0lXdQ2YoyyH2uyPIGHBZZIs2pDBS8R07+qN+E7Q==]]></Encrypt><AgentID><![CDATA[218]]></AgentID></xml>";
	          
    			// TODO: 解析出明文xml标签的内容进行处理
	            log.info("parseXml result......{}",JSON.toJSONString(MessageUtil.parseXml(result)));
	    			
		} catch (Exception e) {
			// 解密失败，失败原因请查看异常
			e.printStackTrace();
		}
		
	}

}
