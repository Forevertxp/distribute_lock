package com.sky.controller;

import com.sky.service.DataService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("data")
public class DataController {

	@Resource
	private DataService dataService;

	/**
	 * @return
	 */
	@RequestMapping(value="/test")
	public  Map<String, Object> list(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {

		    // 模拟并发
//            MyThread r = new MyThread();
//            ExecutorService executorService = Executors.newFixedThreadPool(5);
//            for (int i=0;i<100;i++){
//                executorService.execute(r);
//            }

            dataService.testUpdate();

			result.put("code", "1");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("code", "0");
		}
		return result;	
	}


	class MyThread implements Runnable{

        @Override
        public void run() {
            // 线程安全 单JVM
//            synchronized (this){
                dataService.testUpdate();
//            }
        }
    }
}
