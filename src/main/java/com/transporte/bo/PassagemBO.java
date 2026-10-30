package com.transporte.bo;

import com.transporte.dao.PassagemDAO;
import com.transporte.dao.TransporteDAO;
import com.transporte.exception.ConflitoException;
import com.transporte.exception.RecursoNaoEncontradoException;
import com.transporte.exception.RegraNegocioException;
import com.transporte.model.Passagem;
import com.transporte.model.Transporte;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class PassagemBO {

    private final PassagemDAO passagemDAO;
    private final TransporteDAO transporteDAO;

    public PassagemBO(PassagemDAO passagemDAO, TransporteDAO transporteDAO) {
        this.passagemDAO = passagemDAO;
        this.transporteDAO = transporteDAO;
    }

    public Passagem cadastrar(Passagem p) throws SQLException {
        preparar(p);
        if (p.getStatus() == null || p.getStatus().isBlank()) {
            p.setStatus(Passagem.STATUS_DISPONIVEL);
        }
        passagemDAO.inserir(p);
        return p;
    }

    public List<Passagem> listar(String status) throws SQLException {
        var todas = passagemDAO.listar();
        if (status == null || status.isBlank()) {
            return todas;
        }
        return todas.stream().filter(p -> status.equalsIgnoreCase(p.getStatus())).toList();
    }

    public Passagem buscar(int id) throws SQLException {
        return passagemDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Passagem " + id + " não encontrada."));
    }

    public Passagem atualizar(int id, Passagem p) throws SQLException {
        var atual = buscar(id);
        if (!atual.estaDisponivel()) {
            throw new ConflitoException("Passagem já resgatada não pode ser alterada.");
        }
        preparar(p);
        p.setId(id);
        if (p.getStatus() == null || p.getStatus().isBlank()) {
            p.setStatus(atual.getStatus());
        }
        passagemDAO.atualizar(p);
        return buscar(id);
    }

    public void deletar(int id) throws SQLException {
        var atual = buscar(id);
        if (!atual.estaDisponivel()) {
            throw new ConflitoException("Passagem já resgatada não pode ser excluída.");
        }
        passagemDAO.deletar(id);
    }

    private void preparar(Passagem p) throws SQLException {
        if (p == null) {
            throw new RegraNegocioException("Dados da passagem não informados.");
        }
        Validacao.texto(p.getOrigem(), "origem");
        Validacao.texto(p.getDestino(), "destino");
        Validacao.data(p.getDataViagem(), "dataViagem");

        if (p.getTransporte() != null && p.getTransporte().getId() > 0) {
            Transporte t = transporteDAO.buscarPorId(p.getTransporte().getId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Transporte " + p.getTransporte().getId() + " não encontrado."));
            p.setTransporte(t);
            if (p.getValorEmPontos() <= 0) {
                p.setValorEmPontos(t.calcularPontosComDesconto());
            }
        } else {
            p.setTransporte(null);
        }
        if (p.getValorEmPontos() <= 0) {
            throw new RegraNegocioException("Informe um transporte ou o valor em pontos da passagem.");
        }
    }
}
