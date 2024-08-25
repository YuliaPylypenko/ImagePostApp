package com.example.BlogWebSite;

import com.example.BlogWebSite.model.Role;
import com.example.BlogWebSite.model.User;
import com.example.BlogWebSite.repo.UserRepo;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "BlogWebSite APIS", version = "1.0", description = "BlogWebSite Management Apis."))
public class BlogWebSiteApplication implements CommandLineRunner {

    @Autowired
    private UserRepo userRepo;

    public static void main(String[] args) {
        SpringApplication.run(BlogWebSiteApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        User adminAccount = userRepo.findByRole(Role.ADMIN);
        if(adminAccount == null){
            User user = new User();

            user.setEmail("admin@gmail.com");
            user.setUserName("admin");
            user.setRole(Role.ADMIN);
            user.setPassword(new BCryptPasswordEncoder().encode("admin"));
            userRepo.save(user);
        }
    }
}
