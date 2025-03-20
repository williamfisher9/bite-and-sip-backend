package com.apps.biteandsip.configs;

import com.apps.biteandsip.dao.AuthorityRepository;
import com.apps.biteandsip.model.Authority;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.awt.print.Book;
import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class AppConfigs {
    private static final Logger LOG = LoggerFactory.getLogger(AppConfigs.class);
    private final AuthorityRepository authorityRepository;

    @Autowired
    public AppConfigs(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }


    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }

    @Bean
    public CommandLineRunner startup() {
        return args -> {
            if(authorityRepository.findAll().isEmpty()) {
                LOG.info("Initializing database...");
                authorityRepository.save(new Authority("ADMIN"));
                authorityRepository.save(new Authority("CUSTOMER"));
                authorityRepository.save(new Authority("KITCHEN"));
                authorityRepository.save(new Authority("WAITER"));
                LOG.info("Database initialized!");
            } else {
                LOG.info(authorityRepository.findAll().toString());
            }
        };
    }
}
