package ca.dal.cs.wanderer.filter;
import ca.dal.cs.wanderer.services.UserProfileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// class for managing token authentication filter
public class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private UserProfileService profileService;

    private String clientId;

    // initializing constructor
    public TokenAuthenticationFilter(String defaultFilterProcessesUrl, String clientId, UserProfileService profileService) {
        super(defaultFilterProcessesUrl);
        this.profileService = profileService;
        this.clientId = clientId;
    }

    // method for attempting authentication for api by fetching bearer token from authorization header and then validating
    // @param httpServletRequest - http request
    // @param httpServletResponse - http response
    // @returns - return authentication
    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException {

        // Read the token from the request header. Throw exception if Authorization header is not found.
        String token = getBearerToken(httpServletRequest);

        // encode the token and verify that JWT. if it's valid build the OidcIdToken object.
        OidcIdToken oidcIdToken = verifyJwtAndBuildToken(token);

        // Check if the email id from is not present in our database throw exception.
        if (!profileService.existsUserByEmailId(oidcIdToken.getEmail())) {
            throw new InternalAuthenticationServiceException("Unauthorized");
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new OidcUserAuthority(oidcIdToken));

        // Building principal object to register it in SecurityContextHolder.
        DefaultOidcUser principal = new DefaultOidcUser(authorities, oidcIdToken);
        return new OAuth2AuthenticationToken(principal, authorities, clientId);
    }

    // method for verifying the jwt token to authenticate the user
    // @param token - the user token
    // @return - returns the OidcIdToken
    public OidcIdToken verifyJwtAndBuildToken(String token) throws JsonProcessingException {

        // Decode the jwt.
        Jwt jwt = JwtHelper.decode(token);

        // Converting an json into Map<String, String>.
        Map<String, String> claim = new ObjectMapper().readValue(jwt.getClaims(), new TypeReference<>() {
        });

        // Adding all map key-value pair in oidc token claim.
        OidcIdToken.Builder oidc = OidcIdToken.withTokenValue(token).claims(m -> {
            m.putAll(claim);
        });

        oidc.issuedAt(Instant.ofEpochSecond(Long.parseLong(claim.get("iat"))));
        Instant exp = Instant.ofEpochSecond(Long.parseLong(claim.get("exp")));

        // Checking if token is expired or not. If expired throw exception.
        isTokenAlive(exp);
        oidc.expiresAt(exp);

        return oidc.build();
    }

    // method for checking whether token is alive or not
    // @param exp - expiration time
    protected void isTokenAlive(Instant exp) {
        if (exp.isBefore(Instant.now())) {
            throw new InternalAuthenticationServiceException("Token Expired");
        }
    }

    // method for fetching the bearer token
    // @param request - the http request
    // @return - returns the token
    private String getBearerToken(HttpServletRequest request) {

        // Reading Authorization Header from the request object.
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Checking Authorization header has valid bearer token.
        if (StringUtils.hasText(token) && token.startsWith(OAuth2AccessToken.TokenType.BEARER.getValue())) {
            token = token.replace(OAuth2AccessToken.TokenType.BEARER.getValue(), "");
            return token;
        }

        // If invalid Authorization header.
        throw new InternalAuthenticationServiceException("Token not found");
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // After successful authentication adding auth object into security context and continue the filter chain.
        SecurityContextHolder.getContext().setAuthentication(authResult);
        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        // On unsuccessful authentication sending Http-status code 401 (Unauthorized).
        response.sendError(HttpStatus.UNAUTHORIZED.value(), failed.getMessage());
    }
}