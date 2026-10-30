package com.transporte.bo;

import com.transporte.dao.CartaoPontosDAO;
import com.transporte.dao.UsuarioDAO;
import com.transporte.exception.ConflitoException;
import com.transporte.exception.RecursoNaoEncontradoException;
import com.transporte.exception.RegraNegocioException;
import com.transporte.model.CartaoPontos;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class CartaoPontosBO {

    private final CartaoPontosDAO cartaoDAO;
    private final UsuarioDAO usuarioDAO;

    public CartaoPontosBO(CartaoPontosDAO cartaoDAO, UsuarioDAO usuarioDAO) {
        this.cartaoDAO = cartaoDAO;
        this.usuarioDAO = usuarioDAO;
    }

    public CartaoPontos cadastrar(CartaoPontos c) throws SQLException {
        if (c == null || c.getUsuario() == null) {
            throw new RegraNegocioException("Informe o usuário dono do cartão.");
        }
        var usuario = usuarioDAO.buscarPorId(c.getUsuario().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário " + c.getUsuario().getId() + " não encontrado."));
        if (cartaoDAO.buscarPorUsuarioId(usuario.getId()).isPresent()) {
            throw new ConflitoException("O usuário já possui um bilhete.");
        }
        if (c.getNumeroCartao() == null || c.getNumeroCartao().isBlank()) {
            c.setNumeroCartao("CP-" + usuario.getId());
        }
        if (c.getSaldoPontos() < 0) {
            throw new RegraNegocioException("O saldo inicial não pode ser negativo.");
        }
        Validacao.data(c.getDataValidade(), "dataValidade");
        c.setUsuario(usuario);
        cartaoDAO.inserir(c);
        return c;
    }

    public List<CartaoPontos> listar() throws SQLException {
        return cartaoDAO.listar();
    }

    public CartaoPontos buscar(String numero) throws SQLException {
        return cartaoDAO.buscarPorNumero(numero)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Bilhete " + numero + " não encontrado."));
    }

    public CartaoPontos buscarPorUsuario(int usuarioId) throws SQLException {
        return cartaoDAO.buscarPorUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Bilhete do usuário " + usuarioId + " não encontrado."));
    }

    public CartaoPontos atualizar(String numero, CartaoPontos dados) throws SQLException {
        var atual = buscar(numero);
        if (dados.getSaldoPontos() < 0) {
            throw new RegraNegocioException("O saldo não pode ser negativo.");
        }
        Validacao.data(dados.getDataValidade(), "dataValidade");
        atual.setSaldoPontos(dados.getSaldoPontos());
        atual.setDataValidade(dados.getDataValidade());
        cartaoDAO.atualizar(atual);
        return buscar(numero);
    }

    public CartaoPontos adicionarPontos(String numero, int pontos) throws SQLException {
        if (pontos <= 0) {
            throw new RegraNegocioException("A quantidade de pontos deve ser positiva.");
        }
        var cartao = buscar(numero);
        cartao.adicionarPontos(pontos);
        cartaoDAO.atualizar(cartao);
        return cartao;
    }

    public void deletar(String numero) throws SQLException {
        var cartao = buscar(numero);
        if (cartao.getSaldoPontos() > 0) {
            throw new ConflitoException("Não é possível excluir: o bilhete ainda possui "
                    + cartao.getSaldoPontos() + " pontos.");
        }
        cartaoDAO.deletar(numero);
    }
}
