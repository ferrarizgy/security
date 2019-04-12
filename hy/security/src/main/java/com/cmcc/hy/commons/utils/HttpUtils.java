package com.cmcc.hy.commons.utils;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

@Component
public class HttpUtils {
    @Value("#{db.readTimeOut}")
    private int readTimeOut;
    @Value("#{db.connectionTimeOut}")
    private int connectionTimeOut;

    public int getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public int getConnectionTimeOut() {
        return connectionTimeOut;
    }

    public void setConnectionTimeOut(int connectionTimeOut) {
        this.connectionTimeOut = connectionTimeOut;
    }

    public HttpURLConnection openConnection(URL url)throws IOException{
        URLConnection urlConnection=url.openConnection();
        if(urlConnection instanceof HttpsURLConnection){
            return null;
        }
        HttpURLConnection conn=(HttpURLConnection)urlConnection;
        System.out.println(readTimeOut);
        conn.setReadTimeout(readTimeOut);//读取超时
        conn.setRequestMethod("GET");//设置请求方法
        conn.addRequestProperty("Accept-Charset","UTF-8;");
        conn.setConnectTimeout(connectionTimeOut);//设置超时时间5秒
        conn.setInstanceFollowRedirects(false);
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        return conn;
    }
    public void disconnect(HttpURLConnection conn){
        if (conn!=null){
            try{
                conn.disconnect();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
