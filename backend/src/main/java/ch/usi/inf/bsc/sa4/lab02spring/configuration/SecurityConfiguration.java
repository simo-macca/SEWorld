package ch.usi.inf.bsc.sa4.lab02spring.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration class for the application. Defines security rules, authentication, and
 * authorization settings.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  /** Default constructor for {@code SecurityConfiguration}. */
  public SecurityConfiguration() {
    // default constructor
  }

  /**
   * Configures the security filter chain for HTTP requests.
   *
   * @param http the HttpSecurity to modify
   * @return the built SecurityFilterChain
   * @throws Exception if an error occurs during configuration
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/api/auth/**")
                    .authenticated()
                    .requestMatchers("/api/public/**")
                    .permitAll())
        .oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("http://localhost:3000/topics", true))
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
        .build();
  }
}
