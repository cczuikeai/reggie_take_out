package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Employee;

/**
 * mybatis plus
 * 继承IService接口,限定泛型Employee
 */
public interface EmployeeService extends IService<Employee> {
}
