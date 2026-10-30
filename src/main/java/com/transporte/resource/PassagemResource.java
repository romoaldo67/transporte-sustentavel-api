package com.transporte.resource;

import com.transporte.bo.PassagemBO;
import com.transporte.model.Passagem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/passagens")
public class PassagemResource {

    private final PassagemBO passagemBO;

    public PassagemResource(PassagemBO passagemBO) {
        this.passagemBO = passagemBO;
    }

    @GetMapping
    public List<Passagem> listar(@RequestParam(required = false) String status) throws SQLException {
        return passagemBO.listar(status);
    }

    @GetMapping("/{id}")
    public Passagem buscar(@PathVariable int id) throws SQLException {
        return passagemBO.buscar(id);
    }

    @PostMapping
    public ResponseEntity<Passagem> cadastrar(@RequestBody Passagem passagem) throws SQLException {
        Passagem criada = passagemBO.cadastrar(passagem);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criada.getId()).toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    @PutMapping("/{id}")
    public Passagem atualizar(@PathVariable int id, @RequestBody Passagem passagem) throws SQLException {
        return passagemBO.atualizar(id, passagem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable int id) throws SQLException {
        passagemBO.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
