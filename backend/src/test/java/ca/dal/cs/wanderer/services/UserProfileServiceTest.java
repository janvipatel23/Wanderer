package ca.dal.cs.wanderer.services;

import ca.dal.cs.wanderer.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class UserProfileServiceTest {

    @InjectMocks
    private UserProfileService profileService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExistsUserByEmailId() {
        when(userRepository.existsUserByEmailIdIgnoreCase(anyString())).thenReturn(true);

        assertTrue(profileService.existsUserByEmailId("test@gmail.com"));
        verify(userRepository, times(1)).existsUserByEmailIdIgnoreCase(anyString());
    }

        @Test
        public void testExistsUserByEmailId_userNotExist() {
            when(userRepository.existsUserByEmailIdIgnoreCase(anyString())).thenReturn(false);

            assertFalse(profileService.existsUserByEmailId("test@gmail.com"));
            verify(userRepository, times(1)).existsUserByEmailIdIgnoreCase(anyString());
        }
    }