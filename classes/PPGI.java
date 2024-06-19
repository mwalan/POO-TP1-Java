package classes;

import java.util.*;

public class PPGI {
    private List<Docente> docentes;
    private List<Publicacao> publicacoes;
    private Regra regra;

    public PPGI() {
        this.docentes = new LinkedList<Docente>();
        this.publicacoes = new LinkedList<Publicacao>();
    }

    public List<Docente> getDocentes() {
        return this.docentes;
    }

    public List<Publicacao> getPublicacoes() {
        return this.publicacoes;
    }

    public Regra getRegra() {
        return this.regra;
    }

    public void addDocente(Docente docente) {
        this.docentes.add(docente);
    }

    public void addPublicacao(Publicacao publicacao) {
        this.publicacoes.add(publicacao);
    }

    public void setRegra(Regra regra) {
        this.regra = regra;
    }
}