package com.transporte.dao;

import org.springframework.stereotype.Repository;
import com.transporte.database.ConnectionFactory;
import com.transporte.model.Passagem;
import com.transporte.model.Transporte;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PassagemDAO {

    public void inserir(Passagem p) throws SQLException {
        var sql = """
            INSERT INTO passagens (origem, destino, data_viagem, transporte_id, valor_em_pontos, status)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, p.getOrigem());
            stmt.setString(2, p.getDestino());
            stmt.setString(3, p.getDataViagem());

            if (p.getTransporte() != null) {
                stmt.setInt(4, p.getTransporte().getId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            stmt.setInt(5, p.getValorEmPontos());
            stmt.setString(6, p.getStatus());
            stmt.executeUpdate();

            try (var keys = stmt.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getInt(1));
            }
        }
    }

    public List<Passagem> listar() throws SQLException {
        var sql = """
            SELECT p.*, t.id AS t_id, t.tipo, t.nome AS t_nome,
                   t.valor_pontos, t.taxa_carbono
            FROM passagens p
            LEFT JOIN transportes t ON p.transporte_id = t.id
            ORDER BY p.id
            """;
        var lista = new ArrayList<Passagem>();

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql);
             var rs   = stmt.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Optional<Passagem> buscarPorId(int id) throws SQLException {
        var sql = """
            SELECT p.*, t.id AS t_id, t.tipo, t.nome AS t_nome,
                   t.valor_pontos, t.taxa_carbono
            FROM passagens p
            LEFT JOIN transportes t ON p.transporte_id = t.id
            WHERE p.id = ?
            """;

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (var rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public void atualizar(Passagem p) throws SQLException {
        var sql = """
            UPDATE passagens
            SET origem=?, destino=?, data_viagem=?, transporte_id=?, valor_em_pontos=?, status=?
            WHERE id=?
            """;

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getOrigem());
            stmt.setString(2, p.getDestino());
            stmt.setString(3, p.getDataViagem());

            if (p.getTransporte() != null) {
                stmt.setInt(4, p.getTransporte().getId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            stmt.setInt(5, p.getValorEmPontos());
            stmt.setString(6, p.getStatus());
            stmt.setInt(7, p.getId());
            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        var sql = "DELETE FROM passagens WHERE id = ?";

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Passagem mapear(ResultSet rs) throws SQLException {
        Transporte transporte = null;

        if (rs.getObject("t_id") != null) {
            transporte = new Transporte(
                rs.getInt("t_id"),
                rs.getString("tipo"),
                rs.getString("t_nome"),
                rs.getInt("valor_pontos"),
                rs.getDouble("taxa_carbono")
            );
        }

        return new Passagem(
            rs.getInt("id"),
            rs.getString("origem"),
            rs.getString("destino"),
            rs.getString("data_viagem"),
            transporte,
            rs.getInt("valor_em_pontos"),
            rs.getString("status")
        );
    }
}
