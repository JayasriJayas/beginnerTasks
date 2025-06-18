package com.bank.filter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.bank.util.ResponseUtil;


public class ApiAccessFilter implements Filter {

    static class Route {
        public String path;
        public String method;
        public String role;
    }

    static class RouteConfig {
        public List<Route> routes;
    }

    private Map<String, Map<String, String>> accessMap = new HashMap<>();

   @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String configPath = filterConfig.getServletContext().getRealPath("/WEB-INF/config/api-access.yaml");
        try (InputStream input = new FileInputStream(configPath)) {
            Yaml yaml = new Yaml(new Constructor(RouteConfig.class));
            RouteConfig config = yaml.load(input);
            for (Route r : config.routes) {
                accessMap
                    .computeIfAbsent(r.path, k -> new HashMap<>())
                    .put(r.method.toUpperCase(), r.role);
            }
        } catch (IOException e) {
            throw new ServletException("Failed to load YAML config", e);
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
    	
      
        HttpServletRequest httpReq = (HttpServletRequest) req;
        HttpServletResponse httpRes = (HttpServletResponse) res;
        String fullPath = httpReq.getRequestURI();
        String contextPath = httpReq.getContextPath(); 
        String path = fullPath.substring(contextPath.length()); 
//        String path = httpReq.getPathInfo();
        String method = httpReq.getMethod().toUpperCase();
        HttpSession session = httpReq.getSession(false);
     
        if (path.endsWith(".jsp") || path.endsWith(".css") || path.endsWith(".js")) {
            chain.doFilter(req, res);
            return;
        }
        
        String userRole = "PUBLIC"; 
        if (session != null && session.getAttribute("role") != null) {
            userRole = session.getAttribute("role").toString();
        }


        Map<String, String> methodMap = accessMap.get(path);
        String allowedRole = (methodMap != null) ? methodMap.get(method) : null;

        if ("PUBLIC".equalsIgnoreCase(allowedRole)) {
            chain.doFilter(req, res);
            return;
        }
        System.out.println(allowedRole);
        System.out.println(userRole);
        if (allowedRole == null || !isRoleAllowed(userRole, allowedRole)) {
            ResponseUtil.sendError(httpRes, HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }


        
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {}
    private boolean isRoleAllowed(String userRole, String allowedRole) {
        String[] roles = allowedRole.split(",");
        for (String role : roles) {
            if (role.trim().equalsIgnoreCase(userRole)) {
                return true;
            }
        }
        return false;
    }

}