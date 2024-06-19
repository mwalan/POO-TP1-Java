package classes;

import java.time.*;
import java.util.*;

public class Docente {
    private String codigo;
    private String nome;
    private LocalDate dataNascimento;
    private LocalDate dataIngresso;
    private boolean bolsista;
    private boolean coordenador;
    private boolean licenciado;
    private List<Publicacao> publicacoes;

    public Docente(String codigo, String nome, LocalDate dataNascimento, LocalDate dataIngresso) {
        this.codigo = codigo;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.dataIngresso = dataIngresso;
        this.bolsista = false;
        this.coordenador = false;
        this.licenciado = false;
        this.publicacoes = new LinkedList<Publicacao>();
    }

    public String getCodigo() {
        return this.codigo;
    }

    public String getNome() {
        return this.nome;
    }

    public LocalDate getDataNascimento() {
        return this.dataNascimento;
    }

    public LocalDate getDataIngresso() {
        return this.dataIngresso;
    }

    public boolean isBolsista() {
        return this.bolsista;
    }

    public boolean isCoordenador() {
        return this.coordenador;
    }

    public boolean isLicenciado() {
        return this.licenciado;
    }

    public List<Publicacao> getPublicacoes() {
        return this.publicacoes;
    }

    public void setBolsista(boolean status) {
        this.bolsista = status;
    }

    public void setCoordenador(boolean status) {
        this.coordenador = status;
    }

    public void setLicenciado(boolean status) {
        this.licenciado = status;
    }

    public void addPublicacao(Publicacao publicacao) {
        this.publicacoes.add(publicacao);
    }
}