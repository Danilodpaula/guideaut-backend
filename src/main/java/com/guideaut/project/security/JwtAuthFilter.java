package com.guideaut.project.security;

import com.guideaut.project.identity.Usuario;
import com.guideaut.project.repo.UsuarioRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// REMOVA OS IMPORTS DO SLF4J
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  // REMOVA ESTA LINHA:
  // private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

  private final JwtService jwt;
  private final UsuarioRepo usuarios;

  public JwtAuthFilter(JwtService jwt, UsuarioRepo usuarios) {
    this.jwt = jwt;
    this.usuarios = usuarios;
  }

  @Override
  @Transactional
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    
    String header = req.getHeader("Authorization");
    
    if (header != null && header.startsWith("Bearer ")) {
      try {
        var jws = jwt.parse(header.substring(7));
        String email = jws.getBody().getSubject();
        
        Usuario user = usuarios.findByEmail(email).orElse(null);
        
        if (user != null) {
          // USE 'logger' (que vem da classe pai) em vez do nosso estático
          // Nota: O logger do Spring é Apache Commons Logging, não SLF4J direto, mas funciona igual.
          logger.info("Usuario encontrado: " + email); 
          logger.info("Roles: " + user.getAuthorities());

          var auth = new UsernamePasswordAuthenticationToken(
              user, 
              null, 
              user.getAuthorities() 
          );
          
          auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
          
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      } catch (Exception e) {
          logger.error("Erro no token: " + e.getMessage());
      }
    }
    chain.doFilter(req, res);
  }
}