package classes;

import java.util.*;

public class Publicacao {
    private int ano;
    private Veiculo veiculo;
    private String titulo;
    private List<Docente> autores;
    private int numero;
    private int volume;
    private String local;
    private int paginaInicial;
    private int paginaFinal;

    public Publicacao(int ano, String titulo, int numero, int volume, String local, int paginaInicial,
            int paginaFinal) {
        this.ano = ano;
        this.titulo = titulo;
        this.autores = new LinkedList<Docente>();
        this.numero = numero;
        this.volume = volume;
        this.local = local;
        this.paginaInicial = paginaInicial;
        this.paginaFinal = paginaFinal;
    }

    public int getAno() {
        return this.ano;
    }

    public Veiculo getVeiculo() {
        return this.veiculo;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public List<Docente> getAutores() {
        return this.autores;
    }

    public int getNumero() {
        return this.numero;
    }

    public int getVolume() {
        return this.volume;
    }

    public String getLocal() {
        return this.local;
    }

    public int getPaginaInicial() {
        return this.paginaInicial;
    }

    public int getPaginaFinal() {
        return this.paginaFinal;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public void addAutor(Docente autor) {
        this.autores.add(autor);
    }
}