package br.edu.fatecgru.mercado_inteligente;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@org.springframework.data.jpa.repository.config.EnableJpaAuditing
public class MercadoInteligenteApplication {

	// Comentário Teste
	public static void main(String[] args) {
		SpringApplication.run(MercadoInteligenteApplication.class, args);
	}

}
