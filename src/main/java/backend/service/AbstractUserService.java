package backend.service;

import backend.model.AbstractUser;
import backend.model.Instructor;
import backend.model.Student;
import backend.repository.AbstractUserRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AbstractUserService {

  private static final List<Pair<String, String>> INSTRUCTOR_LIST =
      List.of(
          new Pair<>("John Doe", "johndoe@university.edu"),
          new Pair<>("Jane Smith", "janesmith@university.edu"),
          new Pair<>("Instructor Name", "instructor@example.com"));

  private final AbstractUserRepository abstractUserRepository;

  public AbstractUserService(AbstractUserRepository abstractUserRepository) {
    this.abstractUserRepository = abstractUserRepository;
  }

  public AbstractUser createUser(Object principal) {
    Object resolved = resolvePrincipal(principal);
    Map<String, Object> attributes;
    switch (resolved) {
      case Jwt jwt -> attributes = jwt.getClaims();
      case OAuth2User auth -> attributes = auth.getAttributes();
      default -> throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Unsupported principal type: " + resolved.getClass().getName());
    }

    UUID did;
    try {
      did = UUID.fromString(attributes.get("sub").toString());
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or missing 'sub' UUID", e);
    }

    Object emailObj = attributes.get("email");
    Object nameObj = attributes.get("name");
    if (emailObj == null || nameObj == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Missing required attribute: 'email' and/or 'name'");
    }
    String email = emailObj.toString();
    String name = nameObj.toString();

    if (abstractUserRepository.getByUserDid(did).isPresent()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists");
    }

    return INSTRUCTOR_LIST.contains(new Pair<>(name, email))
        ? abstractUserRepository.save(new Instructor(name, email, did))
        : abstractUserRepository.save(new Student(name, email, did));
  }

  private Object resolvePrincipal(Object principal) {
    if (principal != null) {
      return principal;
    }
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getPrincipal() == null) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "No authenticated user found in security context");
    }
    return auth.getPrincipal();
  }
}
