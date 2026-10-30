package com.transporte.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private ConnectionFactory() {
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url(), env("DB_USER", env("MYSQLUSER", "root")), env("DB_PASSWORD", env("MYSQLPASSWORD", "")));
        } catch (SQLException e) {
            throw new IllegalStateException("Falha ao conectar ao banco de dados: " + e.getMessage(), e);
        }
    }

    private static String url() {
        String url = System.getenv("DB_URL");
        if (url != null && !url.isBlank()) {
            return url;
        }
        String host = env("MYSQLHOST", "localhost");
        String port = env("MYSQLPORT", "3306");
        String database = env("MYSQLDATABASE", "transporte_db");
        return "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo";
    }

    private static String env(String name, String padrao) {
        String valor = System.getenv(name);
        return (valor == null || valor.isBlank()) ? padrao : valor;
    }
}
