package com.lambdaschool.shoppingcart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main class to start the application.
 */
@EnableJpaAuditing
@SpringBootApplication
@PropertySource(value = "file:/Users/ginabethrussell/web38rest.properties", ignoreResourceNotFound = true)
public class ShoppingCartApplication
{
    /**
     * Main method to start the application.
     *
     * @param args Not used in this application.
     */
    public static void main(String[] args)
    {
        // now run the real application!
        SpringApplication.run(ShoppingCartApplication.class,
            args);
    }
}
