package classes;

import java.util.*;

public class Qualis {
    private int ano;
    private String valor;

    public static final List<String> VALORES = new LinkedList<>(
            Arrays.asList("A1", "A2", "A3", "A4", "B1", "B2", "B3", "B4", "B5", "C"));

    public Qualis(int ano, String valor) {
        this.ano = ano;
        this.valor = valor;
    }

    public int getAno() {
        return this.ano;
    }

    public String getValor() {
        return this.valor;
    }
}