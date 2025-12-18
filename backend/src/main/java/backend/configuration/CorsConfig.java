package backend.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

  @Value("${app.frontend-url}") // Inject value
  private String frontendUrl;

  public CorsConfig() {
    // default constructor
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        // Apply to all endpoints
        registry
            .addMapping("/api/**")
            .allowedOrigins(frontendUrl)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            // Allow all headers
            .allowedHeaders("*")
            // Allow cookies to be sent with requests
            .allowCredentials(true);
      }
    };
  }
}
