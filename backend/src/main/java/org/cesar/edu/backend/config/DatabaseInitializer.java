package org.cesar.edu.backend.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void criarBancoSeNaoExistir(
            String host,
            String port,
            String databaseName,
            String username,
            String password
    ) {
        String serverUrl = "jdbc:postgresql://" + host + ":" + port + "/postgres";

        try (
                Connection connection = DriverManager.getConnection(serverUrl, username, password);
                Statement statement = connection.createStatement()
        ) {
            String checkSql = "SELECT 1 FROM pg_database WHERE datname = '" + databaseName + "'";

            try (ResultSet rs = statement.executeQuery(checkSql)) {
                if (!rs.next()) {
                    statement.executeUpdate("CREATE DATABASE " + databaseName);
                    System.out.println("Banco criado: " + databaseName);
                } else {
                    System.out.println("Banco já existe: " + databaseName);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar/criar o banco de dados: " + databaseName, e);
        }
    }
}