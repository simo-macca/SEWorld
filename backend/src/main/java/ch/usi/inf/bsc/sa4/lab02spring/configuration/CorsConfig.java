package ch.usi.inf.bsc.sa4.lab02spring.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for Cross-Origin Resource Sharing (CORS) settings. This class defines CORS
 * policies for the application's API endpoints.
 */
@Configuration
public class CorsConfig {

  /** Default constructor for {@code CorsConfig}. */
  public CorsConfig() {
    // default constructor
  }

  /**
   * Creates and configures a WebMvcConfigurer bean to handle CORS settings.
   *
   * @return A WebMvcConfigurer with CORS mappings configured
   */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        // Apply to all endpoints
        registry
            .addMapping("/api/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            // Allow all headers
            .allowedHeaders("*")
            // Allow cookies to be sent with requests
            .allowCredentials(true);
      }
    };
  }
}
