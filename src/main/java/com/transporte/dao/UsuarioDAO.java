package com.transporte.dao;

import org.springframework.stereotype.Repository;
import com.transporte.database.ConnectionFactory;
import com.transporte.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioDAO {

    public void inserir(Usuario u) throws SQLException {
        var sql = "INSERT INTO usuarios (nome, email, cpf, data_cadastro) VALUES (?, ?, ?, ?)";

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, u.getNome());
            stmt.setString(2, u.getEmail());
            stmt.setString(3, formatarCpf(u.getCpf()));
            stmt.setString(4, u.getDataCadastro());
            stmt.executeUpdate();

            try (var keys = stmt.getGeneratedKeys()) {
                if (keys.next()) u.setId(keys.getInt(1));
            }
        }
    }

    public List<Usuario> listar() throws SQLException {
        var sql   = "SELECT * FROM usuarios ORDER BY id";
        var lista = new ArrayList<Usuario>();

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql);
             var rs   = stmt.executeQuery()) {  // executeQuery() para SELECT

            while (rs.next()) lista.add(mapear(rs)); // mapear() converte linha → objeto
        }
        return lista;
    }

    public Optional<Usuario> buscarPorId(int id) throws SQLException {
        var sql = "SELECT * FROM usuarios WHERE id = ?";

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (var rs = stmt.executeQuery()) {
                // rs.next() = vai para o primeiro resultado; se false = não encontrou
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public void atualizar(Usuario u) throws SQLException {
        var sql = "UPDATE usuarios SET nome=?, email=?, cpf=?, data_cadastro=? WHERE id=?";

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, u.getNome());
            stmt.setString(2, u.getEmail());
            stmt.setString(3, formatarCpf(u.getCpf()));
            stmt.setString(4, u.getDataCadastro());
            stmt.setInt(5, u.getId()); // WHERE id=? — garante que só este registro é alterado
            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        var sql = "DELETE FROM usuarios WHERE id = ?";

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private String formatarCpf(String cpf) {
        String numeros = cpf.replaceAll("[^0-9]", "");
        if (numeros.length() != 11) {
            throw new IllegalArgumentException("CPF inválido: deve conter 11 dígitos.");
        }
        return numeros.substring(0, 3) + "." +
                numeros.substring(3, 6) + "." +
                numeros.substring(6, 9) + "-" +
                numeros.substring(9, 11);
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("cpf"),
                rs.getString("data_cadastro")
        );
    }
}