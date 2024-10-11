package com.bsy.udtest;

import com.bsy.udvertx.Verticle1;
import io.restassured.response.Response;
import io.vertx.core.Launcher;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Test1 {
    // 文件名
    String nameF;
    String uid;
    // 文件内容
    String contextF = "";
    // 临时文件
    File tempFile;

    @BeforeEach
    public void setup() {
        int fileNameInt = getRandomInt();
        nameF = String.valueOf(fileNameInt);
        try {
            tempFile = File.createTempFile(nameF, ".txt");
            FileOutputStream fos = new FileOutputStream(tempFile);
            int ctx1 = getRandomInt();
            contextF += String.valueOf(ctx1 + "\n");
            int ctx2 = getRandomInt();
            contextF += String.valueOf(ctx2);
            fos.write(contextF.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 起服务
        Launcher.executeCommand("run", Verticle1.class.getName());
    }

    @Test
    public void test1() {
        // 上传文件
        Response response = given()
                .multiPart("file", tempFile)
                .when()
                .post("http://localhost:8080/form")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();
        String jsonResponse = response.getBody().asString();
        JsonObject jsonObject = new JsonObject(jsonResponse);
        String downloadUrl =jsonObject.getString("download url");
        uid = jsonObject.getString("fileUid");
        
        // 下载文件
        Response response2 = given()
                .when()
                .get(downloadUrl)
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();
        
        String downloadContent = response2.getBody().asString();
        // 验证文件内容是否相同
        assertEquals(contextF, downloadContent);

        assertEquals(tempFile.getName(), jsonObject.getString("fileName"));
    }
    
    @AfterEach()
    public void tearDown(){
        String deleteUrl = "http://localhost:8080/delete/" + uid;
        given()
                .when()
                .get(deleteUrl)
                .then()
                .statusCode(200);
        
        tempFile.delete();
    } 

    /**
     * @return 返回一个随机的正数
     */
    int getRandomInt() {
        Random rand = new Random();
        int num = rand.nextInt();
        while(num < 1000000000){
            num = rand.nextInt();
        }
        return num;
    }
}
