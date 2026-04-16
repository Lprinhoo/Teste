package com.exemplo.api;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class ApiController {

    private final PerfilRepository perfilRepo;
    private final VeiculoRepository veiculoRepo;
    private final AgendamentoRepo agendamentoRepo;
    private final OficinaRepository oficinaRepo;

    @Autowired
    public ApiController(PerfilRepository perfilRepo, VeiculoRepository veiculoRepo, 
                          AgendamentoRepo agendamentoRepo, OficinaRepository oficinaRepo) {
        this.perfilRepo = perfilRepo;
        this.veiculoRepo = veiculoRepo;
        this.agendamentoRepo = agendamentoRepo;
        this.oficinaRepo = oficinaRepo;
    }

    @GetMapping("/")
    public String home() {
        return "API de Oficinas Online no Railway!";
    }

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

    @PostMapping("/perfis")
    public ResponseEntity<Perfil> salvarPerfil(@RequestBody Perfil perfil) {
        return ResponseEntity.status(HttpStatus.CREATED).body(perfilRepo.save(perfil));
    }

    @GetMapping("/perfis/email/{email}")
    public ResponseEntity<Perfil> buscarPorEmail(@PathVariable String email) {
        return perfilRepo.findByEmail(email).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/veiculos")
    public ResponseEntity<Veiculo> salvarVeiculo(@RequestBody Veiculo veiculo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(veiculoRepo.save(veiculo));
    }

    @GetMapping("/veiculos")
    public List<Veiculo> listarVeiculos() { return veiculoRepo.findAll(); }

    @PostMapping("/oficinas")
    public ResponseEntity<Oficina> cadastrarOficina(@RequestBody Oficina oficina) {
        return ResponseEntity.status(HttpStatus.CREATED).body(oficinaRepo.save(oficina));
    }

    @PutMapping("/oficinas/{id}")
    public ResponseEntity<Oficina> atualizarOficina(@PathVariable Long id, @RequestBody Oficina novosDados) {
        return oficinaRepo.findById(id).map(oficina -> {
            oficina.setNome(novosDados.getNome());
            oficina.setLocalidade(novosDados.getLocalidade());
            oficina.setSenha(novosDados.getSenha());
            oficina.setEndereco(novosDados.getEndereco());
            oficina.setLatitude(novosDados.getLatitude());
            oficina.setLongitude(novosDados.getLongitude());
            if (novosDados.getMecanicos() != null) oficina.setMecanicos(novosDados.getMecanicos());
            if (novosDados.getServicos() != null) oficina.setServicos(novosDados.getServicos());
            if (novosDados.getHorarios() != null) oficina.setHorarios(novosDados.getHorarios());
            return ResponseEntity.ok(oficinaRepo.save(oficina));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/oficinas")
    public List<Oficina> listarOficinas() { return oficinaRepo.findAll(); }

    @PostMapping("/agendamentos")
    public ResponseEntity<Agendamento> agendar(@RequestBody Agendamento agendamento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agendamentoRepo.save(agendamento));
    }

    @GetMapping("/agendamentos")
    public List<Agendamento> listarAgendamentos() { return agendamentoRepo.findAll(); }
}
