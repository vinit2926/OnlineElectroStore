package com.electronicstore.config;

import com.electronicstore.security.JwtAuthenticationEntryPoint;
import com.electronicstore.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

      @Autowired
      private UserDetailsService userDetailsService;

      @Autowired
      private JwtAuthenticationEntryPoint authenticationEntryPoint;

      @Autowired
      private JwtAuthenticationFilter jwtAuthenticationFilter;

//    @Bean
//    public UserDetailsService userDetailsService(){
//                                 // not entity user
//        UserDetails normalUser = User.builder()
//                .username("vinit")
//                .password(passwordEncoder().encode("2222"))
//                .roles("NORMAL")
//                .build();
//
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password(passwordEncoder().encode("2222"))
//                .roles("ADMIN")
//                .build();
//
//        //users create
//       //InMemoryUserDetailsManager => is implementation class of UserDetailService
//
//        return new InMemoryUserDetailsManager(normalUser,admin);
//    }

//    SKIP THIS
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        //for form authentication by browser but we want authentication from frontend
//        http.authorizeRequests()
//                .anyRequest().authenticated()
//                .and()
//                .formLogin()
//                .loginPage("login.html")
//                .loginProcessingUrl("/process-url")
//                .defaultSuccessUrl("/dashboard")
//                .failureUrl("error")
//                .and()
//                .logout()
//                .logoutUrl("/logout");
//        return http.build();

        //Implementing basic authentication
//        http.
//                csrf()
//                .disable()
//                .cors()
//                .disable()
//                .authorizeRequests()
//                .anyRequest()
//                .authenticated()
//                .and()
//                .exceptionHandling()
//                .authenticationEntryPoint(authenticationEntryPoint)
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http
                .csrf(csrf -> csrf.disable())  // Disabling CSRF with the new syntax
                .cors(cors -> cors.disable())  // Disabling CORS with the new syntax
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST,"/users")
                        .permitAll()
                        .requestMatchers(HttpMethod.DELETE,"/users/**").hasRole("ADMIN")
                        .anyRequest().authenticated())  // Replacing authorizeRequests with authorizeHttpRequests
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint))  // Using lambda for exception handling
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));  // Session management with lambda

                http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(this.userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
       return builder.getAuthenticationManager();
    }
}
