package com.bsy.udvertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Verticle1 extends AbstractVerticle {

    // 定义输出日志
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void start() throws Exception {
        // 读取配置文件
        int port = config().getInteger("port", 8080);
        String filePath = config().getString("file-uploads", "D:/file-uploads/");

        // 定义路由器
        Router router = Router.router(vertx);

        // 指定文件保存目录
        router.route().handler(BodyHandler.create().setUploadsDirectory(filePath));

        // 前台界面
        router.route("/index")
                .handler(ctx -> {
                    ctx.response()
                            .putHeader("content-type", "text/html")
                            .end(
                                    "<form action=\"/form\" method=\"post\" enctype=\"multipart/form-data\">\n" +
                                            "    <div>\n" +
                                            "        <label for=\"name\">Select a file:</label>\n" +
                                            "        <input type=\"file\" name=\"file\" />\n" +
                                            "    </div>\n" +
                                            "    <div class=\"button\">\n" +
                                            "        <button type=\"submit\">Send</button>\n" +
                                            "    </div>" +
                                            "</form>"
                            );
                });

        // 处理文件上传
        router.post("/form")
                .handler(ctx -> {
                    // 支持分块传输编码
                    ctx.response().setChunked(true);
                    for (FileUpload f : ctx.fileUploads()) {
                        // 获取文件名、文件大小、uid
                        String uploadedFileName = f.uploadedFileName();
                        String originalFileName = f.fileName();
                        long fileSize = f.size();
                        String uid = uploadedFileName;
                        // 使用uid分别存储文件名和文件内容
                        String fileNameUid = suffixN(uid);
                        String fileContextUid = suffixO(uid);
                        // 写入文件名
                        vertx.fileSystem().writeFile(fileNameUid, Buffer.buffer(originalFileName));
                        // 拷贝文件
                        vertx.fileSystem().copy(uploadedFileName, fileContextUid);
                        //删除文件原先版本
                        vertx.fileSystem().delete(uploadedFileName);
                        // 取得规范的uid
                        String[] parts = uploadedFileName.split("\\\\");
                        String UID = parts[2];
                        // Json对象
                        JsonObject jsonObject = new JsonObject()
                                .put("fileName", originalFileName)
                                .put("fileUid", UID)
                                .put("fileSize", fileSize)
                                .put("download url", "http://localhost:" + String.valueOf(port) + "/download/" + UID);
                        // 将Json对象写到客户端前台
                        ctx.response()
                                .putHeader("Content-Type", "application/json")
                                .write(jsonObject.toString());
                    }
                    ctx.response().end();
                });

        // 处理文件下载
        router.get("/download/:fileUid")
                .handler(ctx -> {
                    String fileUid = ctx.pathParam("fileUid");
                    String nameFile = suffixN(fileUid);
                    String contentFile = suffixO(fileUid);
                    // 创建file对象
                    String contentFilePath = filePath + contentFile;
                    File file = new File(contentFilePath);
                    // 读取原先的文件名
                    String nameFilePath = filePath + nameFile;
                    vertx.fileSystem().readFile(nameFilePath, result -> {
                        if(result.succeeded()) {
                            Buffer buffer = result.result();
                            String content = buffer.toString("UTF-8");
                            // 将文件名和文件内容发送到客户端
                            if (file.exists()) {
                                ctx.response()
                                        .putHeader("Content-Disposition", "attachment; filename=" + content)
                                        .sendFile(file.getPath());
                            }
                        } else {
                            logger.error("Failed to read file name: " + result.cause());
                            ctx.fail(result.cause());
                        }
                    });
                }).failureHandler(ctx -> {
                    ctx.response().end("Sorry, the url you were looking for doesn't exist!");
                    logger.error("Wrong url: " + ctx.request().uri());
                });
        
        // 处理文件删除
        router.get("/delete/:uid").handler(ctx -> {
            String uid = ctx.pathParam("uid");
            String deleteNamePath = suffixN(filePath + uid);
            String deleteContentPath = suffixO(filePath + uid);
            File fileN = new File(deleteNamePath);
            File fileO = new File(deleteContentPath);
            boolean isDelete1 = fileN.delete();
            boolean isDelete2 = fileO.delete();
            System.out.println(isDelete1 && isDelete2);
            ctx.response().end("Successfully deleted.");
        });

        // 启动http服务
        vertx.createHttpServer().requestHandler(router).listen(port);
    }

    // uid文件名处理方法
    public String suffixN(String fileName) {
        return fileName + "-n";
    }

    public String suffixO(String fileName) {
        return fileName + "-o";
    }
}
