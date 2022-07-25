package ca.dal.cs.wanderer.filter;

import ca.dal.cs.wanderer.services.UserProfileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import javax.servlet.ServletException;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TokenAuthenticationFilterTest {

    private TokenAuthenticationFilter tokenFilter;
    private UserProfileService profileService;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    String idTokenValue = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjNkZDZjYTJhODFkYzJmZWE4YzM2NDI0MzFlN2UyOTZkMmQ3NWI0NDYiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIxMjQ0NjM5MzUyMzQtdWhiYzhhZGhzc3BqNHNwYW1tbDBkcDhqdDU0OWlobzQuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiIxMjQ0NjM5MzUyMzQtdWhiYzhhZGhzc3BqNHNwYW1tbDBkcDhqdDU0OWlobzQuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDAxNTUxNDUwNDU2OTEyNTgwOTYiLCJlbWFpbCI6ImJwYWRoaXlhcjRAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF0X2hhc2giOiJ4T01MbWw0NXJCQVgwLVhGTklOdFd3Iiwibm9uY2UiOiJYbEh2MzNGWmRCVEZCYnlhMWFRS0tLaHJUX09adklMNlNpVWw3bEg4LTdvIiwibmFtZSI6IkJoYXJhdCBQYWRoaXlhciIsInBpY3R1cmUiOiJodHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20vYS0vQU9oMTRHaVJVTDJXcWlZRWxfV054SXN5a0xka055QXFBeDBLTk9ZeTBzaWFtZz1zOTYtYyIsImdpdmVuX25hbWUiOiJCaGFyYXQiLCJmYW1pbHlfbmFtZSI6IlBhZGhpeWFyIiwibG9jYWxlIjoiZW4iLCJpYXQiOjE2NDY0OTY1MDEsImV4cCI6MTY0NjUwMDEwMX0.GFXzQVTFESalZWBM7PzUdijhOHTqHzSFrQJx2Qo0tqKa0MAV_yfh_HsACNwjPPKUyTeWchGOBVa6sQP3kubNYQFBM49edNGhznPG8zxceoG0hWYY5fUAR2XmIWocA0_97dty0PLJifwum8WX39-OR0B7klyueLfL_fuyUGkZonwriTMnZYjylMTQpXGwxdQTsspkW3UhOj7i8v6gdrFush9P2hSbMkZIFiBoBNvd_Djg9y5ZSOJH7G7WCrBU1ZNNZlSWQDnj-q-3lIPxWWHtRrdL10mNJamQjJu3AmxWSrCAhOm9McJYARHSIcMPPpHFx4tKgolRsjiRyA5xCLESMQ";

    @BeforeEach
    void setUp() {
        profileService = mock(UserProfileService.class);
        tokenFilter = spy(new TokenAuthenticationFilter("/api/v1/**", "dummyclientId", profileService));
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        request.addHeader(HttpHeaders.AUTHORIZATION, OAuth2AccessToken.TokenType.BEARER.getValue() + " " + idTokenValue);
    }

    @AfterEach
    void tearDown() {
        profileService = null;
        tokenFilter = null;
    }

    @Test
    void attemptAuthentication() throws Exception {
        doNothing().when(tokenFilter).isTokenAlive(any(Instant.class));
        when(profileService.existsUserByEmailId(anyString())).thenReturn(true);

        Authentication auth = tokenFilter.attemptAuthentication(request, response);

        assertNotNull(auth);
        assertNotNull(auth.getPrincipal());
        assertTrue(auth.getPrincipal() instanceof OidcUser);

        verify(profileService, times(1)).existsUserByEmailId(anyString());
    }

    @Test
    void attemptAuthentication_withExpiredToken() {
        assertThrows(InternalAuthenticationServiceException.class,() -> tokenFilter.attemptAuthentication(request, response));
    }

    @Test
    void isTokenAlive() {
        tokenFilter.isTokenAlive(Instant.now().plus(30, ChronoUnit.MINUTES));
    }

    @Test
    void isTokenAlive_withPastInstant() {
        assertThrows(InternalAuthenticationServiceException.class, () -> tokenFilter.isTokenAlive(Instant.now().minus(30, ChronoUnit.MINUTES)));
    }

    @Test
    void verifyJwtAndBuildToken() throws JsonProcessingException {
        doNothing().when(tokenFilter).isTokenAlive(any(Instant.class));
        OidcIdToken token = tokenFilter.verifyJwtAndBuildToken(idTokenValue);

        assertNotNull(token);
        assertEquals(idTokenValue, token.getTokenValue());
    }

    @Test
    void verifyJwtAndBuildToken_whenTokenExpired() throws JsonProcessingException {
        assertThrows(InternalAuthenticationServiceException.class, () -> tokenFilter.verifyJwtAndBuildToken(idTokenValue));
    }

    @Test
    void successfulAuthentication() throws ServletException, IOException {
        MockFilterChain chain = new MockFilterChain();
        Authentication auth = new UsernamePasswordAuthenticationToken("principle", "credential");
        tokenFilter.successfulAuthentication(request, response, chain, auth);

        assertEquals(auth, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void unSuccessfulAuthentication() throws ServletException, IOException {
        AuthenticationException ex = new InternalAuthenticationServiceException("Token Expired");
        tokenFilter.unsuccessfulAuthentication(request, response, ex);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertEquals("Token Expired", response.getErrorMessage());
    }
}