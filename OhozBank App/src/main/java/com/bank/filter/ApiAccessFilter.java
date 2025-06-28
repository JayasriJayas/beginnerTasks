package com.bank.filter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

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

    private static final Logger logger = Logger.getLogger(ApiAccessFilter.class.getName());

    static class Route {
        public String path;
        public String method;
        public String role;
    }

    static class RouteConfig {
        public List<Route> routes;
    }

    private final Map<String, Map<String, String>> accessMap = new HashMap<>();

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
            logger.info("API access configuration loaded successfully from: " + configPath);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load YAML config: " + configPath, e);
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
        String method = httpReq.getMethod().toUpperCase();
       
        HttpSession session = httpReq.getSession(false);

        if (path.endsWith(".jsp") || path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".png") || path.endsWith(".html"))  {
            chain.doFilter(req, res);
            return;
        }

        String userRole = "PUBLIC";
        if (session != null && session.getAttribute("role") != null) {
            userRole = session.getAttribute("role").toString();
        }

        Map<String, String> methodMap = accessMap.get(path);
        String allowedRole = (methodMap != null) ? methodMap.get(method) : null;

        logger.fine("Requested path: " + path + ", Method: " + method + ", Role: " + userRole);

        if ("PUBLIC".equalsIgnoreCase(allowedRole)) {
            logger.fine("Public access allowed for: " + method + " " + path);
            chain.doFilter(req, res);
            return;
        }
        System.out.println(allowedRole);
        System.out.println(userRole);

        if (allowedRole == null || !isRoleAllowed(userRole, allowedRole)) {
    
            logger.warning("Access denied for user with role: " + userRole + " to path: " + method + " " + path);
            ResponseUtil.sendError(httpRes, HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }
  
        logger.fine("Access granted to role: " + userRole + " for path: " + method + " " + path);
        chain.doFilter(req, res);
      
    }

    @Override
    public void destroy() {
        logger.info("ApiAccessFilter destroyed.");
    }

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
