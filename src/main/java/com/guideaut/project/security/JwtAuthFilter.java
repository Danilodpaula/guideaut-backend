package com.guideaut.project.security;

import com.guideaut.project.identity.Usuario;
import com.guideaut.project.repo.UsuarioRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.transaction.annotation.Transactional; // Importante

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwt;
  private final UsuarioRepo usuarios;

  public JwtAuthFilter(JwtService jwt, UsuarioRepo usuarios) {
    this.jwt = jwt;
    this.usuarios = usuarios;
  }

  @Override
  @Transactional // Tenta manter a sessão aberta para carregar lazy/eager
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    
    String header = req.getHeader("Authorization");
    
    if (header != null && header.startsWith("Bearer ")) {
      try {
        var jws = jwt.parse(header.substring(7));
        String email = jws.getBody().getSubject();
        
        Usuario user = usuarios.findByEmail(email).orElse(null);
        
        if (user != null) {
          // FORÇA A LEITURA DAS AUTHORITIES AQUI
          var authorities = user.getAuthorities(); 
          
          // Debug (se puder ver logs): System.out.println("User: " + email + " Roles: " + authorities);

          var auth = new UsernamePasswordAuthenticationToken(
              user, 
              null, 
              authorities // Passa a lista explicitamente
          );
          
          auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
          
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      } catch (Exception e) {
          // Log erro se quiser
      }
    }
    chain.doFilter(req, res);
  }
}