package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * mybatis plus
 * 继承BaseMapper,设置泛型Employee,该接口就可以父类的所有增删改查方法
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
