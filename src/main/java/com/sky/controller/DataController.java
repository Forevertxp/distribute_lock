package com.sky.controller;

import com.sky.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("v1/coach")
public class DataController {
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	private DataService dataService;

	/**
	 * 获取疾病列表
	 * @return
	 */
	@RequestMapping(value="/disease/list")
	public  Map<String, Object> list(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {

        ExecutorService executorService = Executors.newFixedThreadPool(10);
//        for (int i=0;i<10;i++){
//            executorService.execute(new Runnable() {
//                @Override
//                public void run() {
//                    cdrDiseaseService.testUpdate();
//                }
//            });

//            MyThread r = new MyThread();
//            for (int i=0;i<100;i++){
//                new Thread(r).start();
//            }

            dataService.testUpdate();


			result.put("code", "1");
//			result.put("message", CustomConstants.SUCCESS_MSG);
//			result.put("result", arr);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("code", "0");
//			result.put("message", CustomConstants.ERROR_MSG);
		}
		return result;	
	}


	class MyThread implements Runnable{

        @Override
        public void run() {
            // 线程安全
            synchronized (this){
                dataService.testUpdate();
            }
        }
    }
}
