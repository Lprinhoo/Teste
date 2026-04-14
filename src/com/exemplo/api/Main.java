package com.exemplo.api;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.persistence.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Autowired private PerfilRepository perfilRepo;
    @Autowired private VeiculoRepository veiculoRepo;
    @Autowired private AgendamentoRepo agendamentoRepo;
    @Autowired private OficinaRepository oficinaRepo;

    // --- AUTENTICAÇÃO GOOGLE ---
    @PostMapping("/auth/google")
    public ResponseEntity<?> autenticarGoogle(@RequestBody Map<String, String> body) {
        try {
            String idTokenString = body.get("idToken");
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList("860892024221-smqnn7tgfmm09c00h2ph14330out8p6k.apps.googleusercontent.com"))
                .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                Payload payload = idToken.getPayload();
                String uid = payload.getSubject();
                Perfil perfil = perfilRepo.findById(uid).orElseGet(() -> {
                    Perfil novo = new Perfil();
                    novo.setUid(uid);
                    novo.setNome(payload.get("name") != null ? (String) payload.get("name") : "Usuário Google");
                    novo.setEmail(payload.getEmail());
                    return perfilRepo.save(novo);
                });
                return ResponseEntity.ok(perfil);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // --- PERFIS ---
    @PostMapping("/perfis")
    public ResponseEntity<Perfil> salvarPerfil(@RequestBody Perfil perfil) {
        return ResponseEntity.status(HttpStatus.CREATED).body(perfilRepo.save(perfil));
    }

    @GetMapping("/perfis/email/{email}")
    public ResponseEntity<Perfil> buscarPorEmail(@PathVariable String email) {
        return perfilRepo.findByEmail(email).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // --- VEÍCULOS ---
    @PostMapping("/veiculos")
    public ResponseEntity<Veiculo> salvarVeiculo(@RequestBody Veiculo veiculo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(veiculoRepo.save(veiculo));
    }

    @GetMapping("/veiculos")
    public List<Veiculo> listarVeiculos() {
        return veiculoRepo.findAll();
    }

    // --- OFICINAS (MOTOR PRO SPEC) ---
    @PostMapping("/oficinas")
    public ResponseEntity<Oficina> cadastrarOficina(@RequestBody Oficina oficina) {
        // Salva e retorna 201 Created como esperado pelo sistema desktop
        return ResponseEntity.status(HttpStatus.CREATED).body(oficinaRepo.save(oficina));
    }

    @PutMapping("/oficinas/{id}")
    public ResponseEntity<Oficina> atualizarOficina(@PathVariable String id, @RequestBody Oficina novosDados) {
        return oficinaRepo.findById(id).map(oficina -> {
            oficina.setNome(novosDados.getNome());
            oficina.setLocalidade(novosDados.getLocalidade());
            oficina.setSenha(novosDados.getSenha());
            if (novosDados.getMecanicos() != null) oficina.setMecanicos(novosDados.getMecanicos());
            if (novosDados.getServicos() != null) oficina.setServicos(novosDados.getServicos());
            if (novosDados.getHorarios() != null) oficina.setHorarios(novosDados.getHorarios());
            return ResponseEntity.ok(oficinaRepo.save(oficina));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/oficinas")
    public List<Oficina> listarOficinas() {
        return oficinaRepo.findAll();
    }

    // --- AGENDAMENTOS ---
    @PostMapping("/agendamentos")
    public ResponseEntity<Agendamento> agendar(@RequestBody Agendamento agendamento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agendamentoRepo.save(agendamento));
    }

    @GetMapping("/agendamentos")
    public List<Agendamento> listarAgendamentos() {
        return agendamentoRepo.findAll();
    }
}

// --- MODELOS DE DADOS ---

@Entity class Perfil {
    @Id private String uid;
    private String nome;
    @Column(unique = true) private String email;
    public Perfil() {}
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

@Entity class Veiculo {
    @Id private String placa;
    private String modelo, ano;
    public Veiculo() {}
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getAno() { return ano; }
    public void setAno(String ano) { this.ano = ano; }
}

@Entity class Oficina {
    @Id private String id; // VARCHAR/TEXT no banco para aceitar "LV-0001"
    private String nome, localidade, senha;
    
    @ElementCollection private List<String> mecanicos, servicos, horarios;

    public Oficina() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getLocalidade() { return localidade; }
    public void setLocalidade(String localidade) { this.localidade = localidade; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public List<String> getMecanicos() { return mecanicos; }
    public void setMecanicos(List<String> mecanicos) { this.mecanicos = mecanicos; }
    public List<String> getServicos() { return servicos; }
    public void setServicos(List<String> servicos) { this.servicos = servicos; }
    public List<String> getHorarios() { return horarios; }
    public void setHorarios(List<String> horarios) { this.horarios = horarios; }
}

@Entity class Agendamento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId, oficinaId, servico, mecanico, horario;
    public Agendamento() {}
    public Long getId() { return id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getOficinaId() { return oficinaId; }
    public void setOficinaId(String oficinaId) { this.oficinaId = oficinaId; }
    public String getServico() { return servico; }
    public void setServico(String servico) { this.servico = servico; }
    public String getMecanico() { return mecanico; }
    public void setMecanico(String mecanico) { this.mecanico = mecanico; }
    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }
}

// --- REPOSITÓRIOS ---
interface PerfilRepository extends JpaRepository<Perfil, String> {
    Optional<Perfil> findByEmail(String email);
}
interface VeiculoRepository extends JpaRepository<Veiculo, String> {}
interface AgendamentoRepo extends JpaRepository<Agendamento, Long> {}
interface OficinaRepository extends JpaRepository<Oficina, String> {}
