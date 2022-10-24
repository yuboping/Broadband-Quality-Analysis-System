package com.asiainfo.security;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;

/**
 * 当mappedValue=null时， 动态根据url映射权限，例如：/index.do，自动映射为需要权限“index.do”
 * 
 * @author luohuawuyin
 *
 */
public class URLPermissionsFilter extends PermissionsAuthorizationFilter {
    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response,
            Object mappedValue) throws IOException {
        if (mappedValue == null) {
            return super.isAccessAllowed(request, response, buildPermissions(request));
        } else {
            return super.isAccessAllowed(request, response, mappedValue);
        }
    }

    protected String[] buildPermissions(ServletRequest request) {
        HttpServletRequest req = (HttpServletRequest) request;
        String url = req.getRequestURI();
        String ctxpath = req.getContextPath();
        String[] perms = new String[] { url.substring(url.indexOf(ctxpath) + ctxpath.length()) };
        return perms;
    }

}
