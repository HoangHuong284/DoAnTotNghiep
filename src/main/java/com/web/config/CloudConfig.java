package com.web.config;


import com.cloudinary.Cloudinary;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@SpringBootApplication
public class CloudConfig {

    @Bean
    public Cloudinary cloudinaryConfigs() {
        Cloudinary cloudinary = null;
        Map config = new HashMap();
        config.put("cloud_name", "dccekeplx");
        config.put("api_key", "581961351493346");
        config.put("api_secret", "-YF78yCCqWVxbAleF0jXOylj2gQ");
        cloudinary = new Cloudinary(config);
        return cloudinary;
    }

}
