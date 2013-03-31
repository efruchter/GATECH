package efruchter.project3;

import java.util.*;
import java.io.*;

/**
 * Calculates root mean square error for two sets of data
 */
public class CalculateRMSE {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        try {
            BufferedReader br1 = new BufferedReader(new FileReader(new File("src/efruchter/project3/a.txt")));
            BufferedReader br2 = new BufferedReader(new FileReader(new File("src/efruchter/project3/b.txt")));

            System.out.println("Enter the number of instances in this data set>>");
            int numInstances = scan.nextInt();

            double total = 0;
            int counter = 0;
            for(int i = 0; i < numInstances; i++) {
                Scanner scan1 = new Scanner(br1.readLine());
                Scanner scan2 = new Scanner(br2.readLine());

                scan1.useDelimiter(",");
                scan2.useDelimiter(",");

                double a, b;

                do {
                    a = Double.parseDouble(scan1.next());
                    b = Double.parseDouble(scan2.next());

                    total += Math.pow(Math.abs(a-b),2);
                    counter++;
                } while(scan1.hasNext() && scan2.hasNext());
            }

            total /= counter;

            System.out.println("Root Mean Squared Error: " + total);
        }
        catch(Exception e) {
            System.out.println("Enter the attribute data from before and after PCA / RA into a.txt and b.txt.");
            e.printStackTrace();
        }

    }
}
