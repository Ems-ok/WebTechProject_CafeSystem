package com.mase.cafe.system.config;

import com.mase.cafe.system.models.User;
import com.mase.cafe.system.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSetupConfig {
    
    private static final Logger log = LoggerFactory.getLogger(DataSetupConfig.class);
    private static final String DEFAULT_MANAGER = "manager";

    @Value("${app.manager.password:manager}")
    private String managerPassword;

    @Bean
    CommandLineRunner createManagerUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername(DEFAULT_MANAGER) == null) {
                User admin = new User();
                admin.setUsername(DEFAULT_MANAGER);

                admin.setPassword(passwordEncoder.encode(managerPassword));

                admin.setRole("MANAGER");
                userRepository.save(admin);
                
                log.info("MANAGER USER CREATED - Username: '{}'. Password set from configuration.", DEFAULT_MANAGER);
            } else {
                log.info("Manager user '{}' already exists. Skipping creation.", DEFAULT_MANAGER);
            }
        };
    }
}