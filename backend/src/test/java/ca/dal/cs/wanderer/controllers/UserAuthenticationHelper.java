package ca.dal.cs.wanderer.controllers;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class UserAuthenticationHelper {

    public static void setAuthentication() {
        OidcUser oidcUser = new DefaultOidcUser(
                AuthorityUtils.createAuthorityList("SCOPE_message:read"),
                OidcIdToken.withTokenValue("id-token").claim("user_name", "foo_user")
                        .claim("email", "test@gmail.com").build(),
                "user_name");

        SecurityContextHolder.getContext().setAuthentication(new OAuth2AuthenticationToken(oidcUser, AuthorityUtils.createAuthorityList("SCOPE_message:read"), "clientId"));
    }
}
