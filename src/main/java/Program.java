import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.encog.Encog;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import static org.encog.persist.EncogDirectoryPersistence.loadObject;
import static org.encog.persist.EncogDirectoryPersistence.saveObject;

public class Program {

    public static void main(String[] args) {

        //---------------------------------------
        // MAIN CONTROLS (operate before launch)
        //---------------------------------------
        String inputXLSfile = "C:\\Users\\User\\Documents\\icom0521-1.xls";
        String outputXLSfile = "C:\\Users\\User\\Documents\\icom0521-res01.xls";

        NGramStrategy nGramStrategy = new FilteredUnigram();

        boolean newVocabulary = false; // if true, new NN will also launch automatically
        List<String> vocabulary = new ArrayList<>();
        VocabularyBuilder vocabularyBuilder = new VocabularyBuildStreamAPI(nGramStrategy);
        int freqCount = 0; // entries occurring less than that will be dropped
        String inputVOCfile = "C:\\Users\\User\\Documents\\voc04.txt";
        String outputVOCfile = "C:\\Users\\User\\Documents\\voc04.txt";

        boolean newNN = false; // will be considered only with existing vocabulary
        String inputEGfile = "C:\\Users\\User\\Documents\\nn01.eg";
        String outputEGfile = "C:\\Users\\User\\Documents\\nn02.eg";
        double trainError = 0.001; // max error at which training stops (set too low -- and it never will!)
        NeuralNetworkStrategy nnStrategy = new NNsimpleFFbiased2hidden();
        //----------------------------------
        // MAIN CONTROLS END
        //----------------------------------

        // LOAD XLS
        List<ClassifiableText> requestList = new ArrayList<>();

        try {
            requestList = FileManager
                    .loadRequestXLS(inputXLSfile);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        // GET VOCABULARY
        if (newVocabulary) {
            vocabulary = vocabularyBuilder.createVocabulary(requestList, freqCount);
            FileManager.saveVocabulary(outputVOCfile, vocabulary);
        } else {
            try {
                vocabulary = FileManager.loadVocabulary(inputVOCfile);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }

        // PREPARE NN INPUT DATA
        BasicNetwork network;

        double[][] NN_INPUT = new double[requestList.size()][vocabulary.size()];
        double[][] NN_IDEAL = new double[requestList.size()][1];
        int i = 0;
        for (ClassifiableText request : requestList) {
            NN_INPUT[i] = Vector.getTextAsVector(request, vocabulary, nGramStrategy);
            NN_IDEAL[i][0] = request.getQuality();
            i++;
        }
        // create training data
        MLDataSet trainingSet = new BasicMLDataSet(NN_INPUT, NN_IDEAL);

        if (newNN || newVocabulary) {
            // CREATE NEW NN
            network = nnStrategy.createNetwork(vocabulary.size());

            // TRAIN THE NN
            final ResilientPropagation train = new ResilientPropagation(network, trainingSet);

            int epoch = 1;
            do {
                train.iteration();
                System.out.println("Epoch #" + epoch + " Error:" + train.getError());
                epoch++;
            } while(train.getError() > trainError);
            train.finishTraining();

            //TEST THE NN
            System.out.println("Neural Network Test Results:");
            for(MLDataPair pair: trainingSet ) {
                final MLData output = network.compute(pair.getInput());
                System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1)
                        + ", actual=" + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
            }

            // SAVE TRAINED NN
            saveObject(new File(outputEGfile), network);

            Encog.getInstance().shutdown();


        }
        else {
            //LOAD PRETRAINED NN
            network = (BasicNetwork) loadObject(
                    new File(inputEGfile));
            //USE THE NN
            double[] outData = new double[requestList.size()];
            i = 0;
            System.out.println("Neural Network Running:");
            for(MLDataPair pair: trainingSet ) {
                final MLData output = network.compute(pair.getInput());
                outData[i]=output.getData(0);
                i++;
            }
            Encog.getInstance().shutdown();

            //STORE & DISPLAY RESULTS
            i = 0;
            for (ClassifiableText request : requestList) {
                request.quality = outData[i];
                i++;
                System.out.println(request.getText() + " quality= " + request.getQuality());
            }

            // WRITE XLS FILE
            try {
                FileManager.saveRequestXLS(outputXLSfile, requestList);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
