import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

public class NNsimpleFFbiased2hidden implements NeuralNetworkStrategy {
    @Override
    public BasicNetwork createNetwork(int inputSize){
        BasicNetwork network = new BasicNetwork();

        network.addLayer(new BasicLayer(null,true,inputSize));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true, inputSize/2));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true, inputSize/4));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),false,1));
        network.getStructure().finalizeStructure();
        network.reset();

        return network;
    }
}
