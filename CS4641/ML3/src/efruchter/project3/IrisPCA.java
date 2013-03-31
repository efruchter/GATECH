package efruchter.project3;



import shared.DataSet;
import shared.Instance;
import shared.filt.PrincipalComponentAnalysis;
import util.linalg.Matrix;

/**
 * ML3 file.
 *
 * @author toriscope
 */
public class IrisPCA {

    public static void main(String[] args) {
        Instance[] instances =  DataHard.IRIS;
        DataSet set = new DataSet(instances);
        System.out.println("Before PCA");
        System.out.println(set);
        PrincipalComponentAnalysis filter = new PrincipalComponentAnalysis(set);
        System.out.println(filter.getEigenValues());
        System.out.println(filter.getProjection().transpose());
        filter.filter(set);
        System.out.println("After PCA");
        System.out.println(set);
        Matrix reverse = filter.getProjection().transpose();
        for (int i = 0; i < set.size(); i++) {
            Instance instance = set.get(i);
            instance.setData(reverse.times(instance.getData()).plus(filter.getMean()));
        }
        System.out.println("After reconstructing");
        System.out.println(set);
    }

}
