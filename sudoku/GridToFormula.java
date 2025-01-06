import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

public class GridToFormula {
    /**
     * Reads a Sudoku grid from the given input stream.
     *
     * @param input the input stream to read the grid from
     * @return a 9x9 array representing the grid
     */
    public static int[][] readGrid(InputStream input) {
        Scanner scanner = new Scanner(input);
        int[][] grid = new int[9][9];
        int i = 0;
        int j = 0;
        int tmp = 0;
        while (scanner.hasNextLine()) {
            String NextLine = scanner.nextLine();
            while (i != 9) {
                char c = NextLine.charAt(i);
                if (c == '.') {
                    tmp =0;
                }
                else {
                    String s = String.valueOf(c);
                    tmp = Integer.parseInt(s);
                }
                grid[j][i] = tmp;
                i++;
            }
            i = 0;
            j++;
        }
        scanner.close();
        return grid;
    }

    /**
     * Writes a CNF formula to the given output stream.
     *
     * @param formula the CNF formula to write
     * @param output the output stream to write the formula to
     * @throws IOException if an I/O error occurs
     */
    public static void writeFormula(ArrayList<ArrayList<Integer>> formula, OutputStreamWriter output) throws IOException {
        BufferedWriter writer = new BufferedWriter(output);
        int taille = formula.size();
        for (int i = 0; i < taille; i++) {
            int taille2 = formula.get(i).size();
            for (int j = 0; j < taille2; j++) {
                writer.write(formula.get(i).get(j) + " ");
            }
            writer.write(" 0");
            writer.newLine();
            writer.flush();
        }
        writer.close();
    }

    /**
     * Generates a CNF formula that represents the given Sudoku grid.
     *
     * @param sudoku the Sudoku grid to represent
     * @return a CNF formula representing the grid
     */
    public static ArrayList<ArrayList<Integer>> generateFormula(int[][] sudoku) {
        ArrayList<ArrayList<Integer>> formula = new ArrayList<ArrayList<Integer>>();
        int tmp, tmp1;
        ArrayList<Integer> clauses = new ArrayList<Integer>();

        // Each cell has at least one value
        for (int i=1; i<10; i++) {
            for (int j=1; j<10; j++) {
                clauses = new ArrayList<Integer>();
                for (int k=1; k<10; k++) {
                    tmp = i*100 + j*10 + k;
                    clauses.add(tmp);
                }
                formula.add(clauses);
            }
        }
        // Each cell has no more than one value
        for (int i=1; i<10; i++) {
            for (int j=1; j<10; j++) {
                for (int k=1; k<10; k++) {
                    for (int l=k+1; l<10; l++) {
                        clauses = new ArrayList<Integer>();
                        tmp = i*100 + j*10 + k;
                        tmp1 = i*100 + j*10 + l;
                        tmp = -tmp;
                        tmp1 = -tmp1;
                        clauses.add(tmp);
                        clauses.add(tmp1);
                        formula.add(clauses);
                    }
                }
            }
        }

        // Filled cells remain at the initial value
        for (int i=0; i<9; i++) {
            for (int j=0; j<9; j++) {
                if (sudoku[i][j] != 0) {
                    clauses = new ArrayList<Integer>();
                    tmp = (i+1)*100 + (j+1)*10 + sudoku[i][j];
                    clauses.add(tmp);
                    formula.add(clauses);
                }
            }
        }

        // Each row has a value from 1 to 9
        for (int i=1; i<10; i++) {
            for (int k = 1; k < 10; k++) {
                clauses = new ArrayList<Integer>();
                for (int j = 1; j < 10; j++) {
                    tmp = i * 100 + j * 10 + k;
                    clauses.add(tmp);
                }
                formula.add(clauses);
            }
        }

        // Each column has a value from 1 to 9
        for (int i=1; i<10; i++) {
            for (int k=1; k<10; k++) {
                clauses = new ArrayList<Integer>();
                for (int j=1; j<10; j++) {
                    tmp = j*100 + i*10 + k;
                    clauses.add(tmp);
                }
                formula.add(clauses);
            }
        }

        // No duplicates in the regions
        for (int i=0; i<3; i++) {
            for (int j=0; j<3; j++) {
                for (int k=1; k<10; k++) {
                    clauses = new ArrayList<Integer>();
                    for (int l=0; l<3; l++) {
                        for (int m=0; m<3; m++) {
                            tmp = (3 * i + l + 1) * 100 + (3 * j + m + 1) * 10 + k;
                            clauses.add(tmp);
                        }
                    }
                    formula.add(clauses);
                }
            }
        }

        return formula;
    }

    public static void main(String[] args) {
        if (args.length != 1 && args.length != 2) {
            System.out.println("Usage: java GridToFormula <input-file> [output-file]");
            System.exit(1);
        }

        try {
            InputStream inputStream = new FileInputStream(args[0]);
            int[][] grid = readGrid(inputStream);
            inputStream.close();

            ArrayList<ArrayList<Integer>> formule = generateFormula(grid);

            OutputStreamWriter outputStreamWriter = args.length == 2 ? new OutputStreamWriter(new FileOutputStream(args[1])) : new OutputStreamWriter(System.out);
            writeFormula(formule, outputStreamWriter);
            outputStreamWriter.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}

