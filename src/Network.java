public class Network {
	
	private int classLabelCount;
	private int hiddenLayerCount;	
	private int hiddenLayerUnitCount;	

	private Layer[] hiddenLayers;
	private Layer outputLayer;
	
	private final double trainingRate = 0.05;
	private final int maxEpoch = 64;
		
	public Network(int classLabelCount, int inputCount,  int hiddenLayerCount, int hiddenLayerUnitCount) {
		this.classLabelCount = classLabelCount;
		this.hiddenLayerCount = hiddenLayerCount;
		this.hiddenLayerUnitCount = hiddenLayerUnitCount;
		
		hiddenLayers = new Layer[hiddenLayerCount];
		hiddenLayers[0] = new Layer(hiddenLayerUnitCount, inputCount);
			//this layer accepts the original data as inputs
		
		for(int i = 1; i < hiddenLayerCount; i++) {
			hiddenLayers[i] = new Layer(hiddenLayerUnitCount, hiddenLayerUnitCount);
		}
		
		outputLayer = new Layer(classLabelCount, hiddenLayerUnitCount);
	}
	
	//Each training row should have input values, and then target (class label) values.
	void trainNetwork(double[][] trainingData) {
		boolean doneWithTraining = false;
		
		int currentEpoch = 1;
		while(!doneWithTraining) {
						
			if(currentEpoch == maxEpoch) {
				doneWithTraining = true;
			}
			
			for(int i = 0; i < trainingData.length; i++) {
				
				networkOutput(trainingData[i]);
							
				double[] outputError = outputLayerError(trainingData[i]);				
				outputLayer.setLastError(outputError);
				
				for(int j = hiddenLayerCount - 1; j >= 0; j--) {
					double[] hiddenError = hiddenLayerError(trainingData[i], j);
					hiddenLayers[j].setLastError(hiddenError);
				}
				
				outputLayer.updateWeights(trainingRate);
				
				
				for(int j = hiddenLayerCount - 1; j >= 0; j--) {
					hiddenLayers[j].updateWeights(trainingRate);
				}							 
				
			}
			
			System.out.println("Done with epoch " + currentEpoch);
			currentEpoch++;
		}
		
	}
	
	
	double[] output() {
		double[] result = new double[classLabelCount];		
		return result;
	}
	
	private double[] outputLayerError(double[] instance) {
		double[] result = new double[classLabelCount];
		double[] lastOutput = outputLayer.lastOutput;
		
		for(int i = 0; i < classLabelCount; i++) {
			int index = (i + instance.length) - classLabelCount;
			double target = instance[index];			
			
			result[i] = lastOutput[i] * (1 - lastOutput[i]) * (target - lastOutput[i]);			
		}		
		return result;
	}
	
	private double[] hiddenLayerError(double[] instance, int layerIndex) {
		double[] result = new double[hiddenLayerUnitCount];
		double[] lastOutput = hiddenLayers[layerIndex].lastOutput;
		Layer nextLayer;
		
		if(layerIndex == hiddenLayerCount - 1) {
			//use the output error
			nextLayer = outputLayer;
			
		} else {
			//use the error of the next hidden layer			
			nextLayer = hiddenLayers[layerIndex + 1];
		}
		
		for(int i = 0; i < hiddenLayerUnitCount; i++) {
			double sum = 0;
			double[] nextLayerError = nextLayer.getLastError();
			
			for(int k = 0; k < nextLayerError.length; k++) {
				double weight = nextLayer.units[k].unitWeights[i];
				sum += (nextLayerError[k] * weight);
			}
			
			result[i] = lastOutput[i] * (1 - lastOutput[i]) * sum;			
		}
		
		return result;
	}
	
	public double[] networkOutput(double[] instance) {

		double currentOutputData[] = hiddenLayers[0].output(instance);
			//this is the output from the first hiddenLayer, which is done separately
			//since it takes in a larger number of inputs
		
		for(int j = 1; j < hiddenLayers.length; j++) {
			currentOutputData = hiddenLayers[j].output(currentOutputData);
		}
					
		double[] result = outputLayer.output(currentOutputData);
		
		return result;
	}
	
}
