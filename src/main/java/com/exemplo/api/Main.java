package com.exemplo.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.*;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}

// --- ENTIDADES ---

@Entity
class Perfil {
    @Id private String uid;
    private String nome, email;
    public Perfil() {}
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

@Entity
class Veiculo {
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

@Entity
class Oficina {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id; // Agora gera UUID automaticamente
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

@Entity
class Agendamento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId, oficinaId, servico, mecanico, horario;
    public Agendamento() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
