package unittest;

import com.example.pst.Main;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Created by Setu on 3/3/22
 */
public class IntelligentConverterTest {

    private static Gson gson;
    private static final String inputFile1 = "src/test/resources/input1.txt";
    private static final String inputFile2 = "src/test/resources/input2.txt";
    private static final String inputFile3 = "src/test/resources/input3.txt";

    @BeforeAll
    static void init(){
        gson= constructGson();
    }

    /**
     * Test that input file 3 can be converted successfully
     */
    @Test
    public void testInput3() throws IOException {
        Main main = new Main();
        String inputFile = inputFile3;

        //Convert to JSONL
        main.convertFileWithIntelligence(inputFile);

        //Read input file
        Path inputPath = Paths.get(inputFile);

        //Read output file
        Path outputPath = Paths.get(Main.outputFilename);

        //Test if number of lines is correct
        assert Files.lines(outputPath).count() == Files.lines(inputPath).count()-1;

        String jsonLine = Files.lines(outputPath).skip(1).findAny().get();
        Map<String, Object> output = gson.fromJson(jsonLine, Map.class);

        Assertions.assertAll("Attributes",
                () -> Assertions.assertEquals("United States of America", output.get("name")),
                () -> Assertions.assertEquals("Washington, D.C", output.get("capital")),
                () -> Assertions.assertEquals(331893745d, output.get("population")),
                () -> Assertions.assertEquals(3796742d, output.get("area"))
        );
    }

    /**
     * Test that input file 2 can be converted successfully
     */
    @Test
    public void testInput2() throws IOException {
        Main main = new Main();
        String inputFile = inputFile2;

        //Convert to JSONL
        main.convertFileWithIntelligence(inputFile);

        //Read input file
        Path inputPath = Paths.get(inputFile);

        //Read output file
        Path outputPath = Paths.get(Main.outputFilename);

        //Test if number of lines is correct
        assert Files.lines(outputPath).count() == Files.lines(inputPath).count()-1;

        String jsonLine = Files.lines(outputPath).skip(2).findAny().get();
        Map<String, Object> output = gson.fromJson(jsonLine, Map.class);

        Assertions.assertAll("Attributes",
                () -> Assertions.assertEquals("Marie Salomea", output.get("firstName")),
                () -> Assertions.assertEquals("Sklodowska", output.get("middleName")),
                () -> Assertions.assertEquals("Curie", output.get("lastName")),
                () -> Assertions.assertEquals("Female", output.get("gender")),
                () -> Assertions.assertEquals("1934-07-04", output.get("dateOfBirth")),
                () -> Assertions.assertEquals(3000d, output.get("salary"))
        );
    }

    /**
     * Test that input file 1 can be converted successfully
     */
    @Test
    public void testInput1() throws IOException {
        Main main = new Main();
        String inputFile = inputFile1;

        //Convert to JSONL
        main.convertFileWithIntelligence(inputFile);

        //Read input file
        Path inputPath = Paths.get(inputFile);

        //Read output file
        Path outputPath = Paths.get(Main.outputFilename);

        //Test if number of lines is correct
        assert Files.lines(outputPath).count() == Files.lines(inputPath).count()-1;

        String jsonLine = Files.lines(outputPath).skip(2).findAny().get();
        Map<String, Object> output = gson.fromJson(jsonLine, Map.class);

        Assertions.assertAll("Attributes",
                () -> Assertions.assertEquals("Marie, Salomea", output.get("firstName")),
                () -> Assertions.assertEquals("Sklodowska", output.get("middleName")),
                () -> Assertions.assertEquals("Curie", output.get("lastName")),
                () -> Assertions.assertEquals("Female", output.get("gender")),
                () -> Assertions.assertEquals("1934-07-04", output.get("dateOfBirth")),
                () -> Assertions.assertEquals(3000d, output.get("salary"))
        );
    }

    private static Gson constructGson(){
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, getLocalDateSerializer())
                .create();
    }

    private static JsonSerializer<LocalDate> getLocalDateSerializer() {
        return (date, type, context) ->
                new JsonPrimitive(date.format(DateTimeFormatter.ofPattern(Main.outputDatePattern)));
    }
}
