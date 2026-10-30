package com.transporte.bo;

import com.transporte.dao.CartaoPontosDAO;
import com.transporte.dao.PassagemDAO;
import com.transporte.dao.ResgateDAO;
import com.transporte.exception.ConflitoException;
import com.transporte.exception.RecursoNaoEncontradoException;
import com.transporte.exception.RegraNegocioException;
import com.transporte.model.Passagem;
import com.transporte.model.Resgate;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class ResgateBO {

    private final ResgateDAO resgateDAO;
    private final CartaoPontosDAO cartaoDAO;
    private final PassagemDAO passagemDAO;

    public ResgateBO(ResgateDAO resgateDAO, CartaoPontosDAO cartaoDAO, PassagemDAO passagemDAO) {
        this.resgateDAO = resgateDAO;
        this.cartaoDAO = cartaoDAO;
        this.passagemDAO = passagemDAO;
    }

    public Resgate realizar(String numeroCartao, int passagemId, String dataResgate) throws SQLException {
        Validacao.texto(numeroCartao, "numeroCartao");
        var cartao = cartaoDAO.buscarPorNumero(numeroCartao)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Bilhete " + numeroCartao + " não encontrado."));
        var passagem = passagemDAO.buscarPorId(passagemId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Passagem " + passagemId + " não encontrada."));

        if (!passagem.estaDisponivel()) {
            throw new ConflitoException("A passagem " + passagemId + " não está disponível para resgate.");
        }
        int pontos = passagem.getValorEmPontos();
        if (!cartao.possuiSaldo(pontos)) {
            throw new ConflitoException("Saldo insuficiente: faltam " + (pontos - cartao.getSaldoPontos()) + " pontos.");
        }
        if (dataResgate == null || dataResgate.isBlank()) {
            dataResgate = Validacao.hoje();
        }
        Validacao.data(dataResgate, "dataResgate");

        var resgate = new Resgate(0, cartao, passagem, dataResgate, pontos);
        resgateDAO.inserir(resgate);

        try {
            cartao.debitarPontos(pontos);
            cartaoDAO.atualizar(cartao);
            passagem.setStatus(Passagem.STATUS_RESGATADA);
            passagemDAO.atualizar(passagem);
        } catch (SQLException e) {
            cartao.adicionarPontos(pontos);
            cartaoDAO.atualizar(cartao);
            resgateDAO.deletar(resgate.getId());
            throw e;
        }
        return resgate;
    }

    public List<Resgate> listar() throws SQLException {
        return resgateDAO.listar();
    }

    public Resgate buscar(int id) throws SQLException {
        return resgateDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Resgate " + id + " não encontrado."));
    }

    public Resgate atualizarData(int id, String novaData) throws SQLException {
        var resgate = buscar(id);
        Validacao.data(novaData, "dataResgate");
        resgate.setDataResgate(novaData);
        resgateDAO.atualizar(resgate);
        return buscar(id);
    }

    public void cancelar(int id) throws SQLException {
        var resgate = buscar(id);
        var cartao = resgate.getCartaoPontos();
        var passagem = resgate.getPassagem();
        if (cartao == null || passagem == null) {
            throw new RegraNegocioException("Resgate sem bilhete ou passagem associados.");
        }
        resgateDAO.deletar(id);
        cartao.adicionarPontos(resgate.getPontosUtilizados());
        cartaoDAO.atualizar(cartao);
        passagem.setStatus(Passagem.STATUS_DISPONIVEL);
        passagemDAO.atualizar(passagem);
    }
}
