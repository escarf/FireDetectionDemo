package FireDetectionDemo;

public class SigmoidUnit {
	public static final double WEIGHT_UPPER_BOUND = 0.01;
	public static final double WEIGHT_LOWER_BOUND = -1 * WEIGHT_UPPER_BOUND;
	double[] unitWeights;
	private double lastOutput;
	
	//Note: inputCount includes the 'constant' input value of 1
	//(this value is eventually multiplied with unitWeights[0])
	public SigmoidUnit(int inputCount) {		
		unitWeights = new double[inputCount];
		for(int i = 0; i < inputCount; i++) {
			unitWeights[i] = WEIGHT_LOWER_BOUND + (Math.random() * WEIGHT_UPPER_BOUND * 2);
		}
		
	}
	
	public double output(double[] inputData) {
		double dotProduct = 0;
		
		for(int i = 0; i < unitWeights.length; i++) {
			dotProduct += (unitWeights[i] * inputData[i]);
		}
		lastOutput = 1 / (1 + Math.exp(-dotProduct));
		return lastOutput;
		
	}	
	
	public double getLastOutput() {
		return lastOutput;
	}	
	
}
