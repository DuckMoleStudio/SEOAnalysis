import org.encog.neural.networks.BasicNetwork;

public interface NeuralNetworkStrategy {
        BasicNetwork createNetwork(int inputSize);
    }
