package com.asiainfo.security;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asiainfo.initdata.InitDataListener;
import com.asiainfo.mapper.MAdminMapper;
import com.asiainfo.model.system.UserRolesPerms;

@Named("shiroRealm")
public class ShiroRealm extends AuthorizingRealm {

    private static final Logger LOG = LoggerFactory.getLogger(ShiroRealm.class);
    @Inject
    private MAdminMapper adminDAO;
    @Inject
    private InitDataListener initdata;
    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // 获取登录时输入的用户名
        String loginName = (String) principals.fromRealm(getName()).iterator().next();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Authorized Info For User : {} ", loginName);
        }
        UserRolesPerms admin = initdata.getUserRolesPerms(loginName);
        if (admin != null) {
            // 权限信息对象info,用来存放查出的用户的所有的角色（role）及权限（permission）
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            info.addRoles(admin.getRoles());
            info.addStringPermissions(admin.getPermissions());
            return info;
        }
        return null;
    }

    /**
     * 认证;
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
            throws AuthenticationException {
        // UsernamePasswordToken对象用来存放提交的登录信息
        if (LOG.isDebugEnabled()) {
            LOG.debug("Authen Info For User : {} ", authenticationToken.toString());
        }
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        UserRolesPerms admin = adminDAO.getAdminByName(token.getUsername());
        initdata.putUserRolesPerms(admin);
        if (admin != null) {
            // 若存在，将此用户存放到登录认证info中
            LOG.info("{},{}", admin.getAdmin(), admin.getPassword());
            return new SimpleAuthenticationInfo(admin.getAdmin(), admin.getPassword(), getName());
        }
        return null;
    }

}
