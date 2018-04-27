import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is an example program to demonstrate the structure of the
 * PageRank algorithm by Larry Page.
 *
 * @author Severin Kaderli
 * @see <a href="https://ilpubs.stanford.edu:8090/361/1/1998-8.pdf">https://ilpubs.stanford.edu:8090/361/1/1998-8.pdf</a>
 * @see <a href="https://www.cs.princeton.edu/~chazelle/courses/BIB/pagerank.htm">https://www.cs.princeton.edu/~chazelle/courses/BIB/pagerank.htm</a>
 **/
public class Main {
    private static final int NUMBER_OF_ITERATIONS = 20;

    private static List<Page> pages;

    public static void main(String[] args) {
        preparePages();
        calculateSimplePageRank();
        calculateRandomSurferModelPageRank();
    }

    private static void preparePages() {
        // Add some pages
        Page a = new Page("A");
        Page b = new Page("B");
        Page c = new Page("C");
        Page d = new Page("D");
        pages = new ArrayList<>(Arrays.asList(a, b, c, d));

        // Add links between the pages
        a.addLink(b);
        a.addLink(c);
        a.addLink(c);
        b.addLink(c);
        c.addLink(a);
        c.addLink(d);
        d.addLink(c);
    }

    private static void calculateRandomSurferModelPageRank() {
        int n = pages.size();
        double[][] linkCounts = new double[n][n];
        double[] numberOfLinks = new double[n];

        for (int row = 0; row < n; row++) {
            numberOfLinks[row] = pages.get(row).getNumberOflinks();
            for (int col = 0; col < n; col++) {
                linkCounts[row][col] = pages.get(row).numberOfLinksToPage(pages.get(col));
            }
        }
        printMatrix("Link Counts", linkCounts);


        double[][] leapProbability = new double[n][n];
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                leapProbability[row][col] = 0.1 / n;
            }
        }
        printMatrix("Leap Probability", leapProbability);


        double[][] probabilityMatrix = new double[n][n];
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                probabilityMatrix[row][col] = 0.9 * (linkCounts[row][col] / numberOfLinks[row]);
            }
        }
        printMatrix("Probability Matrix", probabilityMatrix);

        double[][] transitionMatrix = new double[n][n];
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                transitionMatrix[row][col] = leapProbability[row][col] + probabilityMatrix[row][col];
            }
        }
        printMatrix("Transition matrix", transitionMatrix);

        // Start on random page
        int currentPageIndex = (int) Math.floor(Math.random() * n);
        int[] frequencies = new int[n];


        int trials = 100000;
        for (int i = 0; i < trials; i++) {
            double randomMove = Math.random();
            double sum = 0;


            for (int targetPage = 0; targetPage < n; targetPage++) {
                sum += transitionMatrix[currentPageIndex][targetPage];
                if (randomMove < sum) {
                    currentPageIndex = targetPage;
                    break;
                }
            }

            frequencies[currentPageIndex]++;
        }

        for (int i = 0; i < n; i++) {
            System.out.printf("PR(%s) = %.3f\n", pages.get(i).getTitle(), (double) frequencies[i] / trials);
        }


    }

    private static void printMatrix(String title, double[][] matrix) {
        System.out.println(title.toUpperCase());
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                if (col > 0) {
                    System.out.print(", ");
                }

                DecimalFormat formatter = new DecimalFormat("###.####");
                System.out.print(formatter.format(matrix[row][col]));

            }
            System.out.println();
        }
        System.out.println();
    }

    private static void calculateSimplePageRank() {


        // Simulate the PageRank calculation
        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
            System.out.println("Iteration NR " + (i + 1));
            double pageRankSum = 0;
            for (Page page : pages) {
                page.calculatePageRank();
                pageRankSum += page.getPageRank();
            }

            System.out.printf("Sum of the PageRanks is: %.2f\n\n", pageRankSum);
        }
    }
}
