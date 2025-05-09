package stud.ntnu.krisefikser.auth.service;

import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import stud.ntnu.krisefikser.auth.config.JwtProperties;
import stud.ntnu.krisefikser.auth.dto.LoginRequest;
import stud.ntnu.krisefikser.auth.dto.LoginResponse;
import stud.ntnu.krisefikser.auth.dto.RefreshRequest;
import stud.ntnu.krisefikser.auth.dto.RefreshResponse;
import stud.ntnu.krisefikser.auth.dto.RegisterRequest;
import stud.ntnu.krisefikser.auth.dto.RegisterResponse;
import stud.ntnu.krisefikser.auth.entity.RefreshToken;
import stud.ntnu.krisefikser.auth.exception.InvalidTokenException;
import stud.ntnu.krisefikser.auth.exception.RefreshTokenDoesNotExistException;
import stud.ntnu.krisefikser.auth.repository.RefreshTokenRepository;
import stud.ntnu.krisefikser.user.dto.CreateUserDto;
import stud.ntnu.krisefikser.user.dto.UserDto;
import stud.ntnu.krisefikser.user.service.UserService;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

  private final UserService userService;
  private final CustomUserDetailsService userDetailsService;
  private final JwtProperties jwtProperties;
  private final TokenService tokenService;
  private final AuthenticationManager authenticationManager;
  private final RefreshTokenRepository refreshTokenRepository;

  public RegisterResponse register(RegisterRequest registerRequest) {
    userService.createUser(new CreateUserDto(
        registerRequest.getEmail(),
        registerRequest.getPassword(),
        registerRequest.getFirstName(),
        registerRequest.getLastName()
    ));

    UserDetails userDetails = userDetailsService.loadUserByUsername(registerRequest.getEmail());

    String accessToken = createAccessToken(userDetails);
    String refreshToken = createRefreshToken(userDetails);

    refreshTokenRepository.save(RefreshToken.builder().token(refreshToken).build());

    return new RegisterResponse(
        accessToken,
        refreshToken
    );
  }

  public LoginResponse login(LoginRequest loginRequest) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getEmail(),
            loginRequest.getPassword()
        )
    );

    UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());

    String accessToken = createAccessToken(userDetails);
    String refreshToken = createRefreshToken(userDetails);

    refreshTokenRepository.save(RefreshToken.builder().token(refreshToken).build());

    return new LoginResponse(
        accessToken,
        refreshToken
    );
  }

  public RefreshResponse refresh(RefreshRequest refreshRequest) {
    RefreshToken existingToken = refreshTokenRepository.findByToken(
        refreshRequest.getRefreshToken()).orElseThrow(
        RefreshTokenDoesNotExistException::new
    );

    String email = tokenService.extractEmail(existingToken.getToken());
    if (email == null) {
      throw new InvalidTokenException();
    }

    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

    String accessToken = createAccessToken(userDetails);
    String refreshToken = createRefreshToken(userDetails);

    refreshTokenRepository.delete(existingToken);
    refreshTokenRepository.save(RefreshToken.builder().token(refreshToken).build());

    return new RefreshResponse(
        accessToken,
        refreshToken
    );
  }

  public UserDto me() {
    return userService.getCurrentUser().toDto();
  }

  private String createAccessToken(UserDetails userDetails) {
    return tokenService.generate(userDetails, getAccessTokenExpiration());
  }

  private String createRefreshToken(UserDetails userDetails) {
    return tokenService.generate(userDetails, getRefreshTokenExpiration());
  }

  private Date getAccessTokenExpiration() {
    return new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpiration());
  }

  private Date getRefreshTokenExpiration() {
    return new Date(System.currentTimeMillis() + jwtProperties.getRefreshTokenExpiration());
  }
}
