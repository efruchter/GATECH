package efruchter.project3;



import shared.DataSet;
import shared.Instance;
import shared.filt.IndependentComponentAnalysis;
import shared.filt.PrincipalComponentAnalysis;
import util.linalg.Matrix;

/**
 * ML3 file.
 *
 * @author toriscope
 */
public class IrisICA {

    public static void main(String[] args) {
        Instance[] instances =  DataHard.IRIS;

        DataSet set = new DataSet(instances);

        System.out.println("Before ICA");
        System.out.println(set);

        int components = 3;

        IndependentComponentAnalysis filter = new IndependentComponentAnalysis(set, components);
        filter.filter(set);

        System.out.println("After ICA");
        System.out.println(set);
    }

}
