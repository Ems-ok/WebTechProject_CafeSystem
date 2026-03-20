package com.mase.cafe.system.config;

import com.mase.cafe.system.models.*;
import com.mase.cafe.system.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class DataSetupConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSetupConfig.class);
    private static final String DEFAULT_MANAGER = "manager";
    private static final String DEFAULT_STAFF = "staff";

    @Value("${app.manager.password:manager}")
    private String managerPassword;

    @Value("${app.staff.password:staff}")
    private String staffPassword;

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                   ItemRepository itemRepository,
                                   OrderRepository orderRepository,
                                   MenuRepository menuRepository, // Added MenuRepository
                                   PasswordEncoder passwordEncoder) {
        return args -> {

            User manager = userRepository.findByUsername(DEFAULT_MANAGER);
            if (manager == null) {
                manager = new User();
                manager.setUsername(DEFAULT_MANAGER);
                manager.setPassword(passwordEncoder.encode(managerPassword));
                manager.setRole("MANAGER");
                userRepository.save(manager);
                log.info("MANAGER USER CREATED");
            }

            User staff = userRepository.findByUsername(DEFAULT_STAFF);
            if (staff == null) {
                staff = new User();
                staff.setUsername(DEFAULT_STAFF);
                staff.setPassword(passwordEncoder.encode(staffPassword));
                staff.setRole("STAFF");
                userRepository.save(staff);
                log.info("STAFF USER CREATED");
            }

            if (itemRepository.count() == 0) {
                Item i1 = new Item(null, "Caramel Macchiato", "Sweet coffee", 5.50, "Beverage", null, true);
                Item i2 = new Item(null, "Croissant", "Buttery pastry", 3.00, "Pastry", null, true);
                Item i3 = new Item(null, "Iced Tea", "Refreshing", 4.00, "Beverage", null, true);
                Item i4 = new Item(null, "Blueberry Muffin", "Fresh baked", 3.50, "Pastry", null, true);
                Item i5 = new Item(null, "Espresso", "Strong shot", 2.50, "Beverage", null, true);
                Item i6 = new Item(null, "Flat White", "Velvety milk and espresso", 4.50, "Beverage", null, true);
                Item i7 = new Item(null, "Ham & Cheese Toastie", "Toasted sourdough", 6.50, "Food", null, true);
                Item i8 = new Item(null, "Chocolate Brownie", "Rich and fudgy", 3.75, "Pastry", null, true);

                itemRepository.saveAll(List.of(i1, i2, i3, i4, i5, i6, i7, i8));
                log.info("SAMPLE ITEMS CREATED (8 Items)");
            }

            if (menuRepository.count() == 0) {

                Item macchiato = itemRepository.findByName("Caramel Macchiato").orElse(null);
                Item croissant = itemRepository.findByName("Croissant").orElse(null);
                Item espresso = itemRepository.findByName("Espresso").orElse(null);
                Item toastie = itemRepository.findByName("Ham & Cheese Toastie").orElse(null);
                Item brownie = itemRepository.findByName("Chocolate Brownie").orElse(null);
                Item icedTea = itemRepository.findByName("Iced Tea").orElse(null);

                Menu todayMenu = new Menu();
                todayMenu.setMenuDate(LocalDate.now());
                todayMenu.setItems(new HashSet<>(Set.of(macchiato, croissant, espresso, toastie)));
                menuRepository.save(todayMenu);

                Menu tomorrowMenu = new Menu();
                tomorrowMenu.setMenuDate(LocalDate.now().plusDays(1));
                tomorrowMenu.setItems(new HashSet<>(Set.of(icedTea, brownie, toastie, espresso)));
                menuRepository.save(tomorrowMenu);

                log.info("SAMPLE MENUS CREATED for Today and Tomorrow");
            }

            if (orderRepository.count() == 0) {
                Item macchiato = itemRepository.findByName("Caramel Macchiato").orElse(null);
                Item croissant = itemRepository.findByName("Croissant").orElse(null);
                Item espresso = itemRepository.findByName("Espresso").orElse(null);
                Item toastie = itemRepository.findByName("Ham & Cheese Toastie").orElse(null);
                Item brownie = itemRepository.findByName("Chocolate Brownie").orElse(null);

                if (macchiato != null && croissant != null) {
                    Order sampleOrder = new Order();
                    sampleOrder.setOrdername("Initial Seed Sale");
                    sampleOrder.setUser(manager);
                    sampleOrder.setOrderTimestamp(LocalDateTime.now());

                    OrderItem sale1 = new OrderItem();
                    sale1.setItem(macchiato);
                    sale1.setQuantity(2);
                    sale1.setOrder(sampleOrder);

                    OrderItem sale2 = new OrderItem();
                    sale2.setItem(croissant);
                    sale2.setQuantity(1);
                    sale2.setOrder(sampleOrder);

                    OrderItem sale3 = new OrderItem();
                    sale3.setItem(espresso);
                    sale3.setQuantity(1);
                    sale3.setOrder(sampleOrder);

                    OrderItem sale4 = new OrderItem();
                    sale4.setItem(brownie);
                    sale4.setQuantity(4);
                    sale4.setOrder(sampleOrder);

                    OrderItem sale5 = new OrderItem();
                    sale5.setItem(toastie);
                    sale5.setQuantity(2);
                    sale5.setOrder(sampleOrder);

                    sampleOrder.setOrderItems(List.of(sale1, sale2, sale3, sale4, sale5));

                    double total = (macchiato.getPrice() * 2) + (croissant.getPrice() * 1) +
                            (espresso.getPrice() * 1) + (brownie.getPrice() * 4) +
                            (toastie.getPrice() * 2);
                    sampleOrder.setTotalAmount(total);

                    orderRepository.save(sampleOrder);
                    log.info("ANALYTICS SEED DATA CREATED");
                }
            }
        };
    }
}