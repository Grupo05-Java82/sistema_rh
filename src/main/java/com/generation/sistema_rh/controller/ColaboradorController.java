package com.generation.sistema_rh.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.sistema_rh.model.Colaborador;
import com.generation.sistema_rh.repository.ColaboradorRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/colaboradores")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ColaboradorController {
	
	@Autowired
	private ColaboradorRepository colaboradorRepository; 
	
	@GetMapping
	public ResponseEntity<List<Colaborador>> getAll(){
		return ResponseEntity.ok(colaboradorRepository.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity <Colaborador> getById (@PathVariable Long id){
		return colaboradorRepository.findById(id)
				.map(resposta -> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}
	
	@GetMapping("/nome/{nome}")
	public ResponseEntity <List<Colaborador>> getByNome(@PathVariable String nome){
		List<Colaborador> produtos = colaboradorRepository.findAllByNomeContainingIgnoreCase(nome);
		
		if (produtos.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		
		return ResponseEntity.ok(colaboradorRepository.findAllByNomeContainingIgnoreCase(nome));
	}
	
	 @PostMapping("/cadastrar")
	    public ResponseEntity<?> post(@Valid @RequestBody Colaborador colaborador) {

	        // Verifica se o e-mail já está cadastrado
	        // Usando Optional para verificar a presença
	        Optional<Colaborador> colaboradorExistente = colaboradorRepository.findByEmail(colaborador.getEmail());
	        if (colaboradorExistente.isPresent()) {
	            return ResponseEntity
	                    .status(HttpStatus.BAD_REQUEST)
	                    .body("Erro: Já existe um usuário com este e-mail cadastrado.");
	        }

	        // Verifica se o usuário tem pelo menos 18 anos
	        
	        if (colaborador.getDataNascimento().plusYears(18).isAfter(LocalDate.now())) {
	            return ResponseEntity
	                    .status(HttpStatus.BAD_REQUEST)
	                    .body("Erro: O usuário deve ser maior de 18 anos.");
	        }

	        // Se tudo estiver OK, salva o colaborador
	        return ResponseEntity.status(HttpStatus.CREATED).body(colaboradorRepository.save(colaborador));
	    }
	
	@PutMapping
	public ResponseEntity<Colaborador> put(@Valid @RequestBody Colaborador colaborador) {
		
		 Optional<Colaborador> colaboradorExistente = colaboradorRepository.findByEmail(colaborador.getEmail());
		 
	     if (colaboradorExistente.isPresent()) {
	    	 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email ja existente!");
	     }
 
		if (colaborador.getId() == null)
			return ResponseEntity.badRequest().build();
 
		if (colaboradorRepository.existsById(colaborador.getId()))
			return ResponseEntity.status(HttpStatus.OK).body(colaboradorRepository.save(colaborador));
	
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		
		Optional<Colaborador> colaborador = colaboradorRepository.findById(id);
		if(colaborador.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		
		colaboradorRepository.deleteById(id);
	}
}
