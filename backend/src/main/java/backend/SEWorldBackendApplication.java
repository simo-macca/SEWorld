package backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SEWorldBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(SEWorldBackendApplication.class, args);
  }
}
