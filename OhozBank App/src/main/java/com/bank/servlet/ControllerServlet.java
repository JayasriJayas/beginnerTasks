package com.bank.servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class ControllerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final Map<String, Route> routeMap = new HashMap<>();

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
            e.printStackTrace();
            throw new RuntimeException("Failed to load routes from YAML");
        }
    }




    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
      
        String path = req.getPathInfo();
        String method = req.getMethod().toUpperCase();
        String key = method + ":" + path;

        Route route = routeMap.get(key);
        if (route == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, "No handler for " + path);
            return;
        }

        try {
            System.out.println(req.getParameter("requestId"));
        	String formattedPath = capitalizeAllWords(path.replaceAll("^/", ""));
          
            String className = "com.bank.handler." + formattedPath  + "Handler";
            System.out.println(className);
            String methodName = "handle" + formattedPath ;
            System.out.println(methodName);
           
            Class<?> clazz = Class.forName(className);
            Object instance = clazz.getDeclaredConstructor().newInstance();
            Method m = clazz.getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
            m.invoke(instance, req, res);

        } catch (Exception e) {
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Handler error: " + e.getMessage());
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
    public static String capitalizeAllWords(String input) {
        String[] parts = input.split("/");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                sb.append(part.substring(0, 1).toUpperCase());
                if (part.length() > 1) {
                    sb.append(part.substring(1).toLowerCase());
                }
            }
        }
        return sb.toString();
    }

}


