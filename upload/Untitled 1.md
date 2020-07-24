```
package com.lcm.ssl.demo01.controller;

import com.lcm.ssl.demo01.vo.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
public class FileManagerController {


    @PostMapping("file/upload")
    public String uploadFile(){
        return "SUCESS";
    }

    @PostMapping(path = "file/upload1")
    public String upload(@RequestPart(name = "data") MultipartFile file, @RequestBody User name) throws IOException {
        String ans = new String(file.getBytes(), "utf-8") + "|" + name;
        return ans;
    }
    @GetMapping("file/download")
    public String  downloadFile(HttpServletResponse response){
        String filename="b.txt";
        String filePath = "D:/download" ;
        File file = new File(filePath + "/" + filename);
        if(file.exists()){ //判断文件父目录是否存在
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            // response.setContentType("application/force-download");
            try {
                response.setHeader("Content-Disposition", "attachment;fileName=" +   java.net.URLEncoder.encode(filename,"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            byte[] buffer = new byte[1024];
            FileInputStream fis = null; //文件输入流
            BufferedInputStream bis = null;

            OutputStream os = null; //输出流
            try {
                os = response.getOutputStream();
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                int i = bis.read(buffer);
                while(i != -1){
                    os.write(buffer);
                    i = bis.read(buffer);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("----------file download---" + filename);
            try {
                bis.close();
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }
}

```