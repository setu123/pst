package unittest;

import com.example.pst.Converter;
import com.example.pst.JsonLineConverter;
import com.example.pst.Main;
import com.example.pst.Person;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by Setu on 3/1/22
 */
public class ConverterTest {

    private static Gson gson;
    private static final String inputFile1 = "src/test/resources/input1.txt";
    private static final String inputFile2 = "src/test/resources/input2.txt";
    private static final String inputFile3 = "src/test/resources/input3.txt";

    @BeforeAll
    static void init(){
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, getLocalDateDeSerializer())
                .create();
    }

    private static JsonDeserializer<LocalDate> getLocalDateDeSerializer() {
        return (json, type, context) ->
                LocalDate.parse(json.getAsString(), DateTimeFormatter.ofPattern(Main.outputDatePattern));
    }

    /**
     * Test that a comma seperated line can be converted successfully
     */
    @Test
    public void convertLineUsingCommaSeperator(){
        Converter converter = new JsonLineConverter(",");
        String firstName = "Marie, Salomea";
        String middleName = "Sklodowska";
        String lastName = "Curie";
        String gender = "Female";
        LocalDate dateOfBirth = LocalDate.of(1934, 7, 4);
        Integer salary = 3000;
        String line = "\"Marie, Salomea\",Sklodowska |,\"Curie\",Female,04-07-1934,3000";

        Person person = converter.convert(line);

        Assertions.assertAll("Person attributes",
                () -> Assertions.assertEquals(person.getFirstName(), firstName),
                () -> Assertions.assertEquals(person.getMiddleName(), middleName),
                () -> Assertions.assertEquals(person.getLastName(), lastName),
                () -> Assertions.assertEquals(person.getGender(), gender),
                () -> Assertions.assertEquals(person.getDateOfBirth(), dateOfBirth),
                () -> Assertions.assertEquals(person.getSalary(), salary)
        );
    }

    /**
     * Test that a pipe seperated line can be converted successfully
     */
    @Test
    public void convertLineUsingPipeSeperator(){
        Converter converter = new JsonLineConverter("\\|");
        String firstName = "Marie Salomea";
        String middleName = "Sklodowska";
        String lastName = "Curie";
        String gender = "Female";
        LocalDate dateOfBirth = LocalDate.of(1934, 7, 4);
        Integer salary = 3000;
        String line = "\"Marie| Salomea\"|Sklodowska,|\"Curie\"|Female|04-07-1934|3000";

        Person person = converter.convert(line);

        Assertions.assertAll("Person attributes",
                () -> Assertions.assertEquals(person.getFirstName(), firstName),
                () -> Assertions.assertEquals(person.getMiddleName(), middleName),
                () -> Assertions.assertEquals(person.getLastName(), lastName),
                () -> Assertions.assertEquals(person.getGender(), gender),
                () -> Assertions.assertEquals(person.getDateOfBirth(), dateOfBirth),
                () -> Assertions.assertEquals(person.getSalary(), salary)
        );
    }

    /**
     * Test that a comma seperated file can be converted successfully
     */
    @Test
    public void convertCSVToJSONLFile() throws IOException {
        Main main = new Main();

        //Convert to JSONL
        main.convertFile(inputFile1);

        //Read input file
        Path inputPath = Paths.get(inputFile1);

        //Read output file
        Path outputPath = Paths.get(Main.outputFilename);

        //Test if number of lines is correct
        assert Files.lines(outputPath).count() == Files.lines(inputPath).count()-1;

        //Test the third 3rd element. Read 3rd element from input file, read 3rd element from output file
        //And then compare
        String textLine = Files.lines(inputPath).skip(3).findAny().get();
        Person inputPerson = new JsonLineConverter(",").convert(textLine);

        String jsonLine = Files.lines(outputPath).skip(2).findAny().get();
        Person outputPerson = gson.fromJson(jsonLine, Person.class);

        Assertions.assertAll("Person attributes",
                () -> Assertions.assertEquals(outputPerson.getFirstName(), inputPerson.getFirstName()),
                () -> Assertions.assertEquals(outputPerson.getMiddleName(), inputPerson.getMiddleName()),
                () -> Assertions.assertEquals(outputPerson.getLastName(), inputPerson.getLastName()),
                () -> Assertions.assertEquals(outputPerson.getGender(), inputPerson.getGender()),
                () -> Assertions.assertEquals(outputPerson.getDateOfBirth(), inputPerson.getDateOfBirth()),
                () -> Assertions.assertEquals(outputPerson.getSalary(), inputPerson.getSalary())
        );
    }

    /**
     * Test that a pipe seperated file can be converted successfully
     */
    @Test
    public void convertPSVToJSONLFile() throws IOException {
        Main main = new Main();

        //Convert to JSONL
        main.convertFile(inputFile2);

        //Read input file
        Path inputPath = Paths.get(inputFile2);

        //Read output file
        Path outputPath = Paths.get(Main.outputFilename);

        //Test if number of lines is correct
        assert Files.lines(outputPath).count() == Files.lines(inputPath).count()-1;

        //Test the third 3rd element. Read 3rd element from input file, read 3rd element from output file
        //And then compare
        String textLine = Files.lines(inputPath).skip(3).findAny().get();
        Person inputPerson = new JsonLineConverter("\\|").convert(textLine);

        String jsonLine = Files.lines(outputPath).skip(2).findAny().get();
        Person outputPerson = gson.fromJson(jsonLine, Person.class);

        Assertions.assertAll("Person attributes",
                () -> Assertions.assertEquals(outputPerson.getFirstName(), inputPerson.getFirstName()),
                () -> Assertions.assertEquals(outputPerson.getMiddleName(), inputPerson.getMiddleName()),
                () -> Assertions.assertEquals(outputPerson.getLastName(), inputPerson.getLastName()),
                () -> Assertions.assertEquals(outputPerson.getGender(), inputPerson.getGender()),
                () -> Assertions.assertEquals(outputPerson.getDateOfBirth(), inputPerson.getDateOfBirth()),
                () -> Assertions.assertEquals(outputPerson.getSalary(), inputPerson.getSalary())
        );
    }
}
