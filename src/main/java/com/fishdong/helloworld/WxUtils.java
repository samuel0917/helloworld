package com.fishdong.helloworld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;


public class WxUtils {
	
	private static Logger log=LoggerFactory.getLogger(WxUtils.class);
	
	private static String[] strs = new String[]{"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	

	private static String weChatAppid="wx8a26edc25685559b";
	private static String weChatAppSecret="a7e20bedea69c278e2236aa8963dfd78";
	
	
	public static void main(String[] args) {
		String token=getToken();
		String ticket=getJsapiTicket(token);
		String noncestr=CreatenNonceStr();
		String url="http://www.sibeis.cn";
		long timestamp=System.currentTimeMillis();
		log.debug(""+timestamp);
		String result=shar1(ticket, noncestr, timestamp, url);
		log.debug("--------------------");
		log.debug(result);
	}
	

	
	/**
	 * 企业签名
	 * @param ticket
	 * @param noncestr
	 * @param timestamp
	 * @param url
	 */
	public static String shar1(String ticket,String noncestr,Long timestamp,String url) {
		//jsapi_ticket=sM4AOVdWfPE4DxkXGEs8VMCPGGVi4C3VM0P37wVUCFvkVAy_90u5h9nbSlYy3-Sl-HhTdfl2fzFy1AOcHKP7qg&noncestr=Wm3WZYTPz0wzccnW&timestamp=1414587457&url=http://mp.weixin.qq.com?params=value
		StringBuilder sb = new StringBuilder(); 
		sb.append("jsapi_ticket=").append(ticket).append("&")
		.append("noncestr=").append(noncestr).append("&")
		.append("timestamp=").append(timestamp).append("&")
		.append("url=").append(url);
		String result=DigestUtils.sha1Hex(sb.toString());
		return result;
		//a2c641189ac16997a42c69b40ed4cfde479a78a1
	}
	
	/**
	 * 获取企业的jsapi_ticket
	 * @return
	 */
	public static String getJsapiTicket(String token) {
		String url="https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token="+token;
		String result=sendGet(url);
		log.debug("get getJsapiTicket result...{}",result);
		JSONObject json = JSONObject.parseObject(result);
		String ticket=json.getString("ticket");
		log.debug(ticket);
		return ticket;
		//kgt8ON7yVITDhtdwci0qeRciXbcplrJ_Hc7NLZIdUnSkORSHLkSLUd_gBqRNfVO4HFoNxKn9WdfWeGUd4_u3NQ
	}
	
	/**
	 *  获取企业微信token
	 * @return
	 */
	public static String getToken() {
		String tokenUrl="https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=wxc99729d223a63c12&corpsecret=asT_0RWy2FP6MNBid3dSAmVomqw676uzHI0z7Sbfiws";
		String result=sendGet(tokenUrl);
		log.debug("get token result...{}",result);
		JSONObject json = JSONObject.parseObject(result);
		String token=json.getString("access_token");
		log.debug(token);
		return token;
		//bGGCjg_DlnYyMaHpr40i2CRuJ2ZX01iNYjMfvGfnhyWSovZrBCRZ3wcDGKEtKOuRKR2AmhI97-lgkS-3vlwGdVt7Zhb8EFzGhnPV0IWhLIULazBXtTocr7Mkhr5pcYFdvGJUA0fBQFDxNlpYwLulDez4lwRBFyWGzDQPar_4PG63A9Olk3hkFlc0oklWJK3jGmmkqPa90mbJt6-Ll3wcSA
	}
	/**“
	 * 生成随机随机字符串
	 * @return
	 */
	public static String CreatenNonceStr(){
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 15; i++)
        {
            sb.append(strs[r.nextInt(strs.length - 1)]);
        }
        
		log.debug("noncestr.......{}",sb.toString());
        return sb.toString();
    }
	

	public static String sendGet(String url) {
		String result = "";
		try {
			URL u = new URL(url);
			BufferedReader in=null;
			try {
				URLConnection conn = u.openConnection();
				conn.setConnectTimeout(10000);
				conn.setReadTimeout(10000);
				conn.setUseCaches(false);
				conn.connect();
				in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
				String line = "";
				while ((line = in.readLine()) != null) {
					result = result + line;
				}
			} catch (IOException e) {
				return result;
			}finally{
					try {
						if(in!=null)
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		} catch (MalformedURLException e) {
			return result;
		}
		return result;
	}
}
