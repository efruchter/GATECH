package efruchter.project3;



import shared.DataSet;
import shared.Instance;
import shared.filt.IndependentComponentAnalysis;
import shared.filt.LinearDiscriminantAnalysis;

/**
 * ML3 file.
 *
 * @author toriscope
 */
public class IrisLDA {

    public static void main(String[] args) {
        Instance[] instances =  DataHard.IRIS_LDA;

        DataSet set = new DataSet(instances);
        System.out.println("Before LDA");
        System.out.println(set);
        LinearDiscriminantAnalysis filter = new LinearDiscriminantAnalysis(set);
        filter.filter(set);
        System.out.println(filter.getProjection());
        System.out.println("After LDA");
        System.out.println(set);
        filter.reverse(set);
        System.out.println("After reconstructing");
        System.out.println(set);
    }

}
