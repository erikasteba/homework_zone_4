package coursesit.Config;

import coursesit.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/login", "/css/**", "/js/**", "/homepage").permitAll() // Разрешаем доступ ко всем
                        .anyRequest().authenticated()  // Все остальные страницы требуют аутентификации
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/homepage", true) // Перенаправление на /homepage после успешного логина
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                )
                .userDetailsService(customUserDetailsService)
                .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)  // Обработка сессий (по умолчанию)
                        .maximumSessions(1)
                        .expiredUrl("/login?expired=true")  // Перенаправление на страницу логина при невалидной сессии
        );
        return http.build();
    }

}

