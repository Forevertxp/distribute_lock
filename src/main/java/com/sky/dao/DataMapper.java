package com.sky.dao;


import com.sky.entity.Data;

import java.util.List;


public interface DataMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Data record);

    int insertSelective(Data record);

    Data selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(Data record);
    /**
     * 查询所有的疾病类型
     */
    List<Data> getAll();
}