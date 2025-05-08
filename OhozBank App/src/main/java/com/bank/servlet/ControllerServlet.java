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

//    @Override
//    public void init() throws ServletException {
//        // Load routes when the servlet initializes
//        loadRoutes();
//    }
//
//    private void loadRoutes() {
//        try {
//            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
//            // Assuming routes.yaml is in the classpath
//            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("/WEB-INF/config/api-access.yaml");
//          
//            if (inputStream == null) {
//                throw new FileNotFoundException("YAML file not found in classpath: /WEB-INF/config/api-access.yaml");
//            }
//
//            RouteList routeList = mapper.readValue(inputStream, RouteList.class);
//
//            // Populate routeMap with method and path as key
//            for (Route route : routeList.getRoutes()) {
//                String key = route.getMethod().toUpperCase() + ":" + route.getPath();
//                routeMap.put(key, route);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Failed to load routes from YAML");
//        }
//    
//    }


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // Extract the method and path from the incoming request
        String path = req.getPathInfo();
        String method = req.getMethod().toUpperCase();
        String key = method + ":" + path;

        // Check if the route exists
        Route route = routeMap.get(key);
        if (route == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, "No handler for " + path);
            return;
        }

        try {
            // Generate class and method names dynamically
            String className = "com.bank.handler." + capitalize(path.replaceAll("^/", "")) + "Handler";
            System.out.println(className);
            String methodName = "handle" + capitalize(path.replaceAll("^/", ""));
            System.out.println(methodName);
            // Load the handler class and invoke the appropriate method
            Class<?> clazz = Class.forName(className);
            Object instance = clazz.getDeclaredConstructor().newInstance();
            Method m = clazz.getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
            m.invoke(instance, req, res);

        } catch (Exception e) {
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Handler error: " + e.getMessage());
        }
    }

    // Utility method to capitalize the first letter of a string
    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    // Helper classes to map YAML
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
}
//
//public class ControllerServlet extends HttpServlet 
//{
//
//	private static final long serialVersionUID = 1L;
//
//	@Override
//	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
//		String method = req.getMethod();
//		String action = req.getPathInfo();
//		
//		System.out.println(method);
//     
//		switch (action) 
//		{
//		case "/signup":
//			new SignupHandler().handleSignup(req, res);
//			break;
//		default:
//			res.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid POST route: " + action);
//		}
//	}
//}
