package br.edu.fatecgru.mercado_inteligente.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TesteController {

	@GetMapping("/teste")
	public String teste() {
		return "API funcionando legal 🚀";
	}
}