package com.exemplo.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.*;
import java.util.List;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "*")
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    private final MensagemRepository repo;

    public Main(MensagemRepository repo) {
        this.repo = repo;
    }

    // ENDPOINT DE SAUDE MANUAL (Para o Railway saber que estamos vivos)
    @GetMapping("/health")
    public String health() {
        return "UP";
    }

    @PostMapping("/mensagem")
    public ResponseEntity<Mensagem> salvar(@RequestBody Mensagem mensagem) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(mensagem));
    }

    @GetMapping("/mensagem")
    public List<Mensagem> listar() {
        return repo.findAll();
    }
}

@Entity
class Mensagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String conteudo;

    public Mensagem() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }
}

interface MensagemRepository extends JpaRepository<Mensagem, Long> {}
