package com.pheonix.authenticationsystem.config;

import com.pheonix.authenticationsystem.assets.Role;
import com.pheonix.authenticationsystem.repository.CompanyEntity;
import com.pheonix.authenticationsystem.repository.CompanyRepository;
import com.pheonix.authenticationsystem.repository.UserEntity;
import com.pheonix.authenticationsystem.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {
    private final PasswordEncoder passwordEncoder;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public DataSeeder(PasswordEncoder passwordEncoder, CompanyRepository companyRepository, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if(companyRepository.count() == 0 && userRepository.count() == 0) {
            CompanyEntity company = new CompanyEntity("amazon");
            UserEntity user = new UserEntity(company, "amazonAdmin", "admin@gmail.com", passwordEncoder.encode("123456"), Role.ADMIN);

            UserEntity user1 = new UserEntity(company, "amazonAnalyst", "analyst@gmail.com", passwordEncoder.encode("123456"), Role.ANALYST);

            companyRepository.save(company);
            userRepository.save(user1);
            userRepository.save(user);

            System.out.println(company);
            System.out.println(user);
            System.out.println(user1);
        }
    }
}
