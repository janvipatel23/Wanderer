package ca.dal.cs.wanderer.services;

import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomOidcUserServiceTest {

    @InjectMocks
    private CustomOidcUserService customOidcUserService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    String idTokenValue = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImFjYjZiZTUxZWZlYTZhNDE5ZWM5MzI1ZmVhYTFlYzQ2NjBmNWIzN2MiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIxMjQ0NjM5MzUyMzQtdWhiYzhhZGhzc3BqNHNwYW1tbDBkcDhqdDU0OWlobzQuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiIxMjQ0NjM5MzUyMzQtdWhiYzhhZGhzc3BqNHNwYW1tbDBkcDhqdDU0OWlobzQuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDAxNTUxNDUwNDU2OTEyNTgwOTYiLCJlbWFpbCI6ImJwYWRoaXlhcjRAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF0X2hhc2giOiJCbEtnT";

    @Test
    public void loadUserTest() {
        ClientRegistration reg = ClientRegistration.withRegistrationId("test").authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE).clientId("dummyClientId").clientSecret("dummyClientSecretId").redirectUri("dummyRedirectURI").authorizationUri("dummmyAuthorizationURI").tokenUri("dummyTokenURI").build();
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "dummyToken", Instant.now(), Instant.now().plus(30, ChronoUnit.MINUTES));
        OidcIdToken idToken = OidcIdToken.withTokenValue(idTokenValue).claim("email", "dummy@gmail.com").subject("dummySubject").build();
        OidcUserRequest oidcUserRequest = new OidcUserRequest(reg, accessToken, idToken);

        User user = getUser();

        when(userRepository.findByEmailId(anyString())).thenReturn(user);

        OidcUser oidcUser = customOidcUserService.loadUser(oidcUserRequest);

        Assertions.assertNotNull(oidcUser);
        Assertions.assertEquals(user.getEmailId(), oidcUser.getEmail());
        Assertions.assertNotNull(oidcUser.getAuthorities());

        verify(userRepository, times(1)).findByEmailId(anyString());
    }

    @Test
    public void loadWhenUserIsNull() {
        ClientRegistration reg = ClientRegistration.withRegistrationId("test").authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE).clientId("dummyClientId").clientSecret("dummyClientSecretId").redirectUri("dummyRedirectURI").authorizationUri("dummmyAuthorizationURI").tokenUri("dummyTokenURI").build();
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "dummyToken", Instant.now(), Instant.now().plus(30, ChronoUnit.MINUTES));
        OidcIdToken idToken = OidcIdToken.withTokenValue(idTokenValue).claim("email", "dummy@gmail.com").subject("dummySubject").build();
        OidcUserRequest oidcUserRequest = new OidcUserRequest(reg, accessToken, idToken);

        OidcUser oidcUser = customOidcUserService.loadUser(oidcUserRequest);

        Assertions.assertNotNull(oidcUser);
        Assertions.assertEquals("dummy@gmail.com", oidcUser.getEmail());
        Assertions.assertNotNull(oidcUser.getAuthorities());
    }

    private User getUser() {
        User user = new User();
        user.setEmailId("dummy@gmail.com");
        user.setId(1);
        user.setFirstName("dummyFirstName");
        user.setLastName("dummyLastName");

        return user;
    }
}