import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;

public class ImageEditor {
    public static void main(String[] args) {
        //Handle incorrect argument length
        if (args.length != 3 && args.length != 4) {
            exitCodeMessage();
            return;
        }

        //Get input file set up
        String inputFileName = args[0];
        Scanner inFile = null;
        try {
            inFile = new Scanner(new File(inputFileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Get rid of comments
        inFile.useDelimiter("((#[^\\n]*\\n)|(\\s+))+");

        //Get output file set up
        String outputFileName = args[1];
        PrintWriter outFile = null;
        try {
            outFile = new PrintWriter(new File(outputFileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        //P3
        inFile.nextLine();
        //Creator
        inFile.nextLine();
        //Width and height
        int width = inFile.nextInt();

        //System.out.println((inFile.nextInt()));
        int height = inFile.nextInt();
        //Max color value
        inFile.next();

        //Set up StringBuilder
        StringBuilder sb = new StringBuilder(width * height + 2);
        sb.append("P3").append("\n");
        sb.append(width).append(" ").append(height).append("\n");
        sb.append("255").append("\n");

        //Initialize 2D pixel array
        Pixel[][] pixelArr = new Pixel[height][width];
        for (int i = 0; i < height; i++) {
            //System.out.println("Run # " + i);
            for (int j = 0; j < width; j++) {
                //System.out.println("Run # " + j);
                pixelArr[i][j] = new Pixel();
            }
        }

        //Set all the pixel values
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pixelArr[i][j].setRed(inFile.nextInt());
                pixelArr[i][j].setGreen(inFile.nextInt());
                pixelArr[i][j].setBlue(inFile.nextInt());
            }
        }

        //Alter picture
        switch (args[2]) {
            case "grayscale":
                //Code to do grayscale
                grayscale(pixelArr, width, height);
                break;
            case "invert":
                //Code to do invert
                invert(pixelArr, width, height);
                break;
            case "emboss":
                //Code to do emboss
                emboss(pixelArr, width, height);
                break;
            case "motionblur":
                int blurLength;
                if (args.length == 4) {
                    blurLength = Integer.parseInt(args[3]);
                    if (blurLength <= 0) {
                        exitCodeMessage();
                        return;
                    }
                }
                else {
                    exitCodeMessage();
                    return;
                }

                //Code to do motionblur
                motionblur(pixelArr, width, height, blurLength);
                break;
            default:
                exitCodeMessage();
                return;
        } //End of switch statement


        //Append all pixel values to StringBuilder
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                sb.append(pixelArr[i][j].getRed()).append("\n");
                sb.append(pixelArr[i][j].getGreen()).append("\n");
                sb.append(pixelArr[i][j].getBlue()).append("\n");
            }
        }

        outFile.println(sb.toString());

    }

    public static void exitCodeMessage() {
        System.out.println("java ImageEditor in-file out-file " +
                "(grayscale|invert|emboss|motionblur motion-blur-length)");
    }

    public static void grayscale(Pixel[][] pixelArr, int width, int height) {
        int average;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                average = 0;
                average += pixelArr[i][j].getRed();
                average += pixelArr[i][j].getGreen();
                average += pixelArr[i][j].getBlue();
                average /= 3;
                pixelArr[i][j].setRed(average);
                pixelArr[i][j].setGreen(average);
                pixelArr[i][j].setBlue(average);
            }
        }
    }

    public static void invert(Pixel[][] pixelArr, int width, int height) {
         /*
                    if x >= 128, do x - 128 and then assign color
                    to 127 - x
                    if x < 128, do 128 - x and then assign color
                    to 127 + x
                */
        int temp;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                //Invert red
                temp = pixelArr[i][j].getRed();
                if (temp >= 128) {
                    temp -= 128;
                    temp = 127 - temp;
                } else {
                    temp = 128 - temp;
                    temp = 127 + temp;
                }
                pixelArr[i][j].setRed(temp);

                //Invert green
                temp = pixelArr[i][j].getGreen();
                if (temp >= 128) {
                    temp -= 128;
                    temp = 127 - temp;
                } else {
                    temp = 128 - temp;
                    temp = 127 + temp;
                }
                pixelArr[i][j].setGreen(temp);

                //Invert blue
                temp = pixelArr[i][j].getBlue();
                if (temp >= 128) {
                    temp -= 128;
                    temp = 127 - temp;
                } else {
                    temp = 128 - temp;
                    temp = 127 + temp;
                }
                pixelArr[i][j].setBlue(temp);
            }
        }
    }

    public static void emboss(Pixel[][] pixelArr, int width, int height) {
        int redDiff, greenDiff, blueDiff, v, maxDifference, temp;

        for (int i = height - 1; i >= 0; i--) {
            for (int j = width - 1; j >= 0; j--) {
                if (i == 0 || j == 0)
                    v = 128;
                else {
                    redDiff = pixelArr[i][j].getRed() - pixelArr[i-1][j-1].getRed();
                    greenDiff = pixelArr[i][j].getGreen() - pixelArr[i-1][j-1].getGreen();
                    blueDiff = pixelArr[i][j].getBlue() - pixelArr[i-1][j-1].getBlue();

                    maxDifference = Math.max(Math.abs(redDiff), Math.max(Math.abs(greenDiff), Math.abs(blueDiff)));
                    if (maxDifference == Math.abs(redDiff))
                        temp = redDiff;
                    else if (maxDifference == Math.abs(greenDiff))
                        temp = greenDiff;
                    else
                        temp = blueDiff;
                    maxDifference = temp;
                    v = 128 + maxDifference;
                    if (v < 0)
                        v = 0;
                    else if (v > 255)
                        v = 255;
                }

                pixelArr[i][j].setRed(v);
                pixelArr[i][j].setGreen(v);
                pixelArr[i][j].setBlue(v);

            }
        }
    }

    public static void motionblur(Pixel[][] pixelArr, int width, int height, int blurLength) {
        /*
        Initialize new empty array
        for loop
            for loop
                if j + n >= width then only average the pixels up
                to width
                set each pixel in blurredArray to the average of the
                right "n - 1" pixels of original array
        */
        //Initialize 2D blurred pixel array
        Pixel[][] blurredPixelArray = new Pixel[height][width];
        for (int i = 0; i < height; i++) {
            //System.out.println("Run # " + i);
            for (int j = 0; j < width; j++) {
                //System.out.println("Run # " + j);
                blurredPixelArray[i][j] = new Pixel();
            }
        }

        int averageRed, averageGreen, averageBlue;
        int edgeBlurLength;
        //Set all the pixel values
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                averageRed = 0;
                averageGreen = 0;
                averageBlue = 0;

                //If we are near an edge
                if (j + blurLength >= width) {
                    edgeBlurLength = width - j;
                    for (int num = 0; num < edgeBlurLength; num++) {
                        averageRed += pixelArr[i][j+num].getRed();
                        averageGreen += pixelArr[i][j+num].getGreen();
                        averageBlue += pixelArr[i][j+num].getBlue();
                    }
                    averageRed /= edgeBlurLength;
                    averageGreen /= edgeBlurLength;
                    averageBlue /= edgeBlurLength;
                }
                //If we are not near an edge
                else {
                    for (int num = 0; num < blurLength; num++) {
                        averageRed += pixelArr[i][j+num].getRed();
                        averageGreen += pixelArr[i][j+num].getGreen();
                        averageBlue += pixelArr[i][j+num].getBlue();
                    }
                    averageRed /= blurLength;
                    averageGreen /= blurLength;
                    averageBlue /= blurLength;
                }
                blurredPixelArray[i][j].setRed(averageRed);
                blurredPixelArray[i][j].setGreen(averageGreen);
                blurredPixelArray[i][j].setBlue(averageBlue);
            }
        }

        //Transfer the blurredPixelArray to the original array
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pixelArr[i][j].setRed(blurredPixelArray[i][j].getRed());
                pixelArr[i][j].setGreen(blurredPixelArray[i][j].getGreen());
                pixelArr[i][j].setBlue(blurredPixelArray[i][j].getBlue());
            }
        }
    }


} //End of class