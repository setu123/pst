package com.example.pst;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by Setu on 2/28/22
 */
public class Main {

    public static final String[] supportedDelimeters = {",", "\\|"};
    private static final int totalFields = 6;
    public static final String outputDatePattern = "yyyy-MM-dd";
    public static final String outputFilename = "output.jsonl";

    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("Exactly 1 argument(input file) is required");
            System.exit(0);
        }

        String inputFile = args[0];

        Main main = new Main();
        main.convertFileWithIntelligence(inputFile);
    }

    /**
     * Convert an input text file into JSONL file
     * @param inputFile - File name along with path to convert
     * @throws IOException - Throws exception in case file not found or cannot be read/write
     */
    public void convertFile(String inputFile) throws IOException {
        //Construct gson object to be used to write to json
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, getLocalDateSerializer())
                .create();

        Path path = Paths.get(inputFile);

        //Get the first line to be used to determine delemeter
        String heading = Files.lines(path).findFirst().get();

        //Get appropriate delimiter based on heading line
        Optional<String> delimiter = getDelimeter(heading);
        if (!delimiter.isPresent()) {
            System.out.println("Delimiter not supported. Exiting");
            System.exit(0);
        }

        //Construct converter to be used to convert
        Converter converter = new JsonLineConverter(delimiter.get());

        //Get writer to write json lines
        BufferedWriter bw = Files.newBufferedWriter(Paths.get(outputFilename));

        Files.lines(path)   //Stream of lines
                .skip(1)    //Skip the first one as its header text
                .map(converter::convert)    //Convert to Person object
                .forEach(person -> {        //For each person write it to file
                    try {
                        bw.write(gson.toJson(person));
                        bw.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        bw.close(); //Close the output buffer
    }

    /**
     * Convert an input text file into JSONL file. Date types would be formatted as much as possible
     * @param inputFile - File name along with path to convert
     * @throws IOException - Throws exception in case file not found or cannot be read/write
     */
    public void convertFileWithIntelligence(String inputFile) throws IOException {
        //Construct gson object to be used to write to json
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, getLocalDateSerializer())
                .create();

        Path path = Paths.get(inputFile);

        //Get the first line to be used to determine delemeter
        String heading = Files.lines(path).findFirst().get();

        //Get appropriate delimiter based on heading line
        Optional<String> delimiter = getDelimeter(heading);
        if (!delimiter.isPresent()) {
            System.out.println("Delimiter not supported. Exiting");
            System.exit(0);
        }

        //Construct converter to be used to convert
        Converter converter = new IntelligentJsonLineConverter(delimiter.get(), heading, gson);

        //Get writer to write json lines
        BufferedWriter bw = Files.newBufferedWriter(Paths.get(outputFilename));

        Files.lines(path)   //Stream of lines
                .skip(1)    //Skip the first one as its header text
                .map(converter::intelligentConvert)    //Convert to Person object
                .forEach(jsonText -> {        //For each person write it to file
                    try {
                        bw.write(jsonText);
                        bw.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        bw.close(); //Close the output buffer
    }

    /**
     * Construct serializer to be used to write to json
     * @return Serializer for LocalDate
     */
    private JsonSerializer<LocalDate> getLocalDateSerializer() {
        return (date, type, context) ->
                new JsonPrimitive(date.format(DateTimeFormatter.ofPattern(outputDatePattern)));
    }

    /**
     * Determine delemeter to be used based on heading of the input file
     * @param heading - First line of the input file
     * @return - Delemeter
     */
    private Optional<String> getDelimeter(String heading) {
        return Arrays.stream(supportedDelimeters)
                .filter(delimeter -> heading.split(delimeter).length > 1)
                .findAny();
    }
}
