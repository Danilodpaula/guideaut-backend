package com.guideaut.project.security;

import com.guideaut.project.identity.Usuario; // Importante: apontar para o pacote identity
import com.guideaut.project.repo.UsuarioRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // <--- ESSENCIAL: Transforma isso num Bean para o SecurityConfig achar
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepo usuarioRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Busca o usuário no banco pelo email
        Usuario usuario = usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));

        // 2. Retorna o usuário. 
        // Como a classe Usuario agora implementa UserDetails, podemos retornar ela direto!
        return usuario; 
    }
}