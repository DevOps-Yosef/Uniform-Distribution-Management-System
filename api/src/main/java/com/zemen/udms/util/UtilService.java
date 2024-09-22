package com.zemen.udms.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zemen.udms.model.AccessLog;
import com.zemen.udms.model.Employee;
import com.zemen.udms.model.Entitlement;
import com.zemen.udms.model.LuStatus;
import com.zemen.udms.model.UniformIssue;
import com.zemen.udms.model.UniformRequisition;
import com.zemen.udms.service.AccessLogService;

import jakarta.servlet.http.HttpServletRequest;

@Service
@Transactional
public class UtilService {

    @Autowired
    private AccessLogService accessLogService;

    String client = "TestUDMS";
    // String client = "UDMS";

    @SuppressWarnings("unchecked")
    public boolean checkUserRequest(Authentication authentication, ArrayList<String> roles) {
        boolean isUnauthorizedRequest = false;
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            Map<String, Object> clientResource, rolesResource;
            if (jwt.getClaims().containsKey("resource_access")) {
                clientResource = (Map<String, Object>) (jwt.getClaims().get("resource_access"));
                if (clientResource != null && clientResource.containsKey(client)) {
                    rolesResource = (Map<String, Object>) (clientResource.get(client));
                    if (rolesResource != null && rolesResource.containsKey("roles")) {
                        Object roleNamesList = rolesResource.get("roles");
                        if (roleNamesList != null) {
                            ArrayList<?> roleList = (ArrayList<?>) roleNamesList;
                            for (Object rawRoleName : roleList) {
                                String roleName = rawRoleName.toString();
                                if (roles.contains(roleName)) {
                                    isUnauthorizedRequest = false;
                                    break;
                                }
                                isUnauthorizedRequest = true;
                            }
                        } else {
                            isUnauthorizedRequest = true;
                        }
                    } else {
                        isUnauthorizedRequest = true;
                    }
                } else {
                    isUnauthorizedRequest = true;
                }
            }
        } else {
            isUnauthorizedRequest = true;
        }
        return isUnauthorizedRequest;
    }

    @SuppressWarnings("unchecked")
    public List<String> listAllUserRoles(Authentication authentication) {
        List<String> roleList = new ArrayList<String>();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            Map<String, Object> clientResource, rolesResource;
            if (jwt.getClaims().containsKey("resource_access")) {
                clientResource = (Map<String, Object>) (jwt.getClaims().get("resource_access"));
                if (clientResource != null && clientResource.containsKey(client)) {
                    rolesResource = (Map<String, Object>) (clientResource.get(client));
                    if (rolesResource != null && rolesResource.containsKey("roles")) {
                        Object roleNamesList = rolesResource.get("roles");
                        if (roleNamesList != null) {
                            ArrayList<?> roleNames = (ArrayList<?>) roleNamesList;
                            for (Object rawRoleName : roleNames) {
                                String roleName = rawRoleName.toString();
                                roleList.add(roleName);
                            }
                        } 
                    } 
                }
            }
        }
        return roleList;
    }

    public <T> void createAccessLog(Authentication authentication, HttpServletRequest request,
            GenericClass<T> genericInstance) {
        AccessLog accessLog = new AccessLog();
        accessLog.setDate(new Date());
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
            String userName = (String) claims.get("preferred_username");
            accessLog.setUsername(userName);
        }
        accessLog.setOperation(request.getRequestURL().toString());
        if (genericInstance.get() instanceof Employee) {
            Employee objectForAccessLog = (Employee) genericInstance.get();
            accessLog.setObjectInfo(objectForAccessLog.getId());
        } else if (genericInstance.get() instanceof Entitlement) {
            Entitlement objectForAccessLog = (Entitlement) genericInstance.get();
            accessLog.setObjectInfo(objectForAccessLog.getId());
        } else if (genericInstance.get() instanceof LuStatus) {
            LuStatus objectForAccessLog = (LuStatus) genericInstance.get();
            accessLog.setObjectInfo(objectForAccessLog.getId());
        } else if (genericInstance.get() instanceof UniformIssue) {
            UniformIssue objectForAccessLog = (UniformIssue) genericInstance.get();
            accessLog.setObjectInfo(objectForAccessLog.getId());
        } else if (genericInstance.get() instanceof UniformRequisition) {
            UniformRequisition objectForAccessLog = (UniformRequisition) genericInstance.get();
            accessLog.setObjectInfo(objectForAccessLog.getId());
        }
        accessLogService.createAccessLog(accessLog);
    }

    public class GenericClass<T> {
        private T t;

        public void set(T t) {
            this.t = t;
        }

        public T get() {
            return t;
        }
    }

}
