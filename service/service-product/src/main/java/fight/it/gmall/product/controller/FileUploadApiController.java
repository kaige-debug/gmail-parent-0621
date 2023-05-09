package fight.it.gmall.product.controller;

import fight.it.gmall.common.result.Result;

import lombok.SneakyThrows;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class FileUploadApiController {
    @SneakyThrows
    @PostMapping("fileUpload")
    public Result fileUpload(@RequestParam("file")MultipartFile multipartFile) throws IOException, MyException {
//        String url = "http://192.168.200.100:8080";
//        String path = FileUploadApiController.class.getClassLoader().getResource("tracker.conf").getPath();
//        ClientGlobal.init(path);
//
//        //连接tracker
//        TrackerClient trackerClient = new TrackerClient();
//        TrackerServer trackerServer = trackerClient.getConnection();
//        //连接storage
//        StorageClient storageClient = new StorageClient(trackerServer,null);
//
//        //上传文件
//        String filenameExtension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
//        String[] jpgs = storageClient.upload_file(multipartFile.getBytes(), filenameExtension, null);
//
//        //返回url
//        for (String jpg : jpgs) {
//            url = url +"/"+jpg;
//        }
//        System.out.println(url);
//
//        return Result.ok(url);
        String url = "http://192.168.200.129:80";

        String path = FileUploadApiController.class.getClassLoader().getResource("tracker.conf").getPath();

        ClientGlobal.init(path);

        // 连接tracker
        TrackerClient trackerClient = new TrackerClient();

        TrackerServer trackerServer = trackerClient.getConnection();

        System.out.println(trackerServer);

        // 连接storage
        StorageClient storageClient = new StorageClient(trackerServer,null);

        // 上传文件
        String filenameExtension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
        String[] jpgs = storageClient.upload_file(multipartFile.getBytes(), filenameExtension, null);

        // 返回url
        for (String jpg : jpgs) {
            url = url + "/"+jpg;
        }

        System.out.println(url);
        return Result.ok(url);
    }
 }
