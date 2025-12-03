package com.guideaut.project.security;

import com.guideaut.project.identity.Usuario;
import com.guideaut.project.repo.UsuarioRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j; // <--- O Import Mágico
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
@Slf4j // <--- Isso cria o objeto 'log' automaticamente
public class JwtAuthFilter extends OncePerRequestFilter {

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
          // Agora usamos 'log' (minúsculo) do Lombok
          log.info("AUTH DEBUG -> Usuario encontrado: {}", email);
          log.info("AUTH DEBUG -> Papeis no banco: {}", user.getPapeis().size());
          log.info("AUTH DEBUG -> Authorities finais: {}", user.getAuthorities());

          var auth = new UsernamePasswordAuthenticationToken(
              user, 
              null, 
              user.getAuthorities() 
          );
          
          auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
          
          SecurityContextHolder.getContext().setAuthentication(auth);
        } else {
            log.warn("AUTH DEBUG -> Usuario nao encontrado no banco: {}", email);
        }
      } catch (Exception e) {
          log.error("AUTH DEBUG -> Erro ao validar token: {}", e.getMessage());
      }
    }
    chain.doFilter(req, res);
  }
}