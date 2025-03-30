package com.apps.biteandsip.configs;

import com.apps.biteandsip.dao.AuthorityRepository;
import com.apps.biteandsip.dao.MenuRepository;
import com.apps.biteandsip.model.Authority;
import com.apps.biteandsip.model.Menu;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class AppConfigs {
    private static final Logger LOG = LoggerFactory.getLogger(AppConfigs.class);
    private final AuthorityRepository authorityRepository;
    private final MenuRepository menuRepository;

    @Autowired
    public AppConfigs(AuthorityRepository authorityRepository, MenuRepository menuRepository) {
        this.authorityRepository = authorityRepository;
        this.menuRepository = menuRepository;
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

                menuRepository.save(new Menu("PUBLIC", "HOME", "/biteandsip/home"));
                menuRepository.save(new Menu("PUBLIC", "MENU", "/biteandsip/menu"));
                menuRepository.save(new Menu("PUBLIC", "ABOUT", "/biteandsip/about"));
                menuRepository.save(new Menu("PUBLIC", "CONTACT", "/biteandsip/contact"));
                menuRepository.save(new Menu("ADMINISTRATION", "DASHBOARD", "/biteandsip/admin/dashboard"));
                menuRepository.save(new Menu("ADMINISTRATION", "FOOD ITEMS", "/biteandsip/admin/food-items"));
                menuRepository.save(new Menu("ADMINISTRATION", "FOOD CATEGORIES", "/biteandsip/admin/food-categories"));
                menuRepository.save(new Menu("ADMINISTRATION", "COUPONS", "/biteandsip/admin/coupons"));

                menuRepository.save(new Menu("ADMINISTRATION", "EMPLOYEES", "/biteandsip/admin/employees"));
                menuRepository.save(new Menu("ADMINISTRATION", "CUSTOMERS", "/biteandsip/admin/customers"));
                menuRepository.save(new Menu("ADMINISTRATION", "ORDERS", "/biteandsip/admin/orders"));

                menuRepository.save(new Menu("ADMINISTRATION", "TABLES RESERVATIONS", "/biteandsip/admin/tables-reservations"));
                menuRepository.save(new Menu("ADMINISTRATION", "SETTINGS", "/biteandsip/admin/settings"));
                menuRepository.save(new Menu("PRIVATE", "MY ORDERS", "/biteandsip/orders"));
                menuRepository.save(new Menu("PRIVATE", "PROFILE", "/biteandsip/profile"));


                Set<Menu> adminMenu = new HashSet<>();
                adminMenu.addAll(Set.of(
                        menuRepository.findByMenuItem("HOME").get(),
                        menuRepository.findByMenuItem("MENU").get(),
                        menuRepository.findByMenuItem("ABOUT").get(),
                        menuRepository.findByMenuItem("CONTACT").get(),
                        menuRepository.findByMenuItem("FOOD ITEMS").get(),
                        menuRepository.findByMenuItem("FOOD CATEGORIES").get(),
                        menuRepository.findByMenuItem("TABLES RESERVATIONS").get(),
                        menuRepository.findByMenuItem("EMPLOYEES").get(),
                        menuRepository.findByMenuItem("CUSTOMERS").get(),
                        menuRepository.findByMenuItem("ORDERS").get(),
                        menuRepository.findByMenuItem("DASHBOARD").get(),
                        menuRepository.findByMenuItem("COUPONS").get(),
                        menuRepository.findByMenuItem("SETTINGS").get(),
                        menuRepository.findByMenuItem("PROFILE").get()
                ));

                Set<Menu> customerMenu = new HashSet<>();
                customerMenu.addAll(Set.of(
                        menuRepository.findByMenuItem("HOME").get(),
                        menuRepository.findByMenuItem("MENU").get(),
                        menuRepository.findByMenuItem("ABOUT").get(),
                        menuRepository.findByMenuItem("CONTACT").get(),
                        menuRepository.findByMenuItem("MY ORDERS").get(),
                        menuRepository.findByMenuItem("PROFILE").get()
                ));

                authorityRepository.save(new Authority("ROLE_ADMIN", adminMenu));
                authorityRepository.save(new Authority("ROLE_CUSTOMER", customerMenu));
                authorityRepository.save(new Authority("ROLE_KITCHEN"));
                authorityRepository.save(new Authority("ROLE_WAITER"));

                LOG.info("Database initialized!");
            } else {
                LOG.info(authorityRepository.findAll().toString());
            }
        };
    }
}
