package com.bank.filter;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

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

        String path = httpReq.getPathInfo();
        String method = httpReq.getMethod().toUpperCase();
        HttpSession session = httpReq.getSession(false);

        String userRole = "PUBLIC"; // default for unauthenticated
        if (session != null && session.getAttribute("role") != null) {
            userRole = session.getAttribute("role").toString();
        }

        Map<String, String> methodMap = accessMap.getOrDefault(path, new HashMap<>());
        String allowedRole = methodMap.get(method);

        if (allowedRole == null || (!allowedRole.equals("PUBLIC") && !allowedRole.equalsIgnoreCase(userRole))) {
            httpRes.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        // Pass along with an attribute for handler
        req.setAttribute("action", path);
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {}
}
