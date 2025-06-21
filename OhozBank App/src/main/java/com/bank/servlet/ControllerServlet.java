
package com.bank.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bank.util.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class ControllerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final Map<String, Route> routeMap = new HashMap<>();
    private static final Logger logger = Logger.getLogger(ControllerServlet.class.getName());

    @Override
    public void init() throws ServletException {
        try {
            String realPath = getServletContext().getRealPath("/WEB-INF/config/api-access.yaml");
            File file = new File(realPath);
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            RouteList routeList = mapper.readValue(file, RouteList.class);

            for (Route route : routeList.getRoutes()) {
                String key = route.getMethod().toUpperCase() + ":" + route.getPath();
                routeMap.put(key, route);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load route configuration from YAML", e);
            throw new ServletException("Failed to load routes from YAML", e);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String fullPath = req.getRequestURI();         
        String contextPath = req.getContextPath();     
        String path = fullPath.substring(contextPath.length()); 
        String method = req.getMethod().toUpperCase(); 
     
        if (!path.startsWith("/api/")) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid API path");
            return;
        }

       
        String apiPath = path.substring("/api".length()); 
        String key = method + ":" + "/api" + apiPath;

        Route route = routeMap.get(key);
        if (route == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, "No handler for " + path);
            return;
        }
       
        try {
            String[] parts = apiPath.split("/");
            if (parts.length < 3) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path format: " + apiPath);
                return;
            }
            String rawMethod = parts[1];       
            String rawClass = parts[2];         
            String methodName = toCamelCase(rawMethod, false);   
            String handlerClass = toCamelCase(rawClass, true) + "Handler";  
            String className = "com.bank.handler." + handlerClass;
            Class<?> clazz = Class.forName(className);
            Object instance = clazz.getDeclaredConstructor().newInstance();
            
            Method handlerMethod = clazz.getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
            handlerMethod.invoke(instance, req, res);

        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Handler class not found for path: " + path, e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_NOT_FOUND, "Handler class not found");
        } catch (NoSuchMethodException e) {
            logger.log(Level.SEVERE, "Handler method not found for path: " + path, e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_BAD_REQUEST, "Handler method not found");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error invoking handler for path: " + path, e);
            ResponseUtil.sendError(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Handler error occurred. Check server logs.");
        }
    }

    public static class RouteList {
        private List<Route> routes;
        public List<Route> getRoutes() { return routes; }
        public void setRoutes(List<Route> routes) { this.routes = routes; }
    }

    public static class Route {
        private String path;
        private String method;
        private String role;

        public String getPath() { return path; }
        public String getMethod() { return method; }
        public String getRole() { return role; }

        public void setPath(String path) { this.path = path; }
        public void setMethod(String method) { this.method = method; }
        public void setRole(String role) { this.role = role; }
    }

    private static String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
    private static String toCamelCase(String input, boolean capitalizeFirst) {
        StringBuilder result = new StringBuilder();
        boolean capitalize = capitalizeFirst;
        for (char c : input.toCharArray()) {
            if (c == '-' || c == '_') {
                capitalize = true;
            } else if (capitalize) {
                result.append(Character.toUpperCase(c));
                capitalize = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }
        return result.toString();
    }

}



