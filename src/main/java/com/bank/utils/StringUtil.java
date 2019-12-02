package com.bank.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class StringUtil {
	
	
	public static String stream2String(InputStream in, String charset) {
        StringBuffer sb = new StringBuffer();
        try {
            Reader r = new InputStreamReader(in, charset);
            int length = 0;
            for (char[] c = new char[10240]; (length = r.read(c)) != -1; ) {
                sb.append(c, 0, length);
            }
            r.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
	
    public static Map<String, String> getMap(String params){
    	
        HashMap<String, String> map = new HashMap<>();

        int start = 0, len = params.length();

        while (start < len) {
            int i = params.indexOf('&', start);

            if (i == -1) {
                i = params.length(); // 此时处理最后的键值对
            }

            String keyValue = params.substring(start, i);

            int j = keyValue.indexOf('=');
            String key = keyValue.substring(0, j);
            String value = keyValue.substring(j + 1, keyValue.length());
  
            if(key.equals("name")){
            	try {
					value =   URLDecoder.decode(value, "utf-8");
				} catch (UnsupportedEncodingException e) {					
					e.printStackTrace();
				}
            }
            
            map.put(key, value);

            if (i == params.length()) {
                break;
            }

            start = i + 1; // index+1 为下一个键值对的起始位置
        }

        return map;
    }
    

}
