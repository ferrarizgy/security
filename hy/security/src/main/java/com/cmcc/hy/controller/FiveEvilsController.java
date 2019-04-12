package com.cmcc.hy.controller;
import com.cmcc.hy.commons.ThreadPoolService;
import com.cmcc.hy.commons.utils.HttpUtils;
import com.cmcc.hy.commons.utils.IOUtils;
import com.cmcc.hy.pojo.MapConfig;
import com.cmcc.hy.pojo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;


@Controller
public class FiveEvilsController {
    @Autowired
    private HttpUtils httpUtils;
    @Autowired
    private ThreadPoolService pool;
    private MapConfig mapConfig;
    @RequestMapping(value = "**",method = RequestMethod.GET)
    public void middleware(HttpServletResponse response, HttpServletRequest request){
        pool.handle(mapConfig);
        String query=request.getQueryString();//获取请求参数
        String requestUri=request.getRequestURI();//获取请求路径URL
        String toURL=findToURL(requestUri,query);//根据前端传过来的请求URL去map里模糊/精确匹配找出对应的toURL
        System.out.println("toURL:"+toURL);
        System.out.println("当前的map:"+mapConfig.getUrlMap());
        HttpURLConnection conn=null;
        OutputStream os=null;
        InputStream in=null;
        try {
            //找不到匹配的toURL
            if(toURL==null){
                IOUtils.writeToJson(response,new ResultVo(404,"没有该接口",null));
                return;
            }
            URL u=new URL(toURL);
            conn=httpUtils.openConnection(u);
            os=response.getOutputStream();
            response.setContentType("application/json");
            if(conn.getResponseCode()==200){//连接状态200状态码正常响应
                //用getinputstream()获取输入流
                in=conn.getInputStream();
                IOUtils.writeToOutStream(in,os);//resonse输出流直接写出去
            }else{
                ResultVo resultVo=new ResultVo(conn.getResponseCode(),"接口状态异常",new Object());
                IOUtils.writeToJson(response,resultVo);
            }
        }catch (IOException e){
            e.printStackTrace();
            IOUtils.writeToJson(response,new ResultVo(501,"IO流异常",new Object()));
        }catch (Exception e){
            e.printStackTrace();
            IOUtils.writeToJson(response,new ResultVo(500,"系统未知异常",new Object()));
        }finally {
            IOUtils.close(in,os);
            httpUtils.disconnect(conn);
        }
    }
    @PostConstruct//启动项目执行一次
    public void init(){
        mapConfig=MapConfig.getInstance();
        System.out.println("mapconfig初始化"+mapConfig);
        mapConfig.loadXML();//初始化加载xml文件
        mapConfig.setLoadTime(System.currentTimeMillis());//设置初始化时间戳
    }
    //根据前端传过来的请求URL去map里模糊/精确匹配找出对应的toURL
    public String findToURL(String requestURL,String query){
        String toURL=null;
        Set<Map.Entry<String,String>> set=mapConfig.getUrlMap().entrySet();
        Iterator<Map.Entry<String,String>> it=set.iterator();
        while (it.hasNext()){
            Map.Entry<String,String> entry=it.next();
            String key=entry.getKey();
            String value=entry.getValue();
            //模糊匹配去掉末尾的*
            if(key.contains("*")){
                key=key.replace("*","");
            }
            if(value.contains("*")){
                value=value.replace("*","");
            }
            //找到匹配的key
            if (requestURL.contains(key)){
                requestURL=requestURL.substring(key.length());
                toURL=new StringBuffer(value).append(requestURL).toString();
                break;
            }
        }
        return toURL;
    }
}
