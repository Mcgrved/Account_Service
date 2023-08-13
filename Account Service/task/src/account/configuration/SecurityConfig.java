package account.configuration;

import account.handlers.CustomAccessDeniedHandler;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.Arrays;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final UserDetailsService userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Autowired
    public SecurityConfig(RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                          UserDetailsService userDetailsService,
                          AuthenticationConfiguration authenticationConfiguration) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.userDetailsService = userDetailsService;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .httpBasic()
            .authenticationEntryPoint(restAuthenticationEntryPoint)
            .and()
            .csrf().disable().headers().frameOptions().disable()
            .and()
            .authorizeHttpRequests()
            .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/changepass").authenticated()
            .requestMatchers(HttpMethod.GET, "api/empl/payment").hasAnyAuthority("ROLE_USER", "ROLE_ACCOUNTANT")
            .requestMatchers("/api/security/events").hasAuthority("ROLE_AUDITOR")
            .requestMatchers("/api/acct/payments").hasAuthority("ROLE_ACCOUNTANT")
            .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMINISTRATOR")
            .requestMatchers("/api/**").authenticated()
            .and()
            .addFilterAt(getAuthenticationFilter(), BasicAuthenticationFilter.class)
            .exceptionHandling().accessDeniedHandler(getAccessDeniedHandler())
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService)
//                .passwordEncoder(getEncoder());
//    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public String[] getBreachedPasswords() {
        String[] arr = new String[] {"PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
                "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
                "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"};
        return Arrays.stream(arr).map(getEncoder()::encode).toArray(String[]::new);
    }

    @Bean
    public AccessDeniedHandler getAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public BasicAuthenticationFilter getAuthenticationFilter() throws Exception {
        return new CustomAuthenticationFilter(authenticationManager(authenticationConfiguration));
    }


}
