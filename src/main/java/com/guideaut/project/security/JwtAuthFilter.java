package com.guideaut.project.security;

import com.guideaut.project.repo.UsuarioRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String header = req.getHeader("Authorization");
    
    if (header != null && header.startsWith("Bearer ")) {
      try {
        var jws = jwt.parse(header.substring(7));
        String email = jws.getBody().getSubject();
        
        // Busca o usuário (que já é um UserDetails completo)
        var user = usuarios.findByEmail(email).orElse(null);
        
        if (user != null) {
          // MUDANÇA AQUI:
          // 1. Passamos o objeto 'user' inteiro (para @AuthenticationPrincipal funcionar no Controller)
          // 2. Usamos user.getAuthorities() que já traz as roles certas do banco (sem inventar prefixo)
          var auth = new UsernamePasswordAuthenticationToken(
              user, 
              null, 
              user.getAuthorities() 
          );
          
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      } catch (Exception ignored) {
          // Token inválido ou expirado, segue o baile como anônimo
      }
    }
    chain.doFilter(req, res);
  }
}