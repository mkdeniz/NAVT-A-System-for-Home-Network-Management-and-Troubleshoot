package uk.ac.gla.navt.process;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.sf.javaml.classification.KDtreeKNN;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.classification.bayes.NaiveBayesClassifier;
import net.sf.javaml.classification.evaluation.CrossValidation;
import net.sf.javaml.classification.evaluation.PerformanceMeasure;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.FileHandler;


/**
 *
 * @author root
 */
public class Classifier{
    
    Dataset data;
    NaiveBayesClassifier NB;
    KNearestNeighbors KNN;
    KDtreeKNN tKNN;
            
    public Classifier() throws IOException {
        data = FileHandler.loadDataset(new File("/home/mkdeniz/Desktop/formatted_flows.csv"), 4, ",");
        for (Instance inst : data) {
        	inst.removeAttribute(0);
        	inst.removeAttribute(1);
        }
        NB = new NaiveBayesClassifier(true,true,false);
        long start = System.currentTimeMillis();
        NB.buildClassifier(data);
        long elapsedTime = System.currentTimeMillis() - start;
        System.out.println("\n"+elapsedTime);
        KNN = new KNearestNeighbors(2);
        start = System.currentTimeMillis();
        KNN.buildClassifier(data);
        elapsedTime = System.currentTimeMillis() - start;
        System.out.println("\n"+elapsedTime);
        tKNN = new KDtreeKNN(2);
    }
    
    public String KNNClassify(double m, double v) {
        double [] d = new double[2];
        System.out.print(m);
        System.out.print(v);
        d[0] = m;
        d[1] = v;
        Instance ins = new DenseInstance(d);
        Object predictedNB = KNN.classify(ins);
        String results;
        if (predictedNB.toString() != null)
            return predictedNB.toString();
        else
            return null;
    }
    
    public String tKNNClassify(double m, double v) {
        double [] d = new double[2];
        System.out.print(m);
        System.out.print(v);
        d[0] = m;
        d[1] = v;
        Instance ins = new DenseInstance(d);
        Object predictedNB = tKNN.classify(ins);
        return predictedNB.toString();
    }
    
    public String NBClassify(double m, double v) {
        double [] d = new double[2];
        System.out.print(m);
        System.out.print(v);
        d[0] = m;
        d[1] = v;
        Instance ins = new DenseInstance(d);
        Object predictedNB = NB.classify(ins);
        return predictedNB.toString();
    }
    
    public HashMap<Integer, Double> CrossValidationKNN() {
        HashMap<Integer,Double> hm = new HashMap<>();
        for (int k = 3; k < 5; k++ ) {
            KNN = new KNearestNeighbors(k);
            CrossValidation cv = new CrossValidation(KNN);
            long start = System.currentTimeMillis();
            Map<Object, PerformanceMeasure> CV = cv.crossValidation(data);
            long elapsedTime = System.currentTimeMillis() - start;
            double d = 0;
            
            for ( Object o :CV.keySet())
                d = d + (CV.get(o).getAccuracy()*100);
            double acc = d/CV.size();
            
            System.out.println("\n"+elapsedTime);
            hm.put(k, acc);
        }
        return hm;
    }
    
    public double CrossValidationNB() {
        CrossValidation cv = new CrossValidation(NB);
        Map<Object, PerformanceMeasure> CV = cv.crossValidation(data);
	double d = 0;
	for ( Object o :CV.keySet())
            d = d + (CV.get(o).getAccuracy()*100);
        System.out.print(CV);
	return d/CV.size();
    }
    
    public static void main(String[] args) throws IOException {
        Classifier c = new Classifier();
        long start = System.currentTimeMillis();
        System.out.println("Accuracy of NB: " + c.CrossValidationNB());
        long elapsedTime = System.currentTimeMillis() - start;
        System.out.println("\n"+elapsedTime);
        HashMap<Integer,Double> result = c.CrossValidationKNN();
        System.out.println("Accuracy of KNN: ");
        for (Integer key : result.keySet()) {
            start = System.currentTimeMillis();
            System.out.print("K="+key+" : "+result.get(key));
            elapsedTime = System.currentTimeMillis() - start;
            System.out.print(elapsedTime);
        }
    }
    
}
