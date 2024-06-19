import classes.*;
import java.time.*;

public class Main {
    public static void main(String[] args) throws Exception {
        String diretorio = Readers.readDiretorio(args);

        try {
            LocalDate dataRecredenciamento = Readers.readAnoRecredenciamento(diretorio);
            PPGI ufes = new PPGI();
            Readers.readPosGraduacao(diretorio, dataRecredenciamento, ufes);
            Reports.writeRelatorios(diretorio, dataRecredenciamento, ufes);
        } catch (Exception exception) {
            Reports.createRelatorios(diretorio);
            System.out.println(exception.getMessage());
        }
    }
}