package ch.usi.inf.bsc.sa4.lab02spring.controller;

import java.util.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class FakeUser implements OAuth2User {

  private final HashMap<String, Object> attributesMap = new HashMap<>();
  private final OAuth2AuthenticationToken token;

  public FakeUser(UUID did, String subId, String name, String email) {
    this.attributesMap.put("did", did);
    this.attributesMap.put("subId", subId);
    this.attributesMap.put("name", name);
    this.attributesMap.put("email", email);
    this.token = new OAuth2AuthenticationToken(this, Collections.emptyList(), "test");
  }

  @SuppressWarnings("unchecked")
  @Override
  public <A> A getAttribute(String name) {
    return (A) this.attributesMap.get(name);
  }

  @Override
  public Map<String, Object> getAttributes() {
    return Collections.unmodifiableMap(this.attributesMap);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  @Override
  public String getName() {
    return null;
  }

  public OAuth2AuthenticationToken getToken() {
    return this.token;
  }
}
