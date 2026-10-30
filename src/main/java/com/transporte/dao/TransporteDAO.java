package com.transporte.dao;

import org.springframework.stereotype.Repository;
import com.transporte.database.ConnectionFactory;
import com.transporte.model.Transporte;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TransporteDAO {

    public void inserir(Transporte t) throws SQLException {
        var sql = "INSERT INTO transportes (tipo, nome, valor_pontos, taxa_carbono) VALUES (?, ?, ?, ?)";

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, t.getTipo());
            stmt.setString(2, t.getNome());
            stmt.setInt(3, t.getValorPontos());
            stmt.setDouble(4, t.getTaxaCarbono());
            stmt.executeUpdate();

            try (var keys = stmt.getGeneratedKeys()) {
                if (keys.next()) t.setId(keys.getInt(1));
            }
        }
    }

    public List<Transporte> listar() throws SQLException {
        var sql   = "SELECT * FROM transportes ORDER BY id";
        var lista = new ArrayList<Transporte>();

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql);
             var rs   = stmt.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Optional<Transporte> buscarPorId(int id) throws SQLException {
        var sql = "SELECT * FROM transportes WHERE id = ?";

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (var rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public void atualizar(Transporte t) throws SQLException {
        var sql = "UPDATE transportes SET tipo=?, nome=?, valor_pontos=?, taxa_carbono=? WHERE id=?";

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, t.getTipo());
            stmt.setString(2, t.getNome());
            stmt.setInt(3, t.getValorPontos());
            stmt.setDouble(4, t.getTaxaCarbono());
            stmt.setInt(5, t.getId());
            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        var sql = "DELETE FROM transportes WHERE id = ?";

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Transporte mapear(ResultSet rs) throws SQLException {
        return new Transporte(
            rs.getInt("id"),
            rs.getString("tipo"),
            rs.getString("nome"),
            rs.getInt("valor_pontos"),
            rs.getDouble("taxa_carbono")
        );
    }
}
