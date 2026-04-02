package com.megaminds.govisaviya.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("removal")
public class DotEnvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String DOT_ENV_FILE = ".env";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        File envFile = new File(DOT_ENV_FILE);

        if (!envFile.exists()) {
            return;
        }

        try {
            String content = Files.readString(envFile.toPath());
            Map<String, Object> envMap = new HashMap<>();

            for (String line : content.split("\\r?\\n")) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int eq = line.indexOf('=');
                if (eq < 1) continue;
                String key = line.substring(0, eq).trim();
                String value = line.substring(eq + 1).trim();
                envMap.put(key, value);
            }

            environment.getPropertySources()
                    .addLast(new MapPropertySource("dotenvFile", envMap));

        } catch (IOException e) {
            throw new RuntimeException("Failed to read .env file: " + e.getMessage());
        }
    }
}
