package com.transporte.resource;

import com.transporte.bo.CartaoPontosBO;
import com.transporte.model.CartaoPontos;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/cartoes")
public class CartaoPontosResource {

    private final CartaoPontosBO cartaoBO;

    public CartaoPontosResource(CartaoPontosBO cartaoBO) {
        this.cartaoBO = cartaoBO;
    }

    @GetMapping
    public List<CartaoPontos> listar() throws SQLException {
        return cartaoBO.listar();
    }

    @GetMapping("/{numero}")
    public CartaoPontos buscar(@PathVariable String numero) throws SQLException {
        return cartaoBO.buscar(numero);
    }

    @PostMapping
    public ResponseEntity<CartaoPontos> cadastrar(@RequestBody CartaoPontos cartao) throws SQLException {
        CartaoPontos criado = cartaoBO.cadastrar(cartao);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{numero}").buildAndExpand(criado.getNumeroCartao()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @PutMapping("/{numero}")
    public CartaoPontos atualizar(@PathVariable String numero, @RequestBody CartaoPontos cartao) throws SQLException {
        return cartaoBO.atualizar(numero, cartao);
    }

    @PostMapping("/{numero}/pontos")
    public CartaoPontos adicionarPontos(@PathVariable String numero, @RequestBody PontosRequest request) throws SQLException {
        return cartaoBO.adicionarPontos(numero, request.pontos());
    }

    @DeleteMapping("/{numero}")
    public ResponseEntity<Void> deletar(@PathVariable String numero) throws SQLException {
        cartaoBO.deletar(numero);
        return ResponseEntity.noContent().build();
    }
}
