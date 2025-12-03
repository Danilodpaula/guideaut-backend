package com.guideaut.project.security;

import com.guideaut.project.identity.Usuario;
import com.guideaut.project.repo.UsuarioRepo;
import jakarta.servlet.*; // Imports do Servlet padrão
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthFilter implements Filter { // <--- Mudamos para implements Filter

    private final JwtService jwt;
    private final UsuarioRepo usuarios;

    public JwtAuthFilter(JwtService jwt, UsuarioRepo usuarios) {
        this.jwt = jwt;
        this.usuarios = usuarios;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialização vazia (não precisamos fazer nada aqui, mas é obrigatório)
        log.info("JwtAuthFilter inicializado com sucesso.");
    }

    @Override
    @Transactional
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Cast necessário porque Filter usa ServletRequest genérico
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String header = req.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            try {
                var jws = jwt.parse(header.substring(7));
                String email = jws.getBody().getSubject();

                Usuario user = usuarios.findByEmail(email).orElse(null);

                if (user != null) {
                    log.info("AUTH DEBUG -> Usuario: {}", email);
                    log.info("AUTH DEBUG -> Roles: {}", user.getAuthorities());

                    var auth = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                log.error("AUTH DEBUG -> Erro token: {}", e.getMessage());
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Limpeza (opcional)
    }
}