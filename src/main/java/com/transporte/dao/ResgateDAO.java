package com.transporte.dao;

import org.springframework.stereotype.Repository;
import com.transporte.database.ConnectionFactory;
import com.transporte.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ResgateDAO {

    public void inserir(Resgate r) throws SQLException {
        var sql = """
            INSERT INTO resgates (numero_cartao, passagem_id, data_resgate, pontos_utilizados)
            VALUES (?, ?, ?, ?)
            """;

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, r.getCartaoPontos().getNumeroCartao());
            stmt.setInt(2, r.getPassagem().getId());
            stmt.setString(3, r.getDataResgate());
            stmt.setInt(4, r.getPontosUtilizados());
            stmt.executeUpdate();

            try (var keys = stmt.getGeneratedKeys()) {
                if (keys.next()) r.setId(keys.getInt(1));
            }
        }
    }

    public List<Resgate> listar() throws SQLException {
        var sql = """
            SELECT r.*,
                   c.saldo_pontos, c.data_validade,
                   u.id AS u_id, u.nome, u.email, u.cpf, u.data_cadastro,
                   p.origem, p.destino, p.data_viagem, p.valor_em_pontos, p.status,
                   t.id AS t_id, t.tipo, t.nome AS t_nome, t.valor_pontos, t.taxa_carbono
            FROM resgates r
            JOIN  cartoes_pontos c  ON r.numero_cartao  = c.numero_cartao
            JOIN  usuarios       u  ON c.usuario_id     = u.id
            JOIN  passagens      p  ON r.passagem_id    = p.id
            LEFT  JOIN transportes t ON p.transporte_id = t.id
            ORDER BY r.id
            """;
        var lista = new ArrayList<Resgate>();

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql);
             var rs   = stmt.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Optional<Resgate> buscarPorId(int id) throws SQLException {
        var sql = """
                SELECT r.*,
                       c.saldo_pontos, c.data_validade,
                       u.id AS u_id, u.nome, u.email, u.cpf, u.data_cadastro,
                       p.origem, p.destino, p.data_viagem, p.valor_em_pontos, p.status,
                       t.id AS t_id, t.tipo, t.nome AS t_nome, t.valor_pontos, t.taxa_carbono
                FROM resgates r
                JOIN  cartoes_pontos c  ON r.numero_cartao  = c.numero_cartao
                JOIN  usuarios       u  ON c.usuario_id     = u.id
                JOIN  passagens      p  ON r.passagem_id    = p.id
                LEFT  JOIN transportes t ON p.transporte_id = t.id
                WHERE r.id = ?
                """;

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (var rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public void atualizar(Resgate r) throws SQLException {
        var sql = "UPDATE resgates SET data_resgate=?, pontos_utilizados=? WHERE id=?";

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, r.getDataResgate());
            stmt.setInt(2, r.getPontosUtilizados());
            stmt.setInt(3, r.getId());
            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        var sql = "DELETE FROM resgates WHERE id = ?";

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Resgate mapear(ResultSet rs) throws SQLException {
        var usuario = new Usuario(
            rs.getInt("u_id"),
            rs.getString("nome"),
            rs.getString("email"),
            rs.getString("cpf"),
            rs.getString("data_cadastro")
        );

        var cartao = new CartaoPontos(
            rs.getString("numero_cartao"),
            rs.getInt("saldo_pontos"),
            rs.getString("data_validade"),
            usuario
        );

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

        var passagem = new Passagem(
            rs.getInt("passagem_id"),
            rs.getString("origem"),
            rs.getString("destino"),
            rs.getString("data_viagem"),
            transporte,
            rs.getInt("valor_em_pontos"),
            rs.getString("status")
        );

        return new Resgate(
            rs.getInt("id"),
            cartao,
            passagem,
            rs.getString("data_resgate"),
            rs.getInt("pontos_utilizados")
        );
    }
}
