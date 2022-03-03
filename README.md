# Converts delimiter seperated input file into jsonl file

## Requirement
Minimum java version 8 is required

## Build
Run the following command to build the project

_mvn package_

## Run
Run the following command to run the converter. An executable is shipped with this converter. After successful run an output file named output.jsonl would be generated in working directory

_java -jar pst-1.0-SNAPSHOT-jar-with-dependencies.jar <input_file_path_and_name>_

## Example
_java -jar pst-1.0-SNAPSHOT-jar-with-dependencies.jar src/test/resources/input1.txt_
