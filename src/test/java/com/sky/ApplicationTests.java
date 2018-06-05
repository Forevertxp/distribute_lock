package com.sky;

import com.sky.service.DataService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

    @Resource
    private DataService dataService;

    @Test
    public void testUpdate() {
        MyThread r = new MyThread();
        for (int i=0;i<10;i++){
            new Thread(r).start();
        }
    }

    static class MyThread implements Runnable{

        public void run() {
            HttpClient client = new DefaultHttpClient();
            //发送get请求
            HttpGet request = new HttpGet("http://127.0.0.1:8080/lock/v1/coach/disease/list");
            try {
                HttpResponse response = client.execute(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
