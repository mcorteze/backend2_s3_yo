
package com.letrasypapeles.backend.security.jwt;

import com.letrasypapeles.backend.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioService usuarioService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String token = null;
        String username = null;

        System.out.println("[JwtFilter] Iniciando filtrado de JWT...");

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            System.out.println("[JwtFilter] Token recibido: " + token);
            try {
                username = jwtUtil.getUsernameFromToken(token);
                System.out.println("[JwtFilter] Nombre de usuario extraído del token: " + username);
            } catch (Exception e) {
                System.out.println("[JwtFilter] Error al extraer username del token: " + e.getMessage());
            }
        } else {
            System.out.println("[JwtFilter] No se encontró el header Authorization o no comienza con 'Bearer '");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = usuarioService.loadUserByUsername(username);
                System.out.println("[JwtFilter] UserDetails cargado: " + userDetails.getUsername());

                if (jwtUtil.validateToken(token)) {
                    System.out.println("[JwtFilter] Token válido. Estableciendo autenticación...");
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    System.out.println("[JwtFilter] Token inválido");
                }
            } catch (Exception e) {
                System.out.println("[JwtFilter] Error al autenticar usuario: " + e.getMessage());
            }
        } else {
            if (username == null) {
                System.out.println("[JwtFilter] Username nulo. No se puede autenticar.");
            } else {
                System.out.println("[JwtFilter] Ya existe una autenticación en el contexto.");
            }
        }

        filterChain.doFilter(request, response);
        System.out.println("[JwtFilter] Filtro completado.");
    }

}
 