package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 前端发过来的请求就是post,所以这里用post
 * 请求过来的路径通过F12可以看到是login
 * <p>
 * 当点击登录时,开发者工具可以看到返回来json形式的账号密码
 * <p>
 * 因为登录成功以后要用employee(员工对象id)存到session一份,这样获取当前用户可以随时获取出来,到时候可以通过
 * request对象来get一个session
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return com.itheima.reggie.common.R<com.itheima.reggie.entity.Employee>
     * @author 姜博怀
     * @date 2022/6/20 11:39 下午
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        // 1. 将页面提交的密码password进行md5加密处理
        /*
        1. 获取密码
        2. 通过工具类里的md5DigestAsHex方法将密码转成byte数组传进去
         */
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2. 根据页面提交的用户名username查询数据库
        /*
        1. 通过创建一个LambdaQueryWrapper对象指定泛型(实体类Employee)来包装一个查询对象
        2. 添加一个查询条件(等值查询,根据用户名查),传过来的username 也在employee对象里,这样查询条件就封装好了
        3. getOne方法,数据库里面已经对username字段做了唯一的约束,在字段上已经加了Unique索引,唯一的就可以调用getOne去查出来唯一的一个数据,然后将其封装成Employee对象
         */
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 3. 如果没有查询到则返回登录失败结果
        /*
        1. 查完以后判断这个数据有没有被查到
        2. 判断emp是否为空,是的话就返回登录失败
        3. 如果判断没有成立就说明查到了结果
         */
        if (emp == null) {
            return R.error("登录失败");
        }

        // 4. 密码比对,如果不一致则返回登录失败结果
        /*
        1. 拿数据库查到的密码和明文处理后的密码进行比对
        2. 判断如果不一致,就返回登录失败
        3. 如果判断没有成立就说明密码比对成功
         */
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }

        // 5. 查看员工状态,如果为已禁用状态,则返回员工已禁用结果
        /*
        1. 查看员工状态是否是正常可用的或者被禁用了
        2. 0表示禁用,1表示可用
        3. 判断员工状态如果是0,则返回员工已禁用结果
        4. 如果没有被禁用就说明账号正常,可以登陆成功
         */
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        // 6. 登录成功,将员工id存入Session并返回登录成功结果
        /*
        1. 登录成功就要将用户的id存入Session中
        2. 通过request的getSession,将employee的id属性存入Session
        3. 将数据库的查出来的emp对象放进去并返回
         */
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);

    }

    /**
     * 员工退出
     *
     * @param request
     * @return com.itheima.reggie.common.R<java.lang.String>
     * @author 姜博怀
     * @date 2022/6/21 1:25 上午
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        // 1. 清理Session中保存的当前登录员工的id
        /*
        1. 用request对象获取Session,在从session移出employee
         */
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 1. 新增员工
     * 2. 只需要判断code就行了,不需要判断data
     * 3. 保存封装成employee对象save(Employee employee)
     * 4. 因为页面没有密码输入框,没提交过来,所有要在新增员工的时候统一给一个初始密码(不能使用明文的)
     * 5. 设置初始密码123456,需要进行md5加密处理
     * 6. 通过工具类里的md5DigestAsHex方法将密码转成byte数组传进去
     * 7. 获取当前系统时间employee.setCreateTime(LocalDateTime.now());
     *    获取当前更新事件employee.setUpdateTime(LocalDateTime.now());
     * 8. 获得当前登录用户的id
     * 9. 从session里取出id,用empId获取
     * 10. 获取创造人的id,获取修改人的id
     * 11. 调用employeeService方法,把employee对象传进去
     * 12. 返回成功信息
     *
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工,员工信息: {}", employee.toString());

        //设置初始密码123456,需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        /**
         * 用mp的公共字段填充
         */
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //获得当前登录用户的id
        //Long empId = (Long) request.getSession().getAttribute("employee");

        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        employeeService.save(employee);
        log.info("git123");
        log.info("git1234");
        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * 这边的泛型要指定page,MybatisPlus提供
     * 进行分页查询会返回page对象,里面有两个属性records:当前这一页要展示的列表数据,total:总的条数
     * 需要传三个参数,分别为page,pageSize,和name(直接在页面时通过名字查询)
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page ={},pageSize={},name={}", page, pageSize, name);

        //构造分页构造器(封装page对象)
        Page pageInfo = new Page(page, pageSize);

        //构造条件构造器(封装过滤条件)
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        //判断name是否为空,不为空就添加,为空就相当于没有这个条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加排序条件
        //根据Employee类中的更新方法(updateTime)来进行排序
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        //调用employeeService的page方法,把new出来的page对象和条件构造器(queryWrapper)传进去
        //不用进行返回,在内部会把查出来的数据进行封装,封装给对象相应的属性进行赋值
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     *
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());

        long id = Thread.currentThread().getId();
        log.info("线程id为: {}",id);


        //Long empId = (Long) request.getSession().getAttribute("employee");
       //employee.setUpdateTime(LocalDateTime.now());
       //employee.setUpdateUser(empId);
        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }


    /**
     * 根据id查询员工信息
     * @PathVariable 路径变量
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工信息..");
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("没有查询到对应员工的信息");
    }
}
