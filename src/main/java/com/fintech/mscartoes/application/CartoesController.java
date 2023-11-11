package com.fintech.mscartoes.application;

import com.fintech.mscartoes.application.representation.CartaoSaveRequest;
import com.fintech.mscartoes.application.representation.CartoesPorClienteResponse;
import com.fintech.mscartoes.domain.Cartao;
import com.fintech.mscartoes.domain.ClienteCartao;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cartoes")
@RequiredArgsConstructor
public class CartoesController {

    private final CartaoService cartaoService;
    private final ClienteCartaoService clienteCartaoService;

    @GetMapping
    public String status() {
        return "ok!";
    }

    @PostMapping
    public ResponseEntity<Void> cadastra(@RequestBody CartaoSaveRequest request) {
        Cartao cartao = request.toModel();
        cartaoService.save(cartao);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(params = "renda")
    public ResponseEntity<List<Cartao>> getCartoesRendaAte(@RequestParam("renda") Long renda) {
        var cartoes = cartaoService.getCartoesMenorIgual(renda);
        return ResponseEntity.ok(cartoes);
    }

    @GetMapping(params = "cpf")
    public ResponseEntity<List<CartoesPorClienteResponse>> getCartoesByCliente(@RequestParam("cpf") String cpf) {
        List<ClienteCartao> clienteCartoesList = clienteCartaoService.listarCartoesByCpf(cpf);

        List<CartoesPorClienteResponse> cartoesPorClienteResponseList = clienteCartoesList.stream()
                .map(CartoesPorClienteResponse::fromModel).toList();

        return ResponseEntity.ok(cartoesPorClienteResponseList);
    }


}
