package com.transporte.resource;

import com.transporte.bo.CartaoPontosBO;
import com.transporte.bo.UsuarioBO;
import com.transporte.model.CartaoPontos;
import com.transporte.model.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioResource {

    private final UsuarioBO usuarioBO;
    private final CartaoPontosBO cartaoBO;

    public UsuarioResource(UsuarioBO usuarioBO, CartaoPontosBO cartaoBO) {
        this.usuarioBO = usuarioBO;
        this.cartaoBO = cartaoBO;
    }

    @GetMapping
    public List<Usuario> listar() throws SQLException {
        return usuarioBO.listar();
    }

    @GetMapping("/{id}")
    public Usuario buscar(@PathVariable int id) throws SQLException {
        return usuarioBO.buscar(id);
    }

    @GetMapping("/{id}/cartao")
    public CartaoPontos buscarCartao(@PathVariable int id) throws SQLException {
        return cartaoBO.buscarPorUsuario(id);
    }

    @PostMapping
    public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws SQLException {
        Usuario criado = usuarioBO.cadastrar(usuario);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @PutMapping("/{id}")
    public Usuario atualizar(@PathVariable int id, @RequestBody Usuario usuario) throws SQLException {
        return usuarioBO.atualizar(id, usuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable int id) throws SQLException {
        usuarioBO.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
