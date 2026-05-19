package br.edu.fatecgru.mercado_inteligente.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecgru.mercado_inteligente.model.entity.Funcionario;
import br.edu.fatecgru.mercado_inteligente.model.entity.Usuario;
import br.edu.fatecgru.mercado_inteligente.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Endpoints relacionados aos Usuários")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar todos os usuários(Apenas ADMIN)")
	public List<Usuario> listarTodos() {
		return usuarioService.listarTodos();
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar usuário por ID (Apenas ADMIN)")
	public Usuario buscarPorId(@PathVariable Long id) {
		return usuarioService.getById(id);
	}

	@GetMapping("/contem-nome/{nome}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar usuário por Nome (Apenas ADMIN)")
	public List<Usuario> buscarPorContemNome(@PathVariable String nome) {
		return usuarioService.getByContainsName(nome);
	}

	// Método para listar clientes
	@GetMapping("/clientes")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar clientes (Apenas ADMIN)")
	public ResponseEntity<List<Usuario>> listarClientes() {

		return ResponseEntity.ok(usuarioService.listarClientes());
	}

	// Método para listar administradores
	@GetMapping("/admins")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar admins (Apenas ADMIN)")
	public ResponseEntity<List<Funcionario>> listarAdmins() {

		return ResponseEntity.ok(usuarioService.listarAdministradores());
	}

	// Método para listar estoquistas
	@GetMapping("/estoquistas")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Listar estoquistas (Apenas ADMIN)")
	public ResponseEntity<List<Funcionario>> listarEstoquistas() {

		return ResponseEntity.ok(usuarioService.listarEstoquistas());
	}

}
