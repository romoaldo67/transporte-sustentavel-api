package com.transporte.bo;

import com.transporte.dao.CartaoPontosDAO;
import com.transporte.dao.UsuarioDAO;
import com.transporte.exception.ConflitoException;
import com.transporte.exception.RecursoNaoEncontradoException;
import com.transporte.exception.RegraNegocioException;
import com.transporte.model.CartaoPontos;
import com.transporte.model.Usuario;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Service
public class UsuarioBO {

    private static final int ANOS_VALIDADE_CARTAO = 4;

    private final UsuarioDAO usuarioDAO;
    private final CartaoPontosDAO cartaoDAO;

    public UsuarioBO(UsuarioDAO usuarioDAO, CartaoPontosDAO cartaoDAO) {
        this.usuarioDAO = usuarioDAO;
        this.cartaoDAO = cartaoDAO;
    }

    public Usuario cadastrar(Usuario u) throws SQLException {
        validar(u);
        if (u.getDataCadastro() == null || u.getDataCadastro().isBlank()) {
            u.setDataCadastro(Validacao.hoje());
        }
        Validacao.data(u.getDataCadastro(), "dataCadastro");

        usuarioDAO.inserir(u);

        String validade = LocalDate.now().plusYears(ANOS_VALIDADE_CARTAO).format(Validacao.FORMATO_DATA);
        try {
            cartaoDAO.inserir(new CartaoPontos("CP-" + u.getId(), 0, validade, u));
        } catch (SQLException e) {
            usuarioDAO.deletar(u.getId());
            throw e;
        }
        return u;
    }

    public List<Usuario> listar() throws SQLException {
        return usuarioDAO.listar();
    }

    public Usuario buscar(int id) throws SQLException {
        return usuarioDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário " + id + " não encontrado."));
    }

    public Usuario atualizar(int id, Usuario u) throws SQLException {
        buscar(id);
        validar(u);
        u.setId(id);
        if (u.getDataCadastro() == null || u.getDataCadastro().isBlank()) {
            u.setDataCadastro(buscar(id).getDataCadastro());
        }
        Validacao.data(u.getDataCadastro(), "dataCadastro");
        usuarioDAO.atualizar(u);
        return buscar(id);
    }

    public void deletar(int id) throws SQLException {
        buscar(id);
        var cartao = cartaoDAO.buscarPorUsuarioId(id);
        if (cartao.isPresent() && cartao.get().getSaldoPontos() > 0) {
            throw new ConflitoException("Não é possível excluir: o bilhete ainda possui "
                    + cartao.get().getSaldoPontos() + " pontos.");
        }
        usuarioDAO.deletar(id);
    }

    private void validar(Usuario u) {
        if (u == null) {
            throw new RegraNegocioException("Dados do usuário não informados.");
        }
        Validacao.texto(u.getNome(), "nome");
        Validacao.email(u.getEmail());
        if (!u.validarCPF()) {
            throw new RegraNegocioException("CPF inválido: informe 11 dígitos.");
        }
    }
}
