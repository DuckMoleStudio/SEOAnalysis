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


import static org.encog.persist.EncogDirectoryPersistence.loadObject;


public class UseNN {

    public static void main(String[] args) {

        //---------------------------------------
        // MAIN CONTROLS (operate before launch) (check & operate before launch, filenames at least!)
        //---------------------------------------

        String inputXLSfile = "C:\\Users\\User\\Documents\\icom0421.xls"; // USER SET, must be valid
        String outputXLSfile = "C:\\Users\\User\\Documents\\icom0421-res02.xls"; // USER SET
        NGramStrategy nGramStrategy = new FilteredUnigram(); // atom strategy (must correspond)
        String inputVOCfile = "C:\\Users\\User\\Documents\\voc04.txt"; // USER SET, must be valid
        String inputEGfile = "C:\\Users\\User\\Documents\\nn02.eg"; // USER SET, must be valid
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

        List<String> vocabulary = new ArrayList<>();

            try {
                vocabulary = FileManager.loadVocabulary(inputVOCfile);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }


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
                if(OutDataSize == 1){
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
