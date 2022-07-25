package ca.dal.cs.wanderer.handler;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.RedirectStrategy;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.spy;

class OAuthSuccessHandlerTest {

    OAuthSuccessHandler oAuthSuccessHandler = new OAuthSuccessHandler();

    @Test
    void oauthSuccessTest() throws IOException {

        RedirectStrategy redirectStrategy = spy(oAuthSuccessHandler.redirectStrategy);

        Mockito.doNothing().when(redirectStrategy).sendRedirect(anyObject(), anyObject(), anyObject());

        oAuthSuccessHandler.onAuthenticationSuccess(new MockHttpServletRequest(), new MockHttpServletResponse(), getAuthentication());
    }

    private Authentication getAuthentication() {
        OidcUser oidcUser = new DefaultOidcUser(
                AuthorityUtils.createAuthorityList("SCOPE_message:read"),
                OidcIdToken.withTokenValue("id-token").claim("user_name", "test_user")
                        .claim("email", "test@gmail.com").build(),
                "user_name");

        return new OAuth2AuthenticationToken(oidcUser, AuthorityUtils.createAuthorityList("SCOPE_message:read"), "clientId");
    }
}