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
	
	// BUSCAR TODOS OS COLABORADORES REGISTRADOS
	@GetMapping
	public ResponseEntity<List<Colaborador>> getAll(){
		return ResponseEntity.ok(colaboradorRepository.findAll());
	}
	
	// BUSCAR COLABORADOR POR ID EM TABELA
	@GetMapping("/{id}")
	public ResponseEntity <Colaborador> getById (@PathVariable Long id){
		return colaboradorRepository.findById(id)
				.map(resposta -> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}
	
	
	// BUSCAR COLABORADOR POR NOME
	@GetMapping("/nome/{nome}")
	public ResponseEntity <List<Colaborador>> getByNome(@PathVariable String nome){
		
		// CONSULTA SE O COLABORADOR NÃO EXISTE
		List<Colaborador> produtos = colaboradorRepository.findAllByNomeContainingIgnoreCase(nome);
		if (produtos.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		
		//CASO ELE EXISTA, SERA MOSTRADO A LISTA
		return ResponseEntity.ok(colaboradorRepository.findAllByNomeContainingIgnoreCase(nome));
	}
	
	//CADASTRAR UM USUARIO
	 @PostMapping("/cadastrar")
	    public ResponseEntity<?> post(@Valid @RequestBody Colaborador colaborador) {

	        // VERIFICA SE O EMAIL ESTA CADASTRADO
	        // USANDO OPTIONAL PARA VERIFICAR A PRESENÇA
	        Optional<Colaborador> colaboradorExistente = colaboradorRepository.findByEmail(colaborador.getEmail());
	        if (colaboradorExistente.isPresent()) {
	            return ResponseEntity
	                    .status(HttpStatus.BAD_REQUEST)
	                    .body("Erro:este e-mail já esta cadastrado.");
	        }

	        // VERIFICA SE FOR MENOR DE IDADE
	        if (colaborador.getDataNascimento().plusYears(18).isAfter(LocalDate.now())) {
	            return ResponseEntity
	                    .status(HttpStatus.BAD_REQUEST)
	                    .body("Erro: O usuário deve ser maior de 18 anos.");
	        }

	        // SE ESTIVER TUDO NOS CONFORMES, SALVA O COLABORADOR
	        return ResponseEntity.status(HttpStatus.CREATED).body(colaboradorRepository.save(colaborador));
	    }
	
	 //ATUALIZAR O COLABORADOR ESCOLHIDO
	@PutMapping
	public ResponseEntity<Colaborador> put(@Valid @RequestBody Colaborador colaborador) {
		
		// VERIFICA SE O EMAIL ESTA CADASTRADO
		// USANDO OPTIONAL PARA VERIFICAR A PRESENÇA
		 Optional<Colaborador> colaboradorExistente = colaboradorRepository.findByEmail(colaborador.getEmail());
	     if (colaboradorExistente.isPresent()) {
	    	 
	    	 //CASO JA EXISTA, ENVIA UMA EXEÇÃO 
	    	 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email ja existente!");
	     }
	     
	     //VERIFICA SE ESTA O ID DIGITADO ESTA VAZIO
		if (colaborador.getId() == null)
			return ResponseEntity.badRequest().build();
 
		//SE TIVER TUDO CERTO, ATUALIZA
		if (colaboradorRepository.existsById(colaborador.getId()))
			return ResponseEntity.status(HttpStatus.OK).body(colaboradorRepository.save(colaborador));
	
		//CASO DIGITE UM ID QUE N ESTEJA CADASTRADO, RETORNA NOT FOUND
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
	
	//DELETE POR ID
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		
		//VERIFICA SE O COLABORADOR EXISTE PELO ID
		Optional<Colaborador> colaborador = colaboradorRepository.findById(id);
		if(colaborador.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		
		//CASO EXISTA, DELETE
		colaboradorRepository.deleteById(id);
	}
}
