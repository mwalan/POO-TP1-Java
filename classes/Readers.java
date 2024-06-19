package classes;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class Readers {
    private static final String DOCENTES = "docentes.csv";
    private static final String OCORRENCIAS = "ocorrencias.csv";
    private static final String VEICULOS = "veiculos.csv";
    private static final String QUALIS = "qualis.csv";
    private static final String PUBLICACOES = "publicacoes.csv";
    private static final String REGRAS = "regras.csv";

    public static LocalDate readDate(String string) {
        return LocalDate.parse(string, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public static String readDiretorio(String[] args) throws Exception {
        if (args.length == 0 || args.length > 1 || !Files.isDirectory(Paths.get(args[0])))
            throw new IllegalArgumentException(
                    "Seu programa deve ser executado da seguinte forma: java <nome_classe_com_a_main> <DIRETORIO/ONDE/ESTÃO/OS/CSV>");
        else if (!args[0].endsWith("/"))
            return args[0] + "/";

        return args[0];
    }

    public static LocalDate readAnoRecredenciamento(String diretorio) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            int ano = scanner.nextInt();
            StringBuilder data = new StringBuilder().append("01/01/").append(ano);

            return Readers.readDate(data.toString());
        } catch (Exception exception) {
            throw new IOException("Erro de I/O", exception);
        }
    }

    public static void readPosGraduacao(String diretorio, LocalDate dataRecredenciamento, PPGI ufes) throws Exception {
        readDocentes(diretorio, ufes);
        readOcorrencias(diretorio, dataRecredenciamento, ufes);
        Map<String, Veiculo> veiculos = Readers.readVeiculos(diretorio);
        readQualis(diretorio, dataRecredenciamento, veiculos);
        readPublicacoes(diretorio, dataRecredenciamento, ufes, veiculos);
        readRegras(diretorio, dataRecredenciamento, ufes);
    }

    private static void readDocentes(String diretorio, PPGI ufes) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(diretorio + DOCENTES))) {
            String linha = reader.readLine();

            while ((linha = reader.readLine()) != null) {
                String[] campos = linha.split(";");

                String codigo = campos[0];
                String nome = campos[1];
                LocalDate dataNascimento = readDate(campos[2]);
                LocalDate dataIngresso = readDate(campos[3]);

                for (Docente docente : ufes.getDocentes())
                    if (docente.getCodigo().equals(codigo))
                        throw new IllegalArgumentException("Código repetido para docente: " + codigo + ".");

                ufes.addDocente(new Docente(codigo, nome, dataNascimento, dataIngresso));
            }
        } catch (Exception exception) {
            if (exception instanceof IllegalArgumentException)
                throw exception;
            else
                throw new IOException("Erro de I/O", exception);
        }
    }

    private static void readOcorrencias(String diretorio, LocalDate dataRecredenciamento, PPGI ufes) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(diretorio + OCORRENCIAS))) {
            String linha = reader.readLine();

            reading: while ((linha = reader.readLine()) != null) {
                String[] campos = linha.split(";");

                String codigo = campos[0];
                String evento = campos[1];
                LocalDate inicio = Readers.readDate(campos[2]);
                LocalDate fim = Readers.readDate(campos[3]);

                for (Docente docente : ufes.getDocentes()) {
                    if (docente.getCodigo().equals(codigo)) {
                        if (!Regra.dataValida(inicio, fim, dataRecredenciamento) &&
                                !(evento.equals("Licença Maternidade") &&
                                        fim.getYear() == dataRecredenciamento.getYear() - 1))
                            continue;

                        switch (evento) {
                            case "Bolsista CNPq":
                                docente.setBolsista(true);
                                break;
                            case "Coordenador":
                                docente.setCoordenador(true);
                                break;
                            case "Licença Maternidade":
                                docente.setLicenciado(true);
                                break;
                        }

                        continue reading; // efetua a próxima leitura de ocorrência
                    }
                }

                // se o código de docente não existir, vai iterar sobre todos os docentes sem
                // cair na flag de leitura da nova ocorrência e assim cai no bloco dessa exceção

                StringBuilder mensagem = new StringBuilder()
                        .append("Código de docente não definido usado na ocorrência: ")
                        .append(codigo).append(":").append(evento).append(".");
                throw new IllegalArgumentException(mensagem.toString());
            }
        } catch (Exception exception) {
            if (exception instanceof IllegalArgumentException)
                throw exception;
            else
                throw new IOException("Erro de I/O", exception);
        }
    }

    private static Map<String, Veiculo> readVeiculos(String diretorio) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(diretorio + VEICULOS))) {
            Map<String, Veiculo> veiculos = new HashMap<String, Veiculo>();
            String linha = reader.readLine();

            while ((linha = reader.readLine()) != null) {
                String[] campos = linha.split(";");

                for (int i = 0; i < campos.length; i++)
                    campos[i] = campos[i].trim();

                String sigla = campos[0];
                String nome = campos[1];
                String tipo = campos[2];
                double impacto = campos[3].isEmpty() ? 0 : Double.parseDouble(campos[3].replace(",", "."));
                String issn = campos.length == 5 ? campos[4] : "";

                if (!(tipo.equals("C") || tipo.equals("P"))) {
                    StringBuilder mensagem = new StringBuilder()
                            .append("Tipo de veículo desconhecido para veículo ")
                            .append(sigla).append(": ").append(tipo).append(".");
                    throw new IllegalArgumentException(mensagem.toString());
                }

                veiculos.put(sigla, new Veiculo(sigla, nome, tipo, impacto, issn));
            }

            return veiculos;
        } catch (Exception exception) {
            if (exception instanceof IllegalArgumentException)
                throw exception;
            else
                throw new IOException("Erro de I/O", exception);
        }
    }

    private static void readQualis(String diretorio, LocalDate dataRecredenciamento, Map<String, Veiculo> veiculos)
            throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(diretorio + QUALIS))) {
            String linha = reader.readLine();

            while ((linha = reader.readLine()) != null) {
                String[] campos = linha.split(";");

                int ano = Integer.parseInt(campos[0]);

                if (ano >= dataRecredenciamento.getYear())
                    continue;

                String sigla = campos[1];
                String qualificacao = campos[2];

                if (!Qualis.VALORES.contains(qualificacao)) {
                    StringBuilder mensagem = new StringBuilder()
                            .append("Qualis desconhecido para qualificação do veículo ")
                            .append(sigla).append(" no ano ").append(ano).append(": ").append(qualificacao).append(".");
                    throw new IllegalArgumentException(mensagem.toString());
                }

                if (veiculos.containsKey(sigla))
                    veiculos.get(sigla).setQualis(new Qualis(ano, qualificacao));
                else {
                    StringBuilder mensagem = new StringBuilder()
                            .append("Sigla de veículo não definida usada na qualificação do ano ")
                            .append("\"").append(ano).append("\": ").append(sigla).append(".");
                    throw new IllegalArgumentException(mensagem.toString());
                }
            }
        } catch (Exception exception) {
            if (exception instanceof IllegalArgumentException)
                throw exception;
            else
                throw new IOException("Erro de I/O", exception);
        }
    }

    private static void readPublicacoes(String diretorio, LocalDate dataRecredenciamento, PPGI ufes,
            Map<String, Veiculo> veiculos) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(diretorio + PUBLICACOES))) {
            String linha = reader.readLine();

            while ((linha = reader.readLine()) != null) {
                String[] campos = linha.split(";");

                for (int i = 0; i < campos.length; i++)
                    campos[i] = campos[i].trim();

                int ano = Integer.parseInt(campos[0]);

                String veiculo = campos[1];
                String titulo = campos[2];
                String[] autores = campos[3].split(",");

                for (int i = 0; i < autores.length; i++)
                    autores[i] = autores[i].trim();

                int numero = campos[4].isEmpty() ? 0 : Integer.parseInt(campos[4]);
                int volume = campos[5].isEmpty() ? 0 : Integer.parseInt(campos[5]);
                String local = campos[6];
                int paginaInicial = campos[7].isEmpty() ? 0 : Integer.parseInt(campos[7]);
                int paginaFinal = campos[8].isEmpty() ? 0 : Integer.parseInt(campos[8]);

                Publicacao publicacao = new Publicacao(ano, titulo, numero, volume, local, paginaInicial, paginaFinal);

                searching: for (String autor : autores) {
                    for (Docente docente : ufes.getDocentes()) {
                        if (docente.getCodigo().equals(autor)) {
                            docente.addPublicacao(publicacao);
                            publicacao.addAutor(docente);

                            continue searching;
                        }
                    }

                    StringBuilder mensagem = new StringBuilder()
                            .append("Código de docente não definido usado na publicação \"")
                            .append(titulo).append("\": ").append(autor).append(".");
                    throw new IllegalArgumentException(mensagem.toString());
                }

                if (veiculos.containsKey(veiculo))
                    publicacao.setVeiculo(veiculos.get(veiculo));
                else {
                    StringBuilder mensagem = new StringBuilder()
                            .append("Sigla de veículo não definida usada na publicação \"")
                            .append(titulo).append("\": ").append(veiculo).append(".");
                    throw new IllegalArgumentException(mensagem.toString());
                }

                ufes.addPublicacao(publicacao);
            }
        } catch (Exception exception) {
            if (exception instanceof IllegalArgumentException)
                throw exception;
            else
                throw new IOException("Erro de I/O", exception);
        }
    }

    private static void readRegras(String diretorio, LocalDate dataRecredenciamento, PPGI ufes) throws Exception {
        List<Regra> regras = new LinkedList<Regra>();

        try (BufferedReader reader = new BufferedReader(new FileReader(diretorio + REGRAS))) {
            String linha = reader.readLine();

            while ((linha = reader.readLine()) != null) {
                String[] campos = linha.split(";");

                LocalDate inicio = Readers.readDate(campos[0]);
                LocalDate fim = Readers.readDate(campos[1]);
                String[] qualis = campos[2].split("-");
                String[] pontos = campos[3].split("-");
                int anosPontos = Integer.parseInt(campos[4]);

                for (String valor : qualis) {
                    if (!Qualis.VALORES.contains(valor)) {
                        StringBuilder mensagem = new StringBuilder()
                                .append("Qualis desconhecido para regras de ")
                                .append(inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append(": ")
                                .append(valor).append(".");
                        throw new IllegalArgumentException(mensagem.toString());
                    }
                }

                Map<String, Double> qualisPontos = new HashMap<String, Double>();
                int index = 0;

                // for para contabilizar os valores das qualis dentro dos intervalos definidos
                for (String valor : Qualis.VALORES) {
                    if (index + 1 < qualis.length && valor.equals(qualis[index + 1]))
                        index++;

                    qualisPontos.put(valor, Double.parseDouble(pontos[index].replace(",", ".")));
                }

                qualis = campos[5].split("-");
                pontos = campos[6].split("-");
                int quantidadePeriodicosNecessarios = Integer.parseInt(pontos[0]);
                int anosQuantidade = Integer.parseInt(campos[7]);

                for (String valor : qualis) {
                    if (!Qualis.VALORES.contains(valor)) {
                        StringBuilder mensagem = new StringBuilder()
                                .append("Qualis desconhecido para regras de ")
                                .append(inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append(": ")
                                .append(valor).append(".");
                        throw new IllegalArgumentException(mensagem.toString());
                    }
                }

                List<String> periodicosNecessarios = new LinkedList<String>();
                index = 0;

                for (String valor : Qualis.VALORES) {
                    if (index + 1 < qualis.length && valor.equals(qualis[index + 1]))
                        break;

                    periodicosNecessarios.add(valor);
                }

                double pontosMinimos = Double.parseDouble(campos[8].replace(",", "."));

                for (Regra regra : regras) {
                    if (regra.dataRepetida(inicio, fim)) {
                        StringBuilder mensagem = new StringBuilder(
                                "Múltiplas regras de pontuação para o mesmo período: ")
                                .append(inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                                .append(" : ").append(fim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                                .append(".");
                        throw new IllegalArgumentException(mensagem.toString());
                    }
                }

                regras.add(new Regra(inicio, fim, qualisPontos, anosPontos, periodicosNecessarios,
                        quantidadePeriodicosNecessarios, anosQuantidade, pontosMinimos));
            }

            // seleciona a regra correspondente ao recredenciamento após ler todas
            for (Regra regra : regras)
                if (regra.dataContida(dataRecredenciamento))
                    ufes.setRegra(regra);
        } catch (Exception exception) {
            if (exception instanceof IllegalArgumentException)
                throw exception;
            else
                throw new IOException("Erro de I/O", exception);
        }
    }
}