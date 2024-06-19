package classes;

import java.time.*;
import java.util.*;

public class Regra {
    private LocalDate inicio;
    private LocalDate fim;
    private Map<String, Double> qualisPontos;
    private int anosPontos;
    private List<String> periodicosNecessarios;
    private int quantidadePeriodicosNecessarios;
    private int anosPeriodicos;
    private double pontosMinimos;

    public Regra(LocalDate inicio, LocalDate fim, Map<String, Double> qualisPontos, int anosPontos,
            List<String> periodicosNecessarios, int quantidadePeriodicosNecessarios, int anosPeriodicos,
            double pontosMinimos) {
        this.inicio = inicio;
        this.fim = fim;
        this.qualisPontos = qualisPontos;
        this.anosPontos = anosPontos;
        this.periodicosNecessarios = periodicosNecessarios;
        this.quantidadePeriodicosNecessarios = quantidadePeriodicosNecessarios;
        this.anosPeriodicos = anosPeriodicos;
        this.pontosMinimos = pontosMinimos;
    }

    public LocalDate getInicio() {
        return this.inicio;
    }

    public LocalDate getFim() {
        return this.fim;
    }

    public Map<String, Double> getQualisPontos() {
        return this.qualisPontos;
    }

    public int getAnosPontos() {
        return this.anosPontos;
    }

    public List<String> getPeriodicosNecessarios() {
        return this.periodicosNecessarios;
    }

    public int getQuantidadePeriodicosNecessarios() {
        return this.quantidadePeriodicosNecessarios;
    }

    public int getAnosPeriodicos() {
        return this.anosPeriodicos;
    }

    public double getPontosMinimos() {
        return this.pontosMinimos;
    }

    // verifica se a data alvo está dentro do intervalo fechado de inicio e fim
    public static boolean dataValida(LocalDate inicio, LocalDate fim, LocalDate alvo) {
        return (alvo.isEqual(inicio) || alvo.isAfter(inicio)) && (alvo.isBefore(fim) || alvo.isEqual(fim));
    }

    // verifica se a data alvo cobre o mesmo intervalo que a data argumento
    public boolean dataRepetida(LocalDate inicio, LocalDate fim) {
        return (this.inicio.isEqual(inicio) || this.inicio.isBefore(inicio)) &&
                (this.fim.isEqual(fim) || this.fim.isAfter(fim));
    }

    // verifica se um dia específico está contido no intervalo de tempo
    public boolean dataContida(LocalDate alvo) {
        return (this.inicio.isEqual(alvo) || this.inicio.isBefore(alvo)) &&
                (this.fim.isEqual(alvo) || this.fim.isAfter(alvo));
    }
}