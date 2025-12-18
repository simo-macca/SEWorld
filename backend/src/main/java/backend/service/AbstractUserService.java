package backend.service;

import backend.model.AbstractUser;
import backend.model.Instructor;
import backend.model.Student;
import backend.repository.AbstractUserRepository;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AbstractUserService {

  private static final Logger log = LoggerFactory.getLogger(AbstractUserService.class);

  private final AbstractUserRepository abstractUserRepository;
  private final OAuth2AuthorizedClientService authorizedClientService;

  private static final List<String> INSTRUCTOR_LIST =
      List.of("johndoe@university.edu", "janesmith@university.edu", "instructor@example.com");

  public AbstractUserService(
      AbstractUserRepository abstractUserRepository,
      OAuth2AuthorizedClientService authorizedClientService) {
    this.abstractUserRepository = abstractUserRepository;
    this.authorizedClientService = authorizedClientService;
  }

  @Transactional
  public AbstractUser createOrFindUser(Object principal) {
    Object resolved = resolvePrincipal(principal);
    Map<String, Object> attributes;
    boolean isOAuth2 = false;

    switch (resolved) {
      case Jwt jwt -> attributes = jwt.getClaims();
      case OAuth2User auth -> {
        attributes = auth.getAttributes();
        isOAuth2 = true;
      }
      default -> throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Unsupported principal type: " + resolved.getClass().getName());
    }

    String did = String.valueOf(attributes.getOrDefault("sub", attributes.get("id")));
    if (did == null || "null".equals(did)) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Invalid or missing 'sub'/'id' in token");
    }

    Object emailObj = attributes.get("email");
    Object nameObj = attributes.get("name");

    if (emailObj == null && isOAuth2) {
      emailObj = fetchEmailFromGithub();
    }

    if (emailObj == null || nameObj == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Missing required attribute: 'email' and/or 'name'");
    }
    String email = emailObj.toString();
    String name = nameObj.toString();

    return abstractUserRepository
        .getByUserDid(did)
        .orElseGet(() -> createNewUser(name, email, did));
  }

  private AbstractUser createNewUser(String name, String email, String did) {
    if (INSTRUCTOR_LIST.contains(email)) {
      return abstractUserRepository.save(new Instructor(name, email, did));
    }
    return abstractUserRepository.save(new Student(name, email, did));
  }

  private Object resolvePrincipal(Object principal) {
    if (principal != null) return principal;

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth == null || auth.getPrincipal() == null) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "No authenticated user found in security context");
    }
    return auth.getPrincipal();
  }

  private String fetchEmailFromGithub() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)) {
      return null;
    }

    OAuth2AuthorizedClient client =
        authorizedClientService.loadAuthorizedClient(
            oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());

    if (client == null || client.getAccessToken() == null) {
      return null;
    }

    String accessToken = client.getAccessToken().getTokenValue();
    String emailUrl = "https://api.github.com/user/emails";

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + accessToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    try {
      ResponseEntity<List<Map<String, Object>>> response =
          restTemplate.exchange(
              emailUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});

      List<Map<String, Object>> emails = response.getBody();
      if (emails != null) {
        return emails.stream()
            .filter(m -> Boolean.TRUE.equals(m.get("primary")))
            .map(m -> (String) m.get("email"))
            .findFirst()
            .orElse(null);
      }
    } catch (Exception e) {
      log.error("Failed to fetch email from GitHub", e);
    }
    return null;
  }
}
