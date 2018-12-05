import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ReadImage {
    private static final int IMG_WIDTH = 20;
    private static final int IMG_HEIGHT = 20;

    
    public ReadImage(String fileNameFire, String resizedImageFire, String fileNameNoFire, String resizedImageNoFire, String outputFileName) throws IOException {
    	
        File fileFire = new File(fileNameFire);
        
        ArrayList<String> fireList = new ArrayList<String>(); // list of file names w/ fire
        resizeImage(fileFire, fireList, fileNameFire, resizedImageFire);
        
        File fileNoFire = new File(fileNameNoFire);
       
        ArrayList<String> noFireList = new ArrayList<String>(); // list of file names w/out fire
        resizeImage(fileNoFire, noFireList, fileNameNoFire, resizedImageNoFire);
        
        writeToFile(fireList, noFireList, resizedImageFire, resizedImageNoFire, outputFileName);
    	
    }

    private static BufferedImage resizeImageHelper(BufferedImage originalImage, int type) {
        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        g.dispose();
        return resizedImage;
    }

    // Resizes all images in a folder to a uniform size
    public static void resizeImage(File directory, ArrayList<String> list, String fileName, String newFileName) throws IOException {
        File[] items = directory.listFiles();
        
        for (int i = 0; i < items.length; i++) {
            list.add(items[i].getName());
            try {
                String file = fileName;
                file += "\\" + items[i].getName();
                String newFile = newFileName;
                newFile += "\\" + items[i].getName();
                BufferedImage originalImage = ImageIO.read(new File(file));
                int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
                if (items[i].getName().endsWith("jpg")) {
                    BufferedImage resizeImageJpg = resizeImageHelper(originalImage, type);
                    ImageIO.write(resizeImageJpg, "jpg", new File(newFile));
                } else if (items[i].getName().endsWith("jpeg")) {
                    BufferedImage resizeImageJpeg = resizeImageHelper(originalImage, type);
                    ImageIO.write(resizeImageJpeg, "jpeg", new File(newFile));
                } else {
                    BufferedImage resizeImagePng = resizeImageHelper(originalImage, type);
                    ImageIO.write(resizeImagePng, "png", new File(newFile));
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // Takes both fire/no fire folders and creates csv file out of their pixel values
    public static void writeToFile(ArrayList<String> fileNameOne, ArrayList<String> fileNameTwo, String resizedFire, String resizedNoFire, String outputFileName) throws IOException {
        BufferedImage raw;
        PrintWriter pw = new PrintWriter(new File(outputFileName));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fileNameOne.size(); i++) {
            sb.append("1");
            sb.append(',');
            String image = resizedFire;
            image += "\\" + fileNameOne.get(i);
            raw = ImageIO.read(new File(image));
            int width = raw.getWidth();
            int height = raw.getHeight();
            int count = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    //this is how we grab the RGBvalue of a pixel
                    // at x,y coordinates in the image
                    int rgb = raw.getRGB(x, y);
                    //extract the red value
                    int r = (rgb >> 16) & 0xFF;
                    sb.append((double) r / 255);
                    sb.append(',');
                    count++;
                    //extract the green value
                    int g = (rgb >> 8) & 0xFF;
                    sb.append((double) g / 255);
                    sb.append(',');
                    count++;
                    //extract the blue value
                    int b = rgb & 0xFF;
                    sb.append((double) b / 255);
                    sb.append(',');
                    count++;
                }
            }
            sb.append('\n');
        }

        for (int i = 0; i < fileNameTwo.size(); i++) {
            sb.append("0");
            sb.append(',');
            String image = resizedNoFire;
            image += "\\" + fileNameTwo.get(i);
            
            raw = ImageIO.read(new File(image));
            sb.append(imageStringHelper(image, raw));
            
            
        }
        pw.write(sb.toString());
        pw.close();
    }
    
    private static String imageStringHelper(String image, BufferedImage raw) {
    	StringBuilder sb = new StringBuilder();
    	
    	
        int width = raw.getWidth();
        int height = raw.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                //this is how we grab the RGBvalue
                // of a pixel at x,y coordinates in the image
                int rgb = raw.getRGB(x, y);
                //extract the red value
                int r = (rgb >> 16) & 0xFF;
                sb.append((double) r / 255);
                sb.append(',');
                //extract the green value
                int g = (rgb >> 8) & 0xFF;
                sb.append((double) g / 255);
                sb.append(',');
                //extract the blue value
                int b = rgb & 0xFF;
                sb.append((double) b / 255);
                sb.append(',');
            }
        }
        sb.append('\n');
    	
    	
    	return sb.toString();
    }
}






