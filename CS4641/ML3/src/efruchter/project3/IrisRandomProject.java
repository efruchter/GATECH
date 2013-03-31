package efruchter.project3;

import shared.DataSet;
import shared.DataSetDescription;
import shared.Instance;
import shared.filt.RandomizedProjectionFilter;
import util.linalg.Matrix;

public class IrisRandomProject {

    public static void main(String[] args) {

       Instance[] instances = DataHard.IRIS;

        DataSet set = new DataSet(instances);
        System.out.println("Before RA");
        System.out.println(set);

        // out, in
        // for iris, try 4 to 4 first
        RandomizedProjectionFilter filter = new RandomizedProjectionFilter(2, 4);
        System.out.println(filter.getProjection().transpose());
        filter.filter(set);

        System.out.println("After RA");
        System.out.println(set);

        Matrix reverse = filter.getProjection().transpose();
        for (int i = 0; i < set.size(); i++) {
            Instance instance = set.get(i);
            instance.setData(reverse.times(instance.getData()));
        }
        System.out.println("After reconstructing");
        System.out.println(set);
    }

}
