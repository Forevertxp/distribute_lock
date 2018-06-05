package com.sky.service;

import com.sky.entity.Data;
import java.util.List;

public interface DataService {
	   int deleteByPrimaryKey(Integer id);

	    int insert(Data record);

	    Data selectByPrimaryKey(Integer id);

	    int updateByPrimaryKey(Data record);

	    void testUpdate();
}
