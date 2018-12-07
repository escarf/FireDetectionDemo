package FireDetectionDemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class AJEApp extends Application{
	private static Network network;
	private static String trainingFile = "training_data.csv";
	private static String testingFile = "testing_data.csv";
	
	private static int classLabelCount = 2;
	private static int inputCount = 400 + 1;
	private static int trainingInstanceCount = 47;
	private static int testingInstanceCount = 19;
	
	private static int hiddenLayerCount = 5;
	private static int hiddenLayerUnitCount = 256;
	
	private static double[][] trainingInstances;
	private static double[][] testingInstances;	
	
	public static void main(String[] args) throws IOException {		
		boolean shouldCreateTrainingCSVFromImages = false;
		if(shouldCreateTrainingCSVFromImages) {
	        String fileNameFire = "Training\\FireImages"; // original file

			String resizedImagesFire = "Training\\FireResized"; // resized file
	        String fileNameNoFire = "Training\\NoFireImages"; // original file
	        String resizedImagesNoFire = "Training\\NoFireResized"; // resized file
	        String outputFileName = "training_data.csv";
			
	        
			ReadImage imageReader = new ReadImage(fileNameFire, resizedImagesFire, fileNameNoFire, resizedImagesNoFire, outputFileName);
		}
		
		boolean shouldCreateTestingCSVFromImages = false;
		
		if(shouldCreateTestingCSVFromImages) {
	        String fileNameFire = "Testing\\FireImages"; // original file
	        String resizedImagesFire = "Testing\\FireResized"; // resized file
	        String fileNameNoFire = "Testing\\NoFireImages"; // original file
	        String resizedImagesNoFire = "Testing\\NoFireResized"; // resized file
	        String outputFileName = "testing_data.csv";
			
	        
			ReadImage imageReader = new ReadImage(fileNameFire, resizedImagesFire, fileNameNoFire, resizedImagesNoFire, outputFileName);
		}
		
		
		launch(args);
		
	}
	
    @Override
    public void start(final Stage primaryStage) {
          	
        final FileChooser fileChooser = new FileChooser();
        final Button trainButton = new Button("Train Using Existing Images");
        final Button testAccuracyButton = new Button("Test Network Accuracy");
 
        trainButton.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {
                	
                    setExtDataFilters(fileChooser);
                    File selectedTrainingFile = fileChooser.showOpenDialog(primaryStage);
                    if (selectedTrainingFile == null) {
                    	try {
							createAndTrainNetwork(trainingFile);
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
                    	
                    } else {
                    	try {
							createAndTrainNetwork(selectedTrainingFile.getAbsolutePath());
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
                    }
                	
                	
                	Alert alert = new Alert(AlertType.INFORMATION);
                	alert.setTitle("Training Status");
                	alert.setHeaderText(null);
                	alert.setContentText("Training Complete");

                	alert.showAndWait();
                	
                	
                }
            });
        
        
        testAccuracyButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                    	
                        setExtDataFilters(fileChooser);
                        File selectedTestingFile = fileChooser.showOpenDialog(primaryStage);
                        double accuracy = -1;
                        if (selectedTestingFile == null) {
                        	try {
                        		accuracy = testNetworkAccuracy(testingFile);
    						} catch (FileNotFoundException e1) {
    							e1.printStackTrace();
    						}
                        	
                        } else {
                        	try {
                        		accuracy = testNetworkAccuracy(selectedTestingFile.getAbsolutePath());
    						} catch (FileNotFoundException e1) {
    							e1.printStackTrace();
    						}
                        }
                    	
                    	
                    	Alert alert = new Alert(AlertType.INFORMATION);
                    	alert.setTitle("Testing Status");
                    	alert.setHeaderText(null);
                    	alert.setContentText("Testing Complete. Accuracy: " + accuracy);

                    	alert.showAndWait();
                    	
                    	
                    }
                });
        
        
        VBox root = new VBox();       
        root.getChildren().add(trainButton);
        root.getChildren().add(testAccuracyButton);
         
        Scene scene = new Scene(root, 400, 150);
         
        primaryStage.setTitle("AJE Classifier");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void setExtDataFilters(FileChooser chooser){
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("CSV", "*.csv")
        );
    }
       
	
	private static double[][] readDataFromFile(String fileName, double[][] data, int instanceCount) throws FileNotFoundException {		
	 	Scanner input;
	 	int fileInstanceCount = instanceCount;
	 	double[][] result;
	 	
	 	//if fileInstanceCount is -1, read from the included testing file
	 	if(fileInstanceCount == -1) {
	 		input = new Scanner(new File(fileName));
	 		fileInstanceCount = 0;
		 	while(input.hasNextLine()) {
		 		input.nextLine();
		 		fileInstanceCount++;
		 		
		 	}
		 	input.close();
	 	}
	 	
	 	result = new double[fileInstanceCount][];

	 	input = new Scanner(new File(fileName));
		int currentDigitValue;
			
		for(int i = 0; i < fileInstanceCount; i++) {
			String currentLine[] = input.nextLine().split(",");
			
			result[i] = new double[inputCount  + classLabelCount];

			currentDigitValue = Integer.parseInt(currentLine[0]);	
			
			result[i][0] = 1; //for the first weight
			
			for(int j = 1; j < inputCount; j++){
				result[i][j] = Double.parseDouble(currentLine[j]) / 255;

			}
			
			for(int j = inputCount; j < inputCount + classLabelCount; j++) {
				
				
				if(currentDigitValue == j - (inputCount)) {
					result[i][j] = 1;
				}
				else {
					result[i][j] = 0;
				}
			}			

		}
		input.close();
		return result;
	 
	}
	
	private void createAndTrainNetwork(String trainingFilePath) throws FileNotFoundException {	
		
		trainingInstances = readDataFromFile(trainingFilePath, trainingInstances, trainingInstanceCount);
		network = new Network(classLabelCount, inputCount, hiddenLayerCount, hiddenLayerUnitCount);
		network.trainNetwork(trainingInstances);		

	}
	
	private double testNetworkAccuracy(String testingFilePath) throws FileNotFoundException {
		testingInstances = readDataFromFile(testingFilePath, testingInstances, testingInstanceCount);
		double accuracy = 0;
		for(int i = 0; i < testingInstances.length; i++) {
			
			double[] outputs = network.networkOutput(testingInstances[i]);
			
			int indexOfMaxOutput = 0;
				//this is the location in outputs that has the highest
				// predicted value
			
			int thisTrueDigit = -1;
			for(int j = 0; j < outputs.length; j++) {
				int currentInstanceIndex = (j + testingInstances[i].length) - classLabelCount;					
				
				if(testingInstances[i][currentInstanceIndex] == 1) {
					thisTrueDigit = j;
				}
				
				if(outputs[j] > outputs[indexOfMaxOutput]) {
					indexOfMaxOutput = j;
				}
																			
			}
			int predictedDigit = indexOfMaxOutput;
			
			if(predictedDigit == thisTrueDigit) {
				accuracy++;
			}
			
		}
		accuracy = accuracy / testingInstances.length;		
		return accuracy;
		
	}
	
}
