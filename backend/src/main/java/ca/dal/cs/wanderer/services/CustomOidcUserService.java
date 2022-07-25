package ca.dal.cs.wanderer.services;

import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomOidcUserService extends OidcUserService {

    private UserRepository userRepository;

    public CustomOidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        String email = userRequest.getIdToken().getEmail();
        User user = userRepository.findByEmailId(email);
        if(user == null) {
            user = new User();
            user.setEmailId(email);
            user.setFirstName(userRequest.getIdToken().getGivenName());
            user.setLastName(userRequest.getIdToken().getFamilyName());
            //user.setImage(userRequest.getIdToken().getPicture());
            userRepository.save(user);
        }

        //principle
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new OidcUserAuthority(userRequest.getIdToken()));

        return new DefaultOidcUser(authorities, userRequest.getIdToken());
    }
}
