package odk.apprenant.jobaventure_backend.config;


import lombok.AllArgsConstructor;
import odk.apprenant.jobaventure_backend.service.UseService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class Security {

    private JwtAuthFilter jwtAuthFilter;
    private UseService useService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(request-> request
                        // Accès uniquement pour ADMIN
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/admins/**").hasRole("ADMIN")
                        .requestMatchers("/api/statistiques/**").hasRole("ADMIN")
                        .requestMatchers("/api/parents/**").hasRole("Parent")
                        .requestMatchers("/api/categories/**").hasRole("ADMIN")
                        .requestMatchers("/api/age/**").hasRole("ADMIN")
                        .requestMatchers("/api/videos/**").hasAnyRole("Enfant", "ADMIN", "PROFESSIONNEL")
                        .requestMatchers("/api/modifier/**").authenticated()
                        .requestMatchers("/api/interview/**").hasAnyRole("Enfant", "ADMIN", "PROFESSIONNEL")

                        .requestMatchers("/api/metiers/**").hasAnyRole("Enfant", "ADMIN")
                        .requestMatchers("/api/reponse/**").hasAnyRole("Enfant", "ADMIN", "PROFESSIONNEL")
                        .requestMatchers("/api/jeux/**").hasAnyRole("Enfant", "ADMIN", "PROFESSIONNEL")
                        .requestMatchers("/api/question/**").hasAnyRole("Enfant", "ADMIN", "PROFESSIONNEL")
                        .requestMatchers("/api/quiz/**").hasAnyRole("Enfant", "ADMIN", "PROFESSIONNEL")
                        .requestMatchers("/api/enfants/**").hasAnyRole("Enfant", "ADMIN", "PROFESSIONNEL") // Vérifiez cette ligne
                        .requestMatchers("/uploads/images/**").permitAll() // Autoriser l'accès à tous les fichiers dans /uploads/
                        .requestMatchers("/uploads/videos/**").permitAll() // Autoriser l'accès à tous les fichiers dans /uploads/
                        .requestMatchers("/uploads/audios/**").permitAll() // Autoriser l'accès à tous les fichiers dans /uploads/
                        // Routes publiques
                        .requestMatchers("/role/**").permitAll()

                        .requestMatchers("/reponse/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()


                )
                .sessionManagement(manager->manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider()).addFilterBefore(
                        jwtAuthFilter, UsernamePasswordAuthenticationFilter.class
                );
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(useService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
}
