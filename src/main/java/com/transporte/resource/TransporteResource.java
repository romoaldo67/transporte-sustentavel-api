package com.transporte.resource;

import com.transporte.bo.TransporteBO;
import com.transporte.model.Transporte;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/transportes")
public class TransporteResource {

    private final TransporteBO transporteBO;

    public TransporteResource(TransporteBO transporteBO) {
        this.transporteBO = transporteBO;
    }

    @GetMapping
    public List<Transporte> listar() throws SQLException {
        return transporteBO.listar();
    }

    @GetMapping("/{id}")
    public Transporte buscar(@PathVariable int id) throws SQLException {
        return transporteBO.buscar(id);
    }

    @PostMapping
    public ResponseEntity<Transporte> cadastrar(@RequestBody Transporte transporte) throws SQLException {
        Transporte criado = transporteBO.cadastrar(transporte);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @PutMapping("/{id}")
    public Transporte atualizar(@PathVariable int id, @RequestBody Transporte transporte) throws SQLException {
        return transporteBO.atualizar(id, transporte);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable int id) throws SQLException {
        transporteBO.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
