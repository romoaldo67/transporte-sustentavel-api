package com.transporte.bo;

import com.transporte.dao.TransporteDAO;
import com.transporte.exception.RecursoNaoEncontradoException;
import com.transporte.exception.RegraNegocioException;
import com.transporte.model.Transporte;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class TransporteBO {

    private final TransporteDAO transporteDAO;

    public TransporteBO(TransporteDAO transporteDAO) {
        this.transporteDAO = transporteDAO;
    }

    public Transporte cadastrar(Transporte t) throws SQLException {
        validar(t);
        transporteDAO.inserir(t);
        return t;
    }

    public List<Transporte> listar() throws SQLException {
        return transporteDAO.listar();
    }

    public Transporte buscar(int id) throws SQLException {
        return transporteDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Transporte " + id + " não encontrado."));
    }

    public Transporte atualizar(int id, Transporte t) throws SQLException {
        buscar(id);
        validar(t);
        t.setId(id);
        transporteDAO.atualizar(t);
        return buscar(id);
    }

    public void deletar(int id) throws SQLException {
        buscar(id);
        transporteDAO.deletar(id);
    }

    private void validar(Transporte t) {
        if (t == null) {
            throw new RegraNegocioException("Dados do transporte não informados.");
        }
        Validacao.texto(t.getTipo(), "tipo");
        Validacao.texto(t.getNome(), "nome");
        if (t.getValorPontos() <= 0) {
            throw new RegraNegocioException("O valor em pontos deve ser maior que zero.");
        }
        if (t.getTaxaCarbono() < 0) {
            throw new RegraNegocioException("A taxa de carbono não pode ser negativa.");
        }
    }
}
