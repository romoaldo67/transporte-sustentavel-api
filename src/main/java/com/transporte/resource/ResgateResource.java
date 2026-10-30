package com.transporte.resource;

import com.transporte.bo.ResgateBO;
import com.transporte.model.Resgate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/resgates")
public class ResgateResource {

    private final ResgateBO resgateBO;

    public ResgateResource(ResgateBO resgateBO) {
        this.resgateBO = resgateBO;
    }

    @GetMapping
    public List<Resgate> listar() throws SQLException {
        return resgateBO.listar();
    }

    @GetMapping("/{id}")
    public Resgate buscar(@PathVariable int id) throws SQLException {
        return resgateBO.buscar(id);
    }

    @PostMapping
    public ResponseEntity<Resgate> realizar(@RequestBody ResgateRequest request) throws SQLException {
        Resgate criado = resgateBO.realizar(request.numeroCartao(), request.passagemId(), request.dataResgate());
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @PutMapping("/{id}")
    public Resgate atualizarData(@PathVariable int id, @RequestBody DataRequest request) throws SQLException {
        return resgateBO.atualizarData(id, request.dataResgate());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable int id) throws SQLException {
        resgateBO.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
