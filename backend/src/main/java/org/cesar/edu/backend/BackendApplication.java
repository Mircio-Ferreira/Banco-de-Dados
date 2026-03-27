package org.cesar.edu.backend;

import org.cesar.edu.backend.config.DatabaseInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
public class BackendApplication {

    private static void carregarArquivo(Properties props, String nomeArquivo) throws Exception {
        try (InputStream input = BackendApplication.class
                .getClassLoader()
                .getResourceAsStream(nomeArquivo)) {

            if (input != null) {
                props.load(input);
            }
        }
    }
    public static void main(String[] args) {
        Properties props = new Properties();

        try {
            carregarArquivo(props, "application.properties");

            String activeProfile = props.getProperty("spring.profiles.active");
            if (activeProfile != null && !activeProfile.isBlank()) {
                carregarArquivo(props, "application-" + activeProfile + ".properties");
            }

            String databaseUrl = props.getProperty("spring.datasource.url");
            String username = props.getProperty("spring.datasource.username");
            String password = props.getProperty("spring.datasource.password");

            if (databaseUrl == null || username == null || password == null) {
                throw new RuntimeException(
                        "Faltam propriedades obrigatórias: spring.datasource.url, " +
                                "spring.datasource.username ou spring.datasource.password"
                );
            }

            String semPrefixo = databaseUrl.replace("jdbc:postgresql://", "");
            String[] partesUrl = semPrefixo.split("/", 2);
            String hostPort = partesUrl[0];
            String databaseName = partesUrl[1];

            String[] partesHostPort = hostPort.split(":", 2);
            String host = partesHostPort[0];
            String port = partesHostPort[1];

            DatabaseInitializer.criarBancoSeNaoExistir(
                    host,
                    port,
                    databaseName,
                    username,
                    password
            );

        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar propriedades e inicializar banco.", e);
        }

        SpringApplication.run(BackendApplication.class, args);
    }

}