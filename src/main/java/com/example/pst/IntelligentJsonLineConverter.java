package com.example.pst;

import com.google.gson.Gson;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * An implementation of Converter which can convert any arbitrary dta
 * Created by Setu on 3/3/22
 */
public class IntelligentJsonLineConverter extends JsonLineConverter{

    private final List<String> fields;
    private final String splitter;
    private final Gson gson;

    public IntelligentJsonLineConverter(String delimeter, String heading, Gson gson) {
        super(delimeter);
        this.fields = getFieldNames(heading, delimeter);
        splitter = delimeter + splitterPostfix;
        this.gson = gson;
    }

    /**
     * Converts a line of text to Person object
     * @param input - text to convert
     * @return - Person object
     */
    @Override
    public String intelligentConvert(String input) {
        String[] attributes = input.split(splitter, -1);

        if(attributes.length != fields.size()){
            System.out.printf("Could not parse line %s%n", input);
            return null;
        }

        Map<String, Object> values = new HashMap<>();
        IntStream.range(0, fields.size()).forEach(i -> {
            String text = trim(attributes[i]);
            Object value = processText(text);
            values.put(fields.get(i), value);
        });

        return gson.toJson(values);
    }

    /**
     * Try to parse long, double and date
     * @param text - text to be processed
     * @return Processed object, if cannot be processed the text itself would be returned
     */
    private Object processText(String text){
        if(text == null || text.length() == 0)
            return null;

        //1. Parse as long
        try{
            return Long.parseLong(text);
        }catch (NumberFormatException nfe){}

        //2. Parse as double
        try{
            return Double.parseDouble(text);
        }catch (NumberFormatException nfe){}

        //3. Parse as double
        LocalDate date = dateFormatters.stream()
                .map(formatter -> parseDate(text, formatter))
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);

        //If date is null, then return string
        return date == null ? text : date;
    }

    private List<String> getFieldNames(String heading, String seperator){
        return Arrays.stream(heading.split(seperator)).map(String::trim).collect(Collectors.toList());
    }
}
