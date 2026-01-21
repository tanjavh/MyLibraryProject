package com.example.onlineLibrary.service.unit.userService;



import com.example.onlineLibrary.model.dto.UserViewDto;
import com.example.onlineLibrary.model.entity.Role;
import com.example.onlineLibrary.model.entity.User;
import com.example.onlineLibrary.model.enums.RoleName;
import com.example.onlineLibrary.repository.LoanRepository;
import com.example.onlineLibrary.repository.RoleRepository;
import com.example.onlineLibrary.repository.UserRepository;
import com.example.onlineLibrary.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private Role userRole;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userRole = Role.builder()
                .id(1L)
                .name(RoleName.USER)
                .build();

        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .active(true)
                .blocked(false)
                .roles(Set.of(userRole))
                .build();
    }

    @Test
    void testRegisterUser_setsDefaultRole() {
        when(roleRepository.findByName(RoleName.USER)).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User registered = userService.register(new User());
        assertThat(registered.getRoles()).contains(userRole);
        assertThat(registered.isActive()).isTrue();
        assertThat(registered.isBlocked()).isFalse();

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDeleteUser_noActiveLoans() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanRepository.existsByUserAndReturnedFalse(user)).thenReturn(false);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testDeleteUser_withActiveLoans_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(loanRepository.existsByUserAndReturnedFalse(user)).thenReturn(true);

        Exception ex = assertThrows(IllegalStateException.class,
                () -> userService.deleteUser(1L));
        assertThat(ex.getMessage()).isEqualTo("Korisnik ima aktivne pozajmice i ne može biti obrisan");
    }

    @Test
    void testBlockUser_setsBlocked() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.blockUser(1L);
        assertTrue(user.isBlocked());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetAllUsersForView_mapsRoles() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(loanRepository.existsByUserAndReturnedFalse(user)).thenReturn(false);

        List<UserViewDto> dtos = userService.getAllUsersForView();
        assertThat(dtos).hasSize(1);

        UserViewDto dto = dtos.get(0);
        assertThat(dto.getUsername()).isEqualTo("testuser");
        assertThat(dto.getRole()).isEqualTo("korisnik"); // mapirano na srpski
        assertThat(dto.isHasActiveLoans()).isFalse();
    }

    @Test
    void testFindByUsername_returnsUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Optional<User> found = userService.findByUsername("testuser");
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void testSave_callsRepository() {
        when(userRepository.save(user)).thenReturn(user);

        User saved = userService.save(user);
        assertThat(saved).isEqualTo(user);
        verify(userRepository, times(1)).save(user);
    }
    @Test
    void whenDuplicateEmail_thenServiceThrows() {
        User existing = User.builder().username("u1").email("dup@example.com").password("pass").build();
        userRepository.save(existing);

        assertThrows(RuntimeException.class, () -> userService.register(
                User.builder().username("u2").email("dup@example.com").password("pass").build()
        ));
    }
}

