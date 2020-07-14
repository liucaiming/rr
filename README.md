package com.lcm.it.spring.boot.controller;



import com.lcm.it.spring.boot.service.IUserManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
@RequestMapping("exception")
public class HelloController {

    @Autowired
    private IUserManagerService iUserManagerService;

    @RequestMapping(value="/test",method = RequestMethod.GET)
        String testException(){
        try {
            iUserManagerService.registUser();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "sucess";
    }


    @PostMapping("/big-upload")
    public ResponseDto uploadOfMerge(@RequestBody FileDto fileDto) throws IOException {
        log.info("上传文件开始");


        String use = fileDto.getUse();
        String key = fileDto.getKey();
        String suffix = fileDto.getSuffix();
        String shardBase64 = fileDto.getShard();
        // 1. 将分片转为 MultipartFile
        MultipartFile shard = Base64ToMultipartFile.base64ToMultipart(fileDto.getShard());
        //  获取分片要保存到的路径
        //  根据use字段获取文件用途，从而上传到不同文件夹下（非必选）
        FileUseEnum useEnum = FileUseEnum.getByCode(use);
        // 若文件夹不存在则创建
        String dir = useEnum.name().toLowerCase();
        File fullDir = new File(FILE_PATH + dir);
        if (!fullDir.exists()) {
            fullDir.mkdir();
        }

        String path = new StringBuffer(dir)
                .append(File.separator)
                .append(key)
                .append(".")
                .append(suffix).toString(); // course\6sfSqfOwzmik4A4icMYuUe.mp4
        String localPath = new StringBuffer(path)
                .append(".")
                .append(fileDto.getShardIndex()).toString(); // course\6sfSqfOwzmik4A4icMYuUe.mp4.1
        String fullPath = FILE_PATH + localPath;
        // 2. 通过 transferTo 保存文件到服务器磁盘
        File dest = new File(fullPath);
        shard.transferTo(dest);
        log.info(dest.getAbsolutePath());
        // 3. 将文件分片信息保存/更新到数据库
        log.info("保存文件记录开始");
        fileDto.setPath(path);
        fileService.saveBigFile(fileDto);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setContent(fileDto);

        // 4. 合并
        // 若分片均已上传，将所有分片合并成一个文件。
        if (fileDto.getShardIndex().equals(fileDto.getShardTotal())) {
            this.merge(fileDto);
        }
        // 5. 返回分片上传结果
        return responseDto;
    }

    private void merge(FileDto fileDto) {
        log.info("合并分片开始");
        String path = fileDto.getPath();
        Integer shardTotal = fileDto.getShardTotal();
        File newFile = new File(FILE_PATH + path);
        byte[] byt = new byte[10 * 1024 * 1024];
        FileInputStream inputStream = null;   // 分片文件
        int len;

        // 文件追加写入
        try (FileOutputStream outputStream = new FileOutputStream(newFile, true);
        ) {
            for (int i = 0; i < shardTotal; i++) {
                // 读取第一个分片
                inputStream = new FileInputStream(new File(FILE_PATH + path + "." + (i+1))); // course\6sfSqfOwzmik4A4icMYuUe.mp4.1
                while ((len = inputStream.read(byt))!=-1) {
                    outputStream.write(byt, 0, len);
                }
            }
        } catch (FileNotFoundException e) {
            log.info("文件寻找异常", e);
        } catch (IOException e) {
            log.info("分片合并异常", e);
        } finally {
            try {
                if(inputStream !=null) {
                    inputStream.close();
                }
                log.info("IO流关闭");
            } catch (IOException e) {
                log.error("IO流关闭", e);
            }

        }
        log.error("合并分片结束");

        System.gc();
        // 删除分片
        log.info("删除分片开始");
        for (int i = 0; i < shardTotal; i++) {
            String filePath = FILE_PATH + path + "." + (i + 1);
            File file = new File(filePath);
            boolean result = file.delete();
            log.info("删除{}，{}", filePath, result ? "成功" : "失败");
        }
        log.info("删除分片结束");
    }

    @GetMapping("/check/{key}")
    public ResponseDto check(@PathVariable String key) {
        log.info("检测上传分片开始：{}}", key);
        ResponseDto responseDto = new ResponseDto();
        FileDto fileDto = fileService.findByKey(key);
        responseDto.setContent(fileDto);
        return responseDto;
    }
}
