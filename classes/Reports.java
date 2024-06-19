package classes;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class Reports {
    private static final String RECREDENCIAMENTO = "saida/1-recredenciamento.csv";
    private static final String PUBLICACOES = "saida/2-publicacoes.csv";
    private static final String ESTATISTICAS = "saida/3-estatisticas.csv";

    public static void writeRelatorios(String diretorio, LocalDate dataRecredenciamento, PPGI ufes) throws Exception {
        createRelatorios(diretorio);
        writeRecredenciamento(diretorio, dataRecredenciamento, ufes);
        writePublicacoes(diretorio, ufes);
        writeEstatisticas(diretorio, ufes);
    }

    public static void createRelatorios(String diretorio) throws Exception {
        try {
            File saida = new File(diretorio + "/saida");

            if (!saida.exists())
                saida.mkdirs();

            new File(diretorio + RECREDENCIAMENTO).createNewFile();
            new File(diretorio + PUBLICACOES).createNewFile();
            new File(diretorio + ESTATISTICAS).createNewFile();
        } catch (Exception exception) {
            throw new IOException("Erro de I/O", exception);
        }
    }

    private static void writeRecredenciamento(String diretorio, LocalDate dataRecredenciamento, PPGI ufes)
            throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(diretorio + RECREDENCIAMENTO))) {
            writer.write("Docente;Pontuação;Recredenciado?\n");

            Regra regra = ufes.getRegra();
            Map<String, Double> qualisPontos = regra.getQualisPontos();
            List<String> periodicosNecessarios = regra.getPeriodicosNecessarios();
            int quantidadePeriodicosNecessarios = regra.getQuantidadePeriodicosNecessarios();
            int anoRecredenciamento = dataRecredenciamento.getYear();

            List<Docente> docentes = ufes.getDocentes();
            docentes.sort(Comparator.comparing(Docente::getNome));

            for (Docente docente : docentes) {
                double pontos = 0;

                for (Publicacao publicacao : docente.getPublicacoes()) {
                    if (!(anoRecredenciamento - publicacao.getAno() <= regra.getAnosPontos()) ||
                            publicacao.getAno() >= anoRecredenciamento)
                        continue;

                    String qualis = publicacao.getVeiculo().getQualis().getValor();
                    pontos += qualisPontos.get(qualis);
                }

                int quantidade = 0;

                for (Publicacao publicacao : docente.getPublicacoes()) {
                    if (!(anoRecredenciamento - publicacao.getAno() <= regra.getAnosPeriodicos())
                            || publicacao.getAno() >= anoRecredenciamento
                            || !publicacao.getVeiculo().getTipo().equals("P"))
                        continue;

                    String qualis = publicacao.getVeiculo().getQualis().getValor();

                    if (periodicosNecessarios.contains(qualis))
                        quantidade++;
                }

                int diaIngresso = docente.getDataIngresso().getDayOfMonth();
                int mesIngresso = docente.getDataIngresso().getMonthValue();
                int diffIngresso = anoRecredenciamento - docente.getDataIngresso().getYear();
                int diffNascimento = anoRecredenciamento - docente.getDataNascimento().getYear();

                String recredenciado = "Não";

                if (docente.isBolsista())
                    recredenciado = "Bolsista CNPq";
                else if (docente.isCoordenador())
                    recredenciado = "Coordenador";
                else if (docente.isLicenciado())
                    recredenciado = "Licença Maternidade";
                else if (diffIngresso < 2 || (diffIngresso == 2 && mesIngresso == 1 && diaIngresso == 1))
                    recredenciado = "PPJ";
                else if (diffNascimento > 60 || (diffNascimento == 60 && mesIngresso == 1 && diaIngresso == 1))
                    recredenciado = "PPS";
                else if (pontos >= regra.getPontosMinimos() && quantidade >= quantidadePeriodicosNecessarios)
                    recredenciado = "Sim";

                String nome = docente.getNome();
                String pontuacao = String.format("%.1f", pontos);

                writer.write(nome + ";" + pontuacao + ";" + recredenciado + "\n");
            }
        } catch (Exception exception) {
            throw new IOException("Erro de I/O", exception);
        }
    }

    private static void writePublicacoes(String diretorio, PPGI ufes) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(diretorio + PUBLICACOES))) {
            writer.write("Ano;Sigla Veículo;Veículo;Qualis;Fator de Impacto;Título;Docentes\n");

            List<Publicacao> publicacoes = ufes.getPublicacoes();
            publicacoes.sort(
                    Comparator.comparing((Publicacao p) -> p.getVeiculo().getQualis().getValor())
                            .thenComparing(Publicacao::getAno, Comparator.reverseOrder())
                            .thenComparing(p -> p.getVeiculo().getSigla())
                            .thenComparing(Publicacao::getTitulo));

            for (Publicacao publicacao : publicacoes) {
                int ano = publicacao.getAno();
                String sigla = publicacao.getVeiculo().getSigla();
                String veiculo = publicacao.getVeiculo().getNome();
                String qualis = publicacao.getVeiculo().getQualis().getValor();
                String fatorImpacto = String.format("%.3f", publicacao.getVeiculo().getImpacto());
                String titulo = publicacao.getTitulo();
                StringBuilder autores = new StringBuilder();

                for (Docente docente : publicacao.getAutores())
                    autores.append(docente.getNome()).append(",");

                String docentes = autores.toString().substring(0, autores.length() - 1);

                writer.write(ano + ";" + sigla + ";" + veiculo + ";" + qualis + ";" + fatorImpacto + ";" + titulo + ";"
                        + docentes + "\n");
            }
        } catch (Exception exception) {
            throw new IOException("Erro de I/O", exception);
        }
    }

    private static void writeEstatisticas(String diretorio, PPGI ufes) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(diretorio + ESTATISTICAS))) {
            writer.write("Qualis;Qtd. Artigos;Média Artigos / Docente\n");

            Map<String, Integer> qualisArtigos = new HashMap<String, Integer>();
            Map<String, Double> qualisDocentes = new HashMap<String, Double>();

            for (Publicacao publicacao : ufes.getPublicacoes()) {
                String qualis = publicacao.getVeiculo().getQualis().getValor();

                // Indica o número total de artigos publicados em veículos com o
                // respectivo Qualis (somatório em que cada artigo soma 1)
                int artigos = qualisArtigos.getOrDefault(qualis, 0) + 1;

                // Indica o número de artigos por docente em cada Qualis (mesmo somatório, só
                // que cada artigo soma 1 dividido pelo número de docentes autores)
                Double media = qualisDocentes.getOrDefault(qualis, 0.0) + (1.0 / publicacao.getAutores().size());

                qualisArtigos.put(qualis, artigos);
                qualisDocentes.put(qualis, media);
            }

            for (String qualis : Qualis.VALORES) {
                int artigos = qualisArtigos.getOrDefault(qualis, 0);
                String media = String.format("%.2f", qualisDocentes.getOrDefault(qualis, 0.0));

                writer.write(qualis + ";" + artigos + ";" + media + "\n");
            }
        } catch (Exception exception) {
            throw new IOException("Erro de I/O", exception);
        }
    }
}