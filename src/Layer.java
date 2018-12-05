
public class Layer {
	private int unitCount;
	private int inputCount;
	SigmoidUnit[] units;
	
	double[] lastOutput;
	double[] lastInput;
	private double[] lastError;
	
	//inputCount should be # of data features + 1
	public Layer(int unitCount, int inputCount) {
		this.unitCount = unitCount;
		this.inputCount = inputCount;
		
		this.units = new SigmoidUnit[unitCount];
		for(int i = 0; i < unitCount; i++) {
			units[i] = new SigmoidUnit(inputCount);
		}
	}
	
	public double[] output(double[] input) {	
		lastOutput = new double[unitCount];
		lastInput = input.clone();
		for(int i = 0; i < unitCount; i++) {
			lastOutput[i] = units[i].output(input);
		}
		
		return lastOutput;
	}
	
	public void setLastError(double[] error) {
		this.lastError = error;
	}
	
	public double[] getLastError() {
		return lastError;
	}
	
	public void updateWeights(double rate ) {
		for(int u = 0; u < unitCount; u++) {
			for(int i = 0; i < inputCount; i++) {
				units[u].unitWeights[i] += (rate * lastError[u] * lastInput[i]);
			}
		}
	}
	
}
