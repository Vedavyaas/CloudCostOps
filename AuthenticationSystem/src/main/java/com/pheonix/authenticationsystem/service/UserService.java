package com.pheonix.authenticationsystem.service;

import com.pheonix.authenticationsystem.assets.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import com.pheonix.authenticationsystem.repository.CompanyEntity;
import com.pheonix.authenticationsystem.repository.CompanyRepository;
import com.pheonix.authenticationsystem.repository.UserEntity;
import com.pheonix.authenticationsystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final CompanyRepository companyRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtEncoder jwtEncoder;

    public UserService(UserRepository userRepository, CompanyRepository companyRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtEncoder jwtEncoder) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
    }

    @Transactional
    public String createCompany(CompanyDetails companyDetails) {
        if(companyRepository.existsByCompanyName(companyDetails.companyName())) {
            throw new CredentialsAlreadyExistsException("Enterprise name already used, choose some other name.");
        }

        if(userRepository.existsByUserName(companyDetails.adminUserName())) {
            throw new CredentialsAlreadyExistsException("Admin name already used, choose some other name.");
        }

        CompanyEntity companyEntity = new CompanyEntity(companyDetails.companyName());
        UserEntity userEntity = new UserEntity(companyEntity, companyDetails.adminUserName(), companyDetails.email(), passwordEncoder.encode(companyDetails.password()), Role.ADMIN);

        companyRepository.save(companyEntity);
        userRepository.save(userEntity);

        return "Enterprise created successfully!";
    }

    public String createAnalyst(AnalystDetails analystDetails, String companyName) {
        if(userRepository.existsByUserName(analystDetails.name())) {
            throw new CredentialsAlreadyExistsException("Username already used, use another one!");
        }

        CompanyEntity companyEntity = companyRepository.findByCompanyName(companyName);

        if (companyEntity == null) {
            //mostly impossible, but ensuring safety
            throw new CredentialsAlreadyExistsException("Company doesnt exist");
        }

        UserEntity user = new UserEntity(companyEntity, analystDetails.name(), analystDetails.email(), passwordEncoder.encode(analystDetails.password()), Role.ANALYST);

        userRepository.save(user);
        return "Account added successfully!!";
    }

    public JWTToken authenticate(LoginRequest loginRequest) {
        Optional<UserEntity> user = userRepository.findByUserName(loginRequest.username());
        String companyName = user.get().getCompany().getCompanyName();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
        );

        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(36000))
                .subject(authentication.getName())
                .claim("scope", scope)
                .claim("company-name", companyName)
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new JWTToken(token);
    }

    public UserDTO getInfo(String username) {
        return userRepository.findUserDTOByUserName(username)
                .orElseThrow(() -> new CredentialsAlreadyExistsException("Username not found"));
    }

    public String getAnalystCount(String companyName) {
        return "No of analyst: " + userRepository.countByCompany_CompanyNameAndRole(companyName, Role.ANALYST);
    }

    public List<UserDTO> getAnalysts(String companyName) {
        return userRepository.findAnalystsByCompanyNameAndRole(companyName, Role.ANALYST);
    }
}
