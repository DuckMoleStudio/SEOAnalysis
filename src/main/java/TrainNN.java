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


import static org.encog.persist.EncogDirectoryPersistence.saveObject;

public class TrainNN {

    public static void main(String[] args) {

        //---------------------------------------
        // MAIN CONTROLS (check & operate before launch, filenames at least!)
        //---------------------------------------


        String inputXLSfile = "C:\\Users\\User\\Documents\\icom0421.xls"; // USER SET, must be valid
        NGramStrategy nGramStrategy = new FilteredUnigram(); // atom strategy
        VocabularyBuilder vocabularyBuilder = new VocabularyBuildStreamAPI(nGramStrategy); // voc strategy
        int freqCount = 0; // entries occurring less than that will be dropped
        String outputVOCfile = "C:\\Users\\User\\Documents\\voc04.txt"; // USER SET
        String outputEGfile = "C:\\Users\\User\\Documents\\nn02.eg"; // USER SET
        double trainError = 0.001; // max error at which training stops (set too low -- and it never will!)
        NeuralNetworkStrategy nnStrategy = new NNsimpleFFbiased2hidden(); // NN type, 4out for class
        int OutDataSize = 1; // 1 for Quality, 4 for Classification

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
        List<String> vocabulary = vocabularyBuilder.createVocabulary(requestList, freqCount);
            FileManager.saveVocabulary(outputVOCfile, vocabulary);


        // PREPARE NN INPUT DATA
        BasicNetwork network;
        double[][] NN_INPUT = new double[requestList.size()][vocabulary.size()];
        double[][] NN_IDEAL = new double[requestList.size()][OutDataSize];

        if(OutDataSize == 1) {
            int i = 0;
            for (ClassifiableText request : requestList) {
                NN_INPUT[i] = Vector.getTextAsVector(request, vocabulary, nGramStrategy);
                NN_IDEAL[i][0] = request.getQuality();
                i++;
            }
        } else {
            int i = 0;
            for (ClassifiableText request : requestList) {
                NN_INPUT[i] = Vector.getTextAsVector(request, vocabulary, nGramStrategy);
                switch (request.getClassification()){
                    case "комм":
                        NN_IDEAL[i][0] = 1;
                        NN_IDEAL[i][1] = 0;
                        NN_IDEAL[i][2] = 0;
                        NN_IDEAL[i][3] = 0;
                        break;
                    case "инфо":
                        NN_IDEAL[i][0] = 0;
                        NN_IDEAL[i][1] = 1;
                        NN_IDEAL[i][2] = 0;
                        NN_IDEAL[i][3] = 0;
                        break;
                    case "бренд":
                        NN_IDEAL[i][0] = 0;
                        NN_IDEAL[i][1] = 0;
                        NN_IDEAL[i][2] = 1;
                        NN_IDEAL[i][3] = 0;
                        break;
                    default:
                        NN_IDEAL[i][0] = 0;
                        NN_IDEAL[i][1] = 0;
                        NN_IDEAL[i][2] = 0;
                        NN_IDEAL[i][3] = 1;
                }
                i++;
            }
        }


        // create training data
        MLDataSet trainingSet = new BasicMLDataSet(NN_INPUT, NN_IDEAL);

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

                String ss = "actual=";
                for(int k=0; k<OutDataSize; k++){
                    ss+=" ";
                    ss+= output.getData(k);
                }
                ss+=" ideal=";
                for(int k=0; k<OutDataSize; k++){
                    ss+=" ";
                    ss+= pair.getIdeal().getData(k);
                }

                System.out.println(ss);
            }

            // SAVE TRAINED NN
            saveObject(new File(outputEGfile), network);

            Encog.getInstance().shutdown();

        }
    }
