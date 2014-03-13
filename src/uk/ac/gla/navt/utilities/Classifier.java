/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.gla.navt.utilities;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.sf.javaml.classification.KDtreeKNN;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.classification.SOM;
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
        data = FileHandler.loadDataset(new File("/home/mkdeniz/Desktop/format.csv"), 4, ",");
        for (Instance inst : data) {
        	inst.removeAttribute(0);
        	inst.removeAttribute(1);
        }
        NB = new NaiveBayesClassifier(true,true,false);
        NB.buildClassifier(data);
        KNN = new KNearestNeighbors(2);
        KNN.buildClassifier(data);
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
        for (int k = 1; k < 11; k++ ) {
            KNN = new KNearestNeighbors(k);
            CrossValidation cv = new CrossValidation(KNN);
            Map<Object, PerformanceMeasure> CV = cv.crossValidation(data);
            double d = 0;
            for ( Object o :CV.keySet())
                d = d + (CV.get(o).getAccuracy()*100);
            double acc = d/CV.size();
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
	return d/CV.size();
    }
    
    public static void main(String[] args) throws IOException {
        Classifier c = new Classifier();
        System.out.println("Accuracy of NB: " + c.CrossValidationNB());
        HashMap<Integer,Double> result = c.CrossValidationKNN();
        System.out.println("Accuracy of KNN: ");
        for (Integer key : result.keySet()) {
            System.out.print("K="+key+" : "+result.get(key));
        }
    }
    
}
