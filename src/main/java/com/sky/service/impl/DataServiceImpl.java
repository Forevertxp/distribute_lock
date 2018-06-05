package com.sky.service.impl;

import com.sky.dao.DataMapper;
import com.sky.entity.Data;
import com.sky.lock.DistributedLockUtil;
import com.sky.service.DataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DataServiceImpl implements DataService {

    private static String lock = "DISTRIBUTE_LOCK";

	@Resource
	private DataMapper dataMapper;
	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(Data record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Data selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return dataMapper.selectByPrimaryKey(id);
	}

	@Override
	public int updateByPrimaryKey(Data record) {
		// TODO Auto-generated method stub
		return dataMapper.updateByPrimaryKey(record);
	}

    @Override
    public void testUpdate() {
	    try {
            if (DistributedLockUtil.acquire(lock)){
                Data record = dataMapper.selectByPrimaryKey(1);
                if (record.getCount()>0){
                    try {
                        int type = record.getCount()-1;
                        System.out.print(type+",");
                        record.setCount(type);
                        dataMapper.updateByPrimaryKey(record);
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            DistributedLockUtil.release(lock);
        }

    }

}
