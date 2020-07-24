```
package com.lcm.ssl.demo.controller;

import com.lcm.ssl.demo.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
public class McmController {

    @Autowired
    RestTemplate restTemplate;

    @PostMapping("mcm/index")
    public void index(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            OutputStream out = new FileOutputStream("D:\\aa.txt");
            InputStream is = new ByteArrayInputStream(bytes);
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = is.read(buff)) != -1) {
                out.write(buff, 0, len);
            }
            is.close();
            out.close();
            // 封装参数和头信息
            String url = "http://localhost:8080/file/upload";
            ResponseEntity<String> mapResponseEntity = restTemplate.postForEntity(url, null, String.class);
            System.out.println(mapResponseEntity.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @PostMapping("mcm/index1")
    public void index1(MultipartFile file) {
       try {
           HttpHeaders headers = new HttpHeaders();
           headers.setContentType(MediaType.MULTIPART_FORM_DATA);
           FileSystemResource resource = new FileSystemResource(new File("D:\\aa.txt"));
           MultiValueMap<String, Object> form = new LinkedMultiValueMap<String, Object>();
           User user=new User(1,"admin");
           // post的文件
           form.add("data", resource);
           // post的表单参数
           form.add("name", user);

           //用HttpEntity封装整个请求报文
           HttpEntity<MultiValueMap<String, Object>> files = new HttpEntity<MultiValueMap<String, Object>>(form, headers);
           String ans = restTemplate.postForObject("http://localhost:8080/file/upload1", files, String.class);

       }catch (Exception e){
            e.printStackTrace();
       }
        //设置请求头

    }


    
}

```