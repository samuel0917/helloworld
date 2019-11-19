package com.fishdong.helloworld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fishdong.helloworld.wx.AesException;
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
		log.info("approveResult sart......");
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
        try {
        	receivePostJson(request);
        }catch (Exception e) {
			e.printStackTrace();
		}
        

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
	
	public String receivePostJson(HttpServletRequest request){
		BufferedReader br=null;
		try{
			 // 读取请求内容
	        br = new BufferedReader(new InputStreamReader(request.getInputStream()));
	        String line = null;
	        StringBuilder sb = new StringBuilder();
	        while((line = br.readLine())!=null){
	            sb.append(line);
	        }
	        // 将资料解码
	        String reqBody = sb.toString();
	        log.info("reqBody...{}" + reqBody);  
	        return URLDecoder.decode(reqBody, "UTF-8");
		}catch(Exception e){
			log.error("receivePostJson error...",e);
		}finally {
				try {
					if(br!=null)
					br.close();
				} catch (IOException e) {
					log.error("receivePostJson IOException...",e);
				}
		}
		return null;
    }

}
