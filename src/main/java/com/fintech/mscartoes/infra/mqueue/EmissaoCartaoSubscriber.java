package com.fintech.mscartoes.infra.mqueue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.mscartoes.domain.Cartao;
import com.fintech.mscartoes.domain.ClienteCartao;
import com.fintech.mscartoes.domain.DadosSolicitacaoEmissaoCartao;
import com.fintech.mscartoes.infra.repository.CartaoRepository;
import com.fintech.mscartoes.infra.repository.ClienteCartaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmissaoCartaoSubscriber {
    private final Logger log = LoggerFactory.getLogger(EmissaoCartaoSubscriber.class);
    private final CartaoRepository cartaoRepository;
    private final ClienteCartaoRepository clienteCartaoRepository;

    @RabbitListener(queues = "${mq.queues.emissao-cartoes}")
    public void receberSolicitacaoEmissao(@Payload String payload) {
        try {
            var mapper = new ObjectMapper();

            DadosSolicitacaoEmissaoCartao dados = mapper.readValue(payload, DadosSolicitacaoEmissaoCartao.class);
            Cartao cartao = cartaoRepository.findById(dados.getIdCartao()).orElseThrow();

            ClienteCartao clienteCartao = new ClienteCartao();
            clienteCartao.setCartao(cartao);
            clienteCartao.setCpf(dados.getCpf());
            clienteCartao.setLimite(dados.getLimiteLiberado());

            clienteCartaoRepository.save(clienteCartao);
        } catch (Exception e) {
            log.error("Erro solicitação de emissão de cartão: {}", e.getMessage());
        }
    }

}
