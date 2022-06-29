function loginApi(data) {
  /**
   * 这边对loginApi用js进行了封装,然后用axios来响应请求
   * url表示请求路径
   * method表示使用请求的方式
   * 再把数据带到loginApi方法中->page/login/login.html
   */
  return $axios({
    'url': '/employee/login',
    'method': 'post',
    data
  })
}

function logoutApi(){
  return $axios({
    'url': '/employee/logout',
    'method': 'post',
  })
}
