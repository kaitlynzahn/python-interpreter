package com.popl.python_interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;


public class PythonInterpreter {

    private static HashMap<String, String> variables = new HashMap<String, String>();
    private static List<String> fileLines = new ArrayList<String>();

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Scanner fileScanner;
        String fileName;
        File pythonFile;
      
        // temporary, for testing
        // variables.put("charmender_attack", "knife");
        // variables.put("charmender_HP", "5");
        // variables.put("squirtle_HP", "2");
        variables.put("num", "2");
        variables.put("i", "2");
        variables.put("eq1", "1");
        // variables.put("name", "Sam");
        // variables.put("turn", "1");
        // variables.put("eq1", "1");
        
        //prompt user to insert their file name and save variable for the file
        System.out.println("Enter the name of your Python file (ex: script.py): ");
        fileName = scan.nextLine();

        //try to read the file
        try {
            //create a python file and scan it
            pythonFile = new File(fileName);
            fileScanner = new Scanner(pythonFile);
            //for every line in the file
            while (fileScanner.hasNextLine()) {
                String data = fileScanner.nextLine();
                //ignore lines with comments, read file
                if (!data.matches("#.*") && !data.matches("\s*")) {
                    fileLines.add(data);
                }
            }
            fileScanner.close();
        } 
        //throw errors
        catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        int lineNum = 0;

        while(lineNum < fileLines.size()) {
            // System.out.println(lineNum + ": " + fileLines.get(lineNum));
            lineNum = interpretLine(lineNum);
            if(lineNum < 0) {
                System.out.println("An error occurred.");
            }
        }

        scan.close();
    }

    private static int interpretLine(int lineNum) {
		String line = fileLines.get(lineNum);
        if(line.matches("\s*while.*")) {
            // call while function
            // return new line num
            lineNum++;
        }
        else if(line.matches("\s*for.*")) {
            // call for function
            // return new line num
            lineNum++;
        }
        else if(line.matches("\s*if.*")) {
            // call if function
            lineNum = handleIf(lineNum);
        }
        else if(line.matches("\s*print.*")) {
            // call print function
            print(line);
            lineNum++;
        }
        else if(line.matches("\s*[a-zA-Z_]+.*")){
            // call variable handling
			// return new line num
            lineNum = handleVariable(line, lineNum);
        }
        else {
            System.out.println("An error occurred.\n");
            System.out.println(line);
            lineNum = -1;
        }
        return lineNum;
    }

    private static int handleVariable(String line, int lineNum) {
        // to temporarily ignore until if function implemented
        if(line.contains("elif") || line.contains("else")) {
            return ++lineNum;
		}
        if(line.contains("-=")) {
            String[] tokens = line.split("-=");
            if(variables.containsKey(tokens[0].trim())) {
                Integer oldNum = Integer.parseInt(variables.get(tokens[0].trim()));
                Integer newNum = oldNum - Integer.parseInt(variables.get(tokens[1].trim()));
                variables.replace(tokens[0].trim(), Integer.toString(newNum));
            }
            else {
                // invalid operation, variable does not exist
                lineNum = -2;
            }
        }
        else if(line.contains("+=")) {
            String[] tokens = line.split("+=");
            if(variables.containsKey(tokens[0].trim())) {
                Integer oldNum = Integer.parseInt(variables.get(tokens[0].trim()));
                Integer newNum = oldNum + Integer.parseInt(variables.get(tokens[1].trim()));
                variables.replace(tokens[0].trim(), Integer.toString(newNum));
            }
            else {
                // invalid operation, variable does not exist
                lineNum = -2;
            }
		}
		else if(line.contains("*=")) {
            String[] tokens = line.split("*=");
            if(variables.containsKey(tokens[0].trim())) {
                Integer oldNum = Integer.parseInt(variables.get(tokens[0].trim()));
                Integer newNum = oldNum * Integer.parseInt(variables.get(tokens[1].trim()));
                variables.replace(tokens[0].trim(), Integer.toString(newNum));
            }
            else {
                // invalid operation, variable does not exist
                lineNum = -2;
            }
		}
		else if(line.contains("/=")) {
            String[] tokens = line.split("/=");
            if(variables.containsKey(tokens[0].trim())) {
                Integer oldNum = Integer.parseInt(variables.get(tokens[0].trim()));
                Integer newNum = oldNum / Integer.parseInt(variables.get(tokens[1].trim()));
                variables.replace(tokens[0].trim(), Integer.toString(newNum));
            }
            else {
                // invalid operation, variable does not exist
                lineNum = -2;
            }
		}
		else if(line.contains("^=")) {
            String[] tokens = line.split("^=");
            if(variables.containsKey(tokens[0].trim())) {
                Integer oldNum = Integer.parseInt(variables.get(tokens[0].trim()));
                double newNum = Math.pow(oldNum, Integer.parseInt(variables.get(tokens[1].trim())));
                Integer newNumInt = (int)newNum;
                variables.replace(tokens[0].trim(), newNumInt.toString());
            }
            else {
                // invalid operation, variable does not exist
                lineNum = -2;
            }
		}
		else if(line.contains("%=")) {
            String[] tokens = line.split("%=");
            if(variables.containsKey(tokens[0].trim())) {
                Integer oldNum = Integer.parseInt(variables.get(tokens[0].trim()));
                Integer newNum = oldNum % Integer.parseInt(variables.get(tokens[1].trim()));
                variables.replace(tokens[0].trim(), Integer.toString(newNum));
            }
            else {
                // invalid operation, variable does not exist
                lineNum = -2;
            }
        }
        else if(line.contains("=")) {
            String[] tokens = line.split("=");
            String newValue;
            if(variables.containsKey(tokens[0].trim())) {
                if(tokens[1].matches("(?:[0-9 ()]+[*+/-])+[0-9 ()]+")) {
                    newValue = calculate(tokens[1]).toString();
                }
                else {
                    newValue = tokens[1].trim();
                }
                variables.replace(tokens[0].trim(), newValue);
            }
            else {
                // check for an equation
                if(tokens[1].matches("(?:[0-9 ()]+[*+/-])+[0-9 ()]+")) {
                    newValue = calculate(tokens[1]).toString();
                }
                else {
                    newValue = tokens[1].trim();
                }
                variables.put(tokens[0].trim(), newValue);
            }
        }
        return ++lineNum;
    }

    private static void print(String line) {
        String str = line.substring(line.indexOf("(") + 1, line.length() - 1);
        String str_segments[] = str.split("\\+");

        String output = "";

        for (String seg: str_segments) {
            if (seg.charAt(0) == '\"') {
                output += seg.substring(1, seg.length() - 1);
            } else if (seg.startsWith("str")) {
                String var = seg.substring(seg.indexOf("(") + 1, seg.length() - 1);
                output += variables.get(var);
            } else {
                output += variables.get(seg);
            }
        }
        System.out.println(output);
    }

    private static boolean evaluate(String line) {
        boolean result = true;
        String statements[] = line.split("and");
        int x;
        int y;

        for (String statement: statements) {
            if (statement.contains("==")) {
                String[] factors = statement.split("==");
                String xStr = factors[0].strip();
                if (xStr.contains("%")) {
                    String[] vars = xStr.split("%");
                    x = Integer.parseInt(variables.get(vars[0])) % Integer.parseInt(variables.get(vars[1]));
                } else {
                    x = Integer.parseInt(variables.get(xStr));
                }
                y = Integer.parseInt(factors[1].strip());
                result = result && (x == y);
            } else if (statement.contains("!=")) {
                String[] factors = statement.split("!=");
                x = Integer.parseInt(variables.get(factors[0].strip()));
                y = Integer.parseInt(factors[1].strip());
                result = result && (x != y);
            } else if (statement.contains(">=")) {
                String[] factors = statement.split(">=");
                x = Integer.parseInt(variables.get(factors[0].strip()));
                y = Integer.parseInt(factors[1].strip());
                result = result && (x >= y);
            } else if (statement.contains("<=")) {
                String[] factors = statement.split("<=");
                x = Integer.parseInt(variables.get(factors[0].strip()));
                y = Integer.parseInt(factors[1].strip());
                result = result && (x <= y);
            } else if (statement.contains(">")) {
                String[] factors = statement.split(">");
                x = Integer.parseInt(variables.get(factors[0].strip()));
                y = Integer.parseInt(factors[1].strip());
                result = result && (x > y);
            } else if (statement.contains("<")) {
                String[] factors = statement.split("<");
                x = Integer.parseInt(variables.get(factors[0]));
                y = Integer.parseInt(factors[1].strip());
                result = result && (x < y);
            }
        }
        return result;
    }

    private static int numTabs(String line) {
        char charLine[] = line.toCharArray();
        int spaces = 0;
        char temp = charLine[0];

        while (temp == ' ') {
            spaces++;
            temp = charLine[spaces];
        }
        return spaces / 4;
    }

    private static int handleIf(int lineNum) {
        String line;
        String condition;
        int numParentTabs;
        int numTabs;
        int currentLineNum;
        boolean consider;
        boolean alreadyPassed = false;
        boolean loop = true;

        line = fileLines.get(lineNum);
        numParentTabs = numTabs(line);
        condition = line.substring(line.indexOf("if")+3,  line.length()-1);
        condition = condition.replace(")", "");
        condition = condition.replace("(", "");
        consider = evaluate(condition);
        // System.out.println("line: " + line + ", cons: " + consider);

        currentLineNum = lineNum + 1;

        while(loop)  {
            try {
                line = fileLines.get(currentLineNum);
            } catch (Exception e) {
                System.exit(0);
            }
            
            numTabs = numTabs(line);
            // System.out.println("num : " + currentLineNum + ", line: " + line);
            
            if (numTabs == numParentTabs + 1) {
                if (consider) {
                    alreadyPassed = true;
                    currentLineNum = interpretLine(currentLineNum);
                } else {
                    currentLineNum++;
                }
            } else {
                if (line.contains("elif")) {
                    // handle elif
                    if (alreadyPassed) {
                        consider = false;
                    } else {
                        condition = line.substring(line.indexOf("elif")+5,  line.length()-1);
                        condition = condition.replace(")", "");
                        condition = condition.replace("(", "");
                        consider = evaluate(condition);
                    }
                    currentLineNum++;
                } else if (line.contains("else")) {
                    // handle else
                    if (alreadyPassed) {
                        consider = false;
                    } else {
                        consider = true;
                    }
                    currentLineNum++;
                } else {
                    loop = false;
                }
            }
        }
        return currentLineNum;
    }

    private static Integer calculate(String line) {
        // Scanner finder = new Scanner(line);
        // int newValue;
        // while(finder.findInLine("[0-9]*")) {
        //     int firstValue = Integer.parseInt(finder.findInLine("[0-9]*"));
        //     String operator = finder.findInLine("[^0-9]*").trim();
        //     int secondValue = Integer.parseInt(finder.findInLine("[0-9]*"));
        //     switch (operator){
        //         case "+":
        //             newValue = firstValue + secondValue;
        //         case "-":
        //             newValue = firstValue - secondValue;
        //         case "/":
        //             newValue = firstValue / secondValue;
        //         case "*":
        //             newValue = firstValue * secondValue;
        //         case "%":
        //             newValue = firstValue % secondValue;
        //         default:
        //             throw new RuntimeException("unknown operator: "+operator);
        //     }
        //     String newLines = line.split(operator);
        //     String moreNewLines = newLines.split(str(secondValue));
        //     line = moreNewLines[1];
        // }
        // finder.close();
        // return newValue;
        return 5;
    }
}

