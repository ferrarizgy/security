package com.cmcc.hy.commons.utils;

import com.alibaba.fastjson.JSON;
import com.cmcc.hy.pojo.ResultVo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {
    //输出流读取输出流写出去，边读边写
    public static void writeToOutStream(InputStream in,OutputStream os){
        try {
            int len=0;
            byte[] buf=new byte[1024];
            while ((len=in.read(buf))!=-1){
                os.write(buf,0,len);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            //关闭流
            IOUtils.close(in,os);
        }
    }
    //关闭输入输出流
    public static void close(InputStream in, OutputStream os){
       close(in);
       close(os);
    }
    public static void close(InputStream in){
        if (in!=null){
            try {
                in.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public static void close(OutputStream os){
        if (os!=null){
            try {
                os.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public static void writeToJson(HttpServletResponse response, ResultVo resultVo){
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        String json= JSON.toJSONString(resultVo);
        OutputStream os=null;
        try {
            os=response.getOutputStream();
            byte[] bytes=json.getBytes();
            os.write(bytes);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            close(os);
        }
    }
}
