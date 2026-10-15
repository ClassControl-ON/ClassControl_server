package Servlet;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * CORS para la aplicación Flutter Web.
 * Permite credenciales porque la autenticación usa JSESSIONID.
 */
@WebFilter("/*")
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String origin = req.getHeader("Origin");
        if (origin != null && origenPermitido(origin)) {
            res.setHeader("Access-Control-Allow-Origin", origin);
            res.setHeader("Vary", "Origin");
            res.setHeader("Access-Control-Allow-Credentials", "true");
            res.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept");
            res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        }

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * Origenes validos: desarrollo en localhost y aplicaciones Flutter Web
     * servidas desde GitHub Codespaces (*.github.dev, *.app.github.dev, ...).
     */
    private boolean origenPermitido(String origin) {
        try {
            java.net.URI uri = java.net.URI.create(origin);
            String host = uri.getHost();
            if (host == null) return false;
            return host.equals("localhost") || host.equals("127.0.0.1")
                    || host.endsWith(".github.dev");
        } catch (Exception e) {
            return false;
        }
    }
}
