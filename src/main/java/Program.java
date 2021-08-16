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

        // OLD VERSION, ALL IN ONE, BETTER USE "TrainNN" & "UseNN" instead

        //---------------------------------------
        // MAIN CONTROLS (operate before launch)
        //---------------------------------------

        //------- UNCOMMENT BUT ONE SECTION AND COMMENT OUT THE REST ------

        //------- TRAIN NEW NN FOR QUALITY --------------
        /*
        String inputXLSfile = "C:\\Users\\User\\Documents\\icom0421.xls"; // USER SET, must be valid
        String outputXLSfile = "C:\\Users\\User\\Documents\\icom0421-res02.xls"; // not used here
        NGramStrategy nGramStrategy = new FilteredUnigram(); // atom strategy
        boolean evaluateQuality = true; // true for quality, false for classification
        VocabularyBuilder vocabularyBuilder = new VocabularyBuildStreamAPI(nGramStrategy); // voc strategy
        int freqCount = 0; // entries occurring less than that will be dropped
        String inputVOCfile = "C:\\Users\\User\\Documents\\voc04.txt"; // not used here
        String outputVOCfile = "C:\\Users\\User\\Documents\\voc04.txt"; // USER SET, must be valid
        boolean newNN = true; // true for training, false for usage
        String inputEGfile = "C:\\Users\\User\\Documents\\nn02.eg"; // not used here
        String outputEGfile = "C:\\Users\\User\\Documents\\nn02.eg"; // USER SET, must be valid
        double trainError = 0.001; // max error at which training stops (set too low -- and it never will!)
        NeuralNetworkStrategy nnStrategy = new NNsimpleFFbiased2hidden(); // NN type, use 4out for class
        int OutDataSize = 1; // 1 for Quality, 4 for Classification

         */

        //------- TRAIN NEW NN FOR CLASSIFICATION --------------
        /*
        String inputXLSfile = "C:\\Users\\User\\Documents\\icom0521-1.xls"; // USER SET, must be valid
        String outputXLSfile = "C:\\Users\\User\\Documents\\icom0421-res02.xls"; // not used here
        NGramStrategy nGramStrategy = new FilteredUnigram(); // atom strategy
        boolean evaluateQuality = false; // true for quality, false for classification
        VocabularyBuilder vocabularyBuilder = new VocabularyBuildStreamAPI(nGramStrategy); // voc strategy
        int freqCount = 0; // entries occurring less than that will be dropped
        String inputVOCfile = "C:\\Users\\User\\Documents\\voc04.txt"; // not used here
        String outputVOCfile = "C:\\Users\\User\\Documents\\voc04.txt"; // USER SET, must be valid
        boolean newNN = true; // true for training, false for usage
        String inputEGfile = "C:\\Users\\User\\Documents\\nn02.eg"; // not used here
        String outputEGfile = "C:\\Users\\User\\Documents\\nn02.eg"; // USER SET, must be valid
        double trainError = 0.0011; // max error at which training stops (set too low -- and it never will!)
        NeuralNetworkStrategy nnStrategy = new NNsimpleFFbiased2hidden4out(); // NN type, use 4out for class
        int OutDataSize = 4; // 1 for Quality, 4 for Classification


         */
        //------- USE NN FOR QUALITY --------------
        /*
        String inputXLSfile = "C:\\Users\\User\\Documents\\icom0421.xls"; // USER SET, must be valid
        String outputXLSfile = "C:\\Users\\User\\Documents\\icom0421-res02.xls"; // USER SET, must be valid
        NGramStrategy nGramStrategy = new FilteredUnigram(); // atom strategy (must correspond)
        boolean evaluateQuality = true; // true for quality, false for classification
        VocabularyBuilder vocabularyBuilder = new VocabularyBuildStreamAPI(nGramStrategy); // not used here
        int freqCount = 0; // not used here
        String inputVOCfile = "C:\\Users\\User\\Documents\\voc04.txt"; // USER SET, must be valid
        String outputVOCfile = "C:\\Users\\User\\Documents\\voc04.txt"; // not used here
        boolean newNN = false; // true for training, false for usage
        String inputEGfile = "C:\\Users\\User\\Documents\\nn02.eg"; // USER SET, must be valid
        String outputEGfile = "C:\\Users\\User\\Documents\\nn02.eg"; // not used here
        double trainError = 0.001; // not used here
        NeuralNetworkStrategy nnStrategy = new NNsimpleFFbiased2hidden(); // NN type, use 4out for class
        int OutDataSize = 1; // 1 for Quality, 4 for Classification

         */

        //------- USE NN FOR CLASSIFICATION --------------

        String inputXLSfile = "C:\\Users\\User\\Documents\\icom0521-1.xls"; // USER SET, must be valid
        String outputXLSfile = "C:\\Users\\User\\Documents\\icom0521-res03.xls"; // USER SET, must be valid
        NGramStrategy nGramStrategy = new FilteredUnigram(); // atom strategy (must correspond)
        boolean evaluateQuality = false; // true for quality, false for classification
        VocabularyBuilder vocabularyBuilder = new VocabularyBuildStreamAPI(nGramStrategy); // not used here
        int freqCount = 0; // not used here
        String inputVOCfile = "C:\\Users\\User\\Documents\\voc04.txt"; // USER SET, must be valid
        String outputVOCfile = "C:\\Users\\User\\Documents\\voc04.txt"; // not used here
        boolean newNN = false; // true for training, false for usage
        String inputEGfile = "C:\\Users\\User\\Documents\\nn02.eg"; // USER SET, must be valid
        String outputEGfile = "C:\\Users\\User\\Documents\\nn02.eg"; // not used here
        double trainError = 0.001; // not used here
        NeuralNetworkStrategy nnStrategy = new NNsimpleFFbiased2hidden4out(); // NN type, use 4out for class
        int OutDataSize = 4; // 1 for Quality, 4 for Classification



        //----------------------------------
        // MAIN CONTROLS END
        //----------------------------------

        List<String> vocabulary = new ArrayList<>();

        // LOAD XLS
        List<ClassifiableText> requestList = new ArrayList<>();

        try {
            requestList = FileManager
                    .loadRequestXLS(inputXLSfile);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        // GET VOCABULARY
        if (newNN) {
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
        double[][] NN_IDEAL = new double[requestList.size()][OutDataSize];

        if(evaluateQuality) {
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

        if (newNN) {
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
        else {
            //LOAD PRETRAINED NN
            network = (BasicNetwork) loadObject(
                    new File(inputEGfile));
            //USE THE NN
            double[][] outData = new double[requestList.size()][OutDataSize];
            int i = 0;
            System.out.println("Neural Network Running:");
            for(MLDataPair pair: trainingSet ) {
                final MLData output = network.compute(pair.getInput());
                for(int j=0;j<OutDataSize;j++){
                outData[i][j]=output.getData(j);}
                i++;
            }
            Encog.getInstance().shutdown();

            //STORE & DISPLAY RESULTS
            i = 0;
            for (ClassifiableText request : requestList) {
                if(evaluateQuality){
                request.quality = outData[i][0];
                System.out.println(request.getText() + " quality= " + request.getQuality());
                }
                else {
                    if (outData[i][0] > outData[i][1] && outData[i][0] > outData[i][2]
                    && outData[i][0] > outData[i][3]) {
                        request.classification = "комм";
                    } else if (outData[i][1] > outData[i][0] && outData[i][1] > outData[i][2]
                            && outData[i][1] > outData[i][3]) {
                        request.classification = "инфо";
                    } else if (outData[i][2] > outData[i][1] && outData[i][2] > outData[i][0]
                            && outData[i][2] > outData[i][3]) {
                        request.classification = "бренд";
                    } else {
                        request.classification = "ошибка";
                    }
                    System.out.println(request.getText() + " class= " + request.getClassification());
                }
                i++;

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
