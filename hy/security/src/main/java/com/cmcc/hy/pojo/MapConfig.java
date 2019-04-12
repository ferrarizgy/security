package com.cmcc.hy.pojo;

import com.cmcc.hy.commons.utils.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapConfig implements Runnable{
    private static final MapConfig mapConfig=new MapConfig();
    private Map<String,String> UrlMap;
    private long loadTime;//记录配置文件更新时间戳
    private int interval;//配置文件更新时间间隔

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public long getLoadTime() {
        return loadTime;
    }

    public void setLoadTime(long loadTime) {
        this.loadTime = loadTime;
    }

    public Map<String, String> getUrlMap() {
        return UrlMap;
    }

    public void setUrlMap(Map<String, String> urlMap) {
        UrlMap = urlMap;
    }

    public static MapConfig getInstance(){
        return mapConfig;
    }
    public void reload(){
        synchronized (MapConfig.class){
            if(System.currentTimeMillis()-mapConfig.getLoadTime()>mapConfig.getInterval()){//每隔xx秒加载一次xml
                mapConfig.loadXML();//执行加载
                mapConfig.setLoadTime(System.currentTimeMillis());//设置加载的时间戳
            }
        }
    }
    //加载配置文件
    public void loadXML(){
        InputStream in=null;
        Map<String,String> map=mapConfig.getUrlMap();
        if (map==null){
            map=new HashMap<>();
        }
        try {
            System.out.println("开始加载:"+map);
            map.clear();
            System.out.println("清空map"+map);
            SAXReader reader=new SAXReader();
            in=this.getClass().getResourceAsStream("/Configuration.xml");
            Document doc=reader.read(in);
            Element element=doc.getRootElement();
            //更新加载文件时间间隔
            mapConfig.setInterval(Integer.parseInt(element.attributeValue("interval")));
            System.out.println("更新加载文件时间间隔:"+mapConfig.getInterval());
            List<Element> list=element.elements();
            //更新地址映射的map
            for (Element e:list){
                System.out.println("add key:"+e.attributeValue("requestUrl"));
                System.out.println("add value:"+e.attributeValue("toURL"));
                map.put(e.attributeValue("requestUrl"),e.attributeValue("toURL"));
            }
            mapConfig.setUrlMap(map);
            System.out.println("loadXML()加载xml完成:"+map);
        }catch (DocumentException e){
            e.printStackTrace();
        }finally {
            IOUtils.close(in);
        }
    }

    @Override
    public void run() {
        mapConfig.reload();
    }
}
