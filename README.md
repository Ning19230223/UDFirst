# UDFirst2

#### 介绍

Use vert.x to upload and download files



### 使用

- windows系统创建*D:/file-uploads/*目录

- 运行在9017端口

  - 方式一：在idea中的Run/Debug Configurations中添加

    ```run com.bsy.udvertx.Verticle1 -conf D:\code\UDFirst5\ud-vertx\src\main\resources\config.json```

    ，然后运行MyLauncer.class

  - 方式二：使用shadowJar进行打包，在ud-vertx目录下运行指令```java -jar .\build\libs\ud-vertx-1.0-SNAPSHOT-fat.jar -conf D:\code\UDFirst5\ud-vertx\src\main\resources\config.json```

    -conf 后面填写代码的放置目录下config.json的绝对路径



### 单元测试

运行Test1.class，生成临时文件，逐一验证文件上传、下载、校对、删除功能，运行在8080端口
