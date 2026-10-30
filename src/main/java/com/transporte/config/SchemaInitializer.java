package com.transporte.config;

import com.transporte.database.ConnectionFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class SchemaInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        String script = new ClassPathResource("schema-init.sql").getContentAsString(StandardCharsets.UTF_8);
        try (var conn = ConnectionFactory.getConnection(); var stmt = conn.createStatement()) {
            for (String comando : script.split(";")) {
                if (!comando.isBlank()) {
                    stmt.execute(comando.trim());
                }
            }
        }
    }
}
