package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录(过滤器)
 * filterName指定过滤器名称loginCheckFilter
 * urlPatterns拦截的路径/*(所有的请求都拦截)
 * 实现Filter接口重写doFilter方法
 */

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    /**
     * 路径匹配器,支持通配符
     * 专门用来进行路径比较的
     */
    public static  final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        /**
         * {}表示占位符,动态输出的内容可以根据后面给的参数输出在这个位置
         * 要对servletRequest进行强转成HttpServletRequest(向下转型)
         * request.getRequestURI():获得请求的路径
         * 对其进行放行 filterChain.doFilter,把request(请求),response(响应)传进去
         */
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1. 获取本次请求的URI
        /**
         * 1. 获取请求的uri
         * 2. 定义一些直接放行的请求路径(不需要处理的请求,用数组进行封装)
         *      - 登录 "/employee/login"
         *      - 退出 "/employee/logout"
         *      - 静态资源 "/backend/**"
         *      - 移动端页面 "/front/**"
         */
        String requestURI = request.getRequestURI();

        log.info("拦截到请求: {}",requestURI);

        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**"
        };

        //2. 判断本次请求是否需要处理
        /**
         * 封装一个方法check
         * 调用方法check
         */
        boolean check = check(urls, requestURI);

        //3. 如果不需要处理,则直接放行
        /**
         * 1. 如果check为true,直接放行
         */
        if (check) {
            log.info("本次请求{}不需要处理", requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //4. 判断登录状态,如果已登录,则直接放行
        /**
         * 1. 如果check不成立为false,判断登录状态
         * 2. 从session中获取登录用户,如果能获取出来表示已经登录
         * 3. 判断是否为空,不为空直接放行
         */
        if (request.getSession().getAttribute("employee") != null) {
            log.info("用户已经登录, 用户id位: {}",request.getSession().getAttribute("employee"));

            /**
             * 设置用户id(线程的局部变量)
             */
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);



            filterChain.doFilter(request,response);
            return;
        }

        //5. 如果未登录则返回未登录结果,通过输出流方式向客户端页面响应数据
        /**
         * 1. 如果为空,表示没有登录,结合页面的js代码,通过输出流方式向客户端响应数据
         * 2. 把R对象的错误方法转换成JSON,通过输出流写回去
         */
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

   /**
    * 路径匹配,检查本次请求是否需要放行
    * 判断不需要判断的路径和获得的请求路径
    * 遍历urls
    * 通过路径比较器PATH_MATCHER的方法match来进行判断(参数1为遍历出来的url元素,参数2为获得的路径requestURI)
    * 匹配上返回true,匹配不上就返回false
    * @author 姜博怀
    * @date 2022/6/21 10:35 下午
    * @param urls
    * @param requestURI
    * @return boolean
    */
    public boolean check(String[] urls,String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
