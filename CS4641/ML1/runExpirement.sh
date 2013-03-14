export WEKA_HOME="weka-3-7-7"
export CLASSPATH=$WEKA_HOME/weka.jar:$CLASSPATH
export CLASSPATH=$WEKA_HOME/weka-src.jar:$CLASSPATH

export TRAIN=$1
export RESULTS=$2

mkdir $RESULTS
rm $RESULTS/*

echo "    Decision Tree- Subtree Replacement"
java -Xmx512m weka.Run J48 -S -C 0.125 -M 2 -x 10 -t $TRAIN &> $RESULTS/dtree0.txt
java -Xmx512m weka.Run J48 -S -C 0.1875 -M 2 -x 10 -t $TRAIN &> $RESULTS/dtree1.txt
java -Xmx512m weka.Run J48 -S -C 0.25 -M 2 -x 10 -t $TRAIN &> $RESULTS/dtree2.txt
java -Xmx512m weka.Run J48 -S -C 0.375 -M 2 -x 10 -t $TRAIN &> $RESULTS/dtree3.txt
java -Xmx512m weka.Run J48 -S -C 0.50 -M 2 -x 10 -t $TRAIN &> $RESULTS/dtree4.txt
java -Xmx512m weka.Run J48 -U  -M 2 -x 10 -t $TRAIN &> $RESULTS/dtree5.txt

echo "    Neural Network"
java -Xmx512m weka.Run MultilayerPerceptron -L 0.1 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a -x 10 -t $TRAIN &> $RESULTS/nn_1.txt
java -Xmx512m weka.Run MultilayerPerceptron -L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a -x 10 -t $TRAIN &> $RESULTS/nn_3.txt
java -Xmx512m weka.Run MultilayerPerceptron -L 0.5 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a -x 10 -t $TRAIN &> $RESULTS/nn_5.txt

java -Xmx512m weka.Run MultilayerPerceptron -L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a -x 10 -t $TRAIN &> $RESULTS/nn_m_1.txt
java -Xmx512m weka.Run MultilayerPerceptron -L 0.3 -M 0.5 -N 500 -V 0 -S 0 -E 20 -H a -x 10 -t $TRAIN &> $RESULTS/nn_m_3.txt
java -Xmx512m weka.Run MultilayerPerceptron -L 0.3 -M 0.7 -N 500 -V 0 -S 0 -E 20 -H a -x 10 -t $TRAIN &> $RESULTS/nn_m_5.txt


echo "    AdaBoostM1"
java -Xmx512m weka.Run weka.classifiers.meta.AdaBoostM1 -P 100 -S 1 -I 10 -x 10 -t $TRAIN -W weka.classifiers.trees.J48 -- -C 0.125 -M 2 &> $RESULTS/boost0.txt
java -Xmx512m weka.Run weka.classifiers.meta.AdaBoostM1 -P 100 -S 1 -I 10 -x 10 -t $TRAIN -W weka.classifiers.trees.J48 -- -C 0.1875 -M 2 &> $RESULTS/boost1.txt
java -Xmx512m weka.Run weka.classifiers.meta.AdaBoostM1 -P 100 -S 1 -I 10 -x 10 -t $TRAIN -W weka.classifiers.trees.J48 -- -C 0.25 -M 2 &> $RESULTS/boost2.txt
java -Xmx512m weka.Run weka.classifiers.meta.AdaBoostM1 -P 100 -S 1 -I 10 -x 10 -t $TRAIN -W weka.classifiers.trees.J48 -- -C 0.375 -M 2 &> $RESULTS/boost3.txt
java -Xmx512m weka.Run weka.classifiers.meta.AdaBoostM1 -P 100 -S 1 -I 10 -x 10 -t $TRAIN -W weka.classifiers.trees.J48 -- -C 0.50 -M 2 &> $RESULTS/boost4.txt
java -Xmx512m weka.Run weka.classifiers.meta.AdaBoostM1 -P 100 -S 1 -I 10 -x 10 -t $TRAIN -W weka.classifiers.trees.DecisionStump &> $RESULTS/boost_stump.txt

echo "    SVM-PolyKernel"
java -Xmx512m weka.classifiers.functions.SMO -C 00.01 -L 0.0010 -P 1.0E-12 -N 0 -V 10 -W 1 -K "weka.classifiers.functions.supportVector.PolyKernel" -x 10 -t $TRAIN  &> $RESULTS/svm_poly0.txt
java -Xmx512m weka.classifiers.functions.SMO -C 00.10 -L 0.0010 -P 1.0E-12 -N 0 -V 10 -W 1 -K "weka.classifiers.functions.supportVector.PolyKernel" -x 10 -t $TRAIN  &> $RESULTS/svm_poly1.txt
java -Xmx512m weka.classifiers.functions.SMO -C 01.00 -L 0.0010 -P 1.0E-12 -N 0 -V 10 -W 1 -K "weka.classifiers.functions.supportVector.PolyKernel" -x 10 -t $TRAIN  &> $RESULTS/svm_poly2.txt
java -Xmx512m weka.classifiers.functions.SMO -C 10.00 -L 0.0010 -P 1.0E-12 -N 0 -V 10 -W 1 -K "weka.classifiers.functions.supportVector.PolyKernel" -x 10 -t $TRAIN  &> $RESULTS/svm_poly3.txt
java -Xmx512m weka.classifiers.functions.SMO -C 100.0 -L 0.0010 -P 1.0E-12 -N 0 -V 10 -W 1 -K "weka.classifiers.functions.supportVector.PolyKernel" -x 10 -t $TRAIN  &> $RESULTS/svm_poly4.txt

echo "    SVM-RBFKernel"
java -Xmx512m weka.classifiers.functions.SMO -C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V 10 -W 1 -K "weka.classifiers.functions.supportVector.RBFKernel -C 250007 -G 0.01" -x 10 -t $TRAIN &> $RESULTS/svm_rbf0.txt
java -Xmx512m weka.classifiers.functions.SMO -C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V 10 -W 1 -K "weka.classifiers.functions.supportVector.RBFKernel -C 250007 -G 0.1" -x 10 -t $TRAIN &> $RESULTS/svm_rbf1.txt
java -Xmx512m weka.classifiers.functions.SMO -C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V 10 -W 1 -K "weka.classifiers.functions.supportVector.RBFKernel -C 250007 -G 01" -x 10 -t $TRAIN &> $RESULTS/svm_rbf2.txt
java -Xmx512m weka.classifiers.functions.SMO -C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V 10 -W 1 -K "weka.classifiers.functions.supportVector.RBFKernel -C 250007 -G 10" -x 10 -t $TRAIN &> $RESULTS/svm_rbf3.txt
java -Xmx512m weka.classifiers.functions.SMO -C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V 10 -W 1 -K "weka.classifiers.functions.supportVector.RBFKernel -C 250007 -G 100" -x 10 -t $TRAIN &> $RESULTS/svm_rbf4.txt

echo "    kNN"
for ((i=1; i<=10; i++)); do
java -Xmx512m weka.Run IBk -K "$i" -W 0 -A "weka.core.neighboursearch.LinearNNSearch -A \"weka.core.EuclideanDistance -R first-last\"" -x 10 -t $TRAIN &> $RESULTS/knn_k"$i".txt
done
