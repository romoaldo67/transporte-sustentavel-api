package com.transporte.dao;

import org.springframework.stereotype.Repository;
import com.transporte.database.ConnectionFactory;
import com.transporte.model.CartaoPontos;
import com.transporte.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
public class CartaoPontosDAO {

    public void inserir(CartaoPontos c) throws SQLException {
        var sql = "INSERT INTO cartoes_pontos (numero_cartao, saldo_pontos, data_validade, usuario_id) VALUES (?, ?, ?, ?)";

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getNumeroCartao());
            stmt.setInt(2, c.getSaldoPontos());
            stmt.setString(3, c.getDataValidade());
            stmt.setInt(4, c.getUsuario().getId());
            stmt.executeUpdate();
        }
    }


    public List<CartaoPontos> listar() throws SQLException {
        var sql = """
            SELECT c.*, u.id AS u_id, u.nome, u.email, u.cpf, u.data_cadastro
            FROM cartoes_pontos c
            JOIN usuarios u ON c.usuario_id = u.id
            ORDER BY c.numero_cartao
            """;
        var lista = new ArrayList<CartaoPontos>();

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql);
             var rs   = stmt.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Optional<CartaoPontos> buscarPorUsuarioId(int usuarioId) throws SQLException {
        var sql = """
            SELECT c.*, u.id AS u_id, u.nome, u.email, u.cpf, u.data_cadastro
            FROM cartoes_pontos c
            JOIN usuarios u ON c.usuario_id = u.id
            WHERE c.usuario_id = ?
            """;

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            try (var rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public Optional<CartaoPontos> buscarPorNumero(String numeroCartao) throws SQLException {
        var sql = """
            SELECT c.*, u.id AS u_id, u.nome, u.email, u.cpf, u.data_cadastro
            FROM cartoes_pontos c
            JOIN usuarios u ON c.usuario_id = u.id
            WHERE c.numero_cartao = ?
            """;

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numeroCartao);
            try (var rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public void atualizar(CartaoPontos c) throws SQLException {
        var sql = "UPDATE cartoes_pontos SET saldo_pontos=?, data_validade=? WHERE numero_cartao=?";

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, c.getSaldoPontos());
            stmt.setString(2, c.getDataValidade());
            stmt.setString(3, c.getNumeroCartao());
            stmt.executeUpdate();
        }
    }

    public void deletar(String numeroCartao) throws SQLException {
        var sql = "DELETE FROM cartoes_pontos WHERE numero_cartao = ?";

        try (var conn = ConnectionFactory.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numeroCartao);
            stmt.executeUpdate();
        }
    }

    private CartaoPontos mapear(ResultSet rs) throws SQLException {
        var usuario = new Usuario(
            rs.getInt("u_id"),
            rs.getString("nome"),
            rs.getString("email"),
            rs.getString("cpf"),
            rs.getString("data_cadastro")
        );
        return new CartaoPontos(
            rs.getString("numero_cartao"),
            rs.getInt("saldo_pontos"),
            rs.getString("data_validade"),
            usuario
        );
    }
}
