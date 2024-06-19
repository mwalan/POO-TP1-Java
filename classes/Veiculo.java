package classes;

public class Veiculo {
    private String sigla;
    private String nome;
    private String tipo;
    private double impacto;
    private String issn;
    private Qualis qualis;

    public Veiculo(String sigla, String nome, String tipo, double impacto, String issn) {
        this.sigla = sigla;
        this.nome = nome;
        this.tipo = tipo;
        this.impacto = impacto;
        this.issn = issn;
    }

    public String getSigla() {
        return this.sigla;
    }

    public String getNome() {
        return this.nome;
    }

    public String getTipo() {
        return this.tipo;
    }

    public double getImpacto() {
        return this.impacto;
    }

    public String getIssn() {
        return this.issn;
    }

    public Qualis getQualis() {
        return this.qualis;
    }

    public void setQualis(Qualis qualis) {
        if (this.qualis == null || qualis.getAno() > this.qualis.getAno())
            this.qualis = qualis;
    }
}