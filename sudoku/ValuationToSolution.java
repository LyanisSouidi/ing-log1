import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ValuationToSolution {
    /**
     * Reads a valuation from the given input stream.
     *
     * @param input the input stream to read the valuation from
     * @return a list of strings representing the valuation
     */
    public static List<String> readValuation(InputStream input) {
        Scanner scanner = new Scanner(input);

        String status = scanner.nextLine();
        if (status.equals("UNSAT")) {
            System.out.println("No solution found");
            System.exit(1);
        } else if (!status.equals("SAT")) {
            System.err.println("Invalid status: the first line of the input file should be either 'SAT' or 'UNSAT'");
            System.exit(1);
        }

        String[] str_valuations = scanner.nextLine().split(" ");
        scanner.close();
        List<String> int_valuations = new ArrayList<>();
        try {
            int int_valuation;
            for (String str_valuation : str_valuations) {
                int_valuation = Integer.parseInt(str_valuation);
                if (int_valuation > 0) int_valuations.add(String.valueOf(int_valuation));
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid valuation: " + e.getMessage());
            System.exit(1);
        }

        return int_valuations;
    }

    /**
     * Converts a valuation to a string representation of the solution.
     *
     * @param valuations the valuation to convert
     * @param raw whether to output the solution in raw or pretty format
     * @return a string representation of the solution
     */
    public static String solutionToString(List<String> valuations, boolean raw) {
        int[][] grid = new int[9][9];
        try {
            for (String valuation : valuations) {
                String[] split = valuation.split("");
                int i = Integer.parseInt(split[0]) - 1;
                int j = Integer.parseInt(split[1]) - 1;
                int value = Integer.parseInt(split[2]);
                grid[i][j] = value;
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Invalid valuation: " + e.getMessage());
            System.exit(1);
        }
        String[] rows;
        if (raw) {
            rows = new String[9];
        } else {
            rows = new String[13];
            rows[0]  = "┌───────┬───────┬───────┐";
            rows[4]  = "├───────┼───────┼───────┤";
            rows[8]  = "├───────┼───────┼───────┤";
            rows[12] = "└───────┴───────┴───────┘";
        }

        for (int i = 0; i < 9; i++) {
            int row_index = i + (raw ? 0 : i > 5 ? 3 : i > 2 ? 2 : 1);
            rows[row_index] = raw ? "" : "│";
            for (int j = 0; j < 9; j++) {
                if (!raw) rows[row_index] += " ";
                rows[row_index] += String.valueOf(grid[i][j]);
                if (!raw && j % 3 == 2) rows[row_index] += " │";
            }
        }

        String result = "";
        for (int i = 0; i < rows.length; i++) {
            result += rows[i];
            if (i < rows.length - 1) result += "\n";
        }

        return result;
    }

    public static void main(String[] args) {
        if (args.length != 1 && args.length != 2) {
            System.out.println("Usage: java ValuationToSolution [--raw] <input-file>");
            System.exit(1);
        }

        String filename = args[0];
        boolean raw = false;

        if (args.length == 2) {
            if (args[0].equals("--raw")) {
                filename = args[1];
                raw = true;
            } else if (args[1].equals("--raw")) {
                raw = true;
            } else {
                System.out.println("Usage: java ValuationToSolution [--raw] <input-file>");
                System.exit(1);
            }
        }

        try {
            InputStream inputStream = new FileInputStream(filename);
            List<String> valuations = readValuation(inputStream);
            inputStream.close();

            System.out.println(solutionToString(valuations, raw));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}

