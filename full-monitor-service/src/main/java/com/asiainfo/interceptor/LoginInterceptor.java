package com.asiainfo.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @ClassName: LoginInterceptor
 * @Description: TODO(拦截器)
 * @author
 * @date
 *
 */
public class LoginInterceptor implements HandlerInterceptor {

    private List<String> noInterUrl;

    public void setNoInterUrl(List<String> noInterUrl) {
        this.noInterUrl = noInterUrl;
    }

    public List<String> getNoInterUrl() {
        return noInterUrl;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) throws Exception {
    }

}
