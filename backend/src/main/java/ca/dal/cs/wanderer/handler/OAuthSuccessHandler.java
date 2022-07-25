package ca.dal.cs.wanderer.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// class managing the oauth success handler
@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${oauth.success.redirect.uri}")
    private String successRedirectUri;

    RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    // method for redirecting user with token on successfully authentication
    // @param httpServletRequest - http request
    // @param httpServletResponse - http response
    // @param authentication - authentication
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        // getting the principal
        DefaultOidcUser principal = (DefaultOidcUser) authentication.getPrincipal();
        // redirecting with the success redirect uri along with token
        redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse,successRedirectUri + "?token=" + principal.getIdToken().getTokenValue());
    }
}
