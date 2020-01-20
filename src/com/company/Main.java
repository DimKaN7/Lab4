package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static final String IMAGE_FILE   = "/Users/DimKa_N7/Documents/IS/lab4/self1.png";
    private static final String CORRUPTED_FILE   = "/Users/DimKa_N7/Documents/IS/lab4/selfCorrupted.png";

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        print("Введите сообщение в двоичном виде: ");
        String input = br.readLine();
        print("Введите полином в двоичном виде: ");
        String polynom = br.readLine();

	    boolean[] inputBool = toBoolArr(input);
	    boolean[] polynomBool = toBoolArr(polynom);

        boolean[] temp = new boolean[inputBool.length + polynomBool.length - 1];

        // начальное заполнение массива (сначала инвертированное сообщение, потом 0)
        for (int i = 0; i < temp.length; i++) {
            if (i < inputBool.length) {
                temp[i] = inputBool[i];
            }
            else {
                temp[i] = false;
            }
        }

        print("CRC: " + toString(getCRC(temp, polynomBool)));

        BufferedImage image = ImageIO.read(new File(IMAGE_FILE));
        String binary = "";
        for (int c = 0; c < 3; c++) {
            int[][] colorMatrix = getPixels(image, c);
            for (int i = 0; i < 50; i++) {
                for (int j = 0; j < 50; j++) {
                    binary += Integer.toBinaryString(colorMatrix[i][j]);
                }
            }
        }
        print(binary);
        print("Image CRC: " + toString(getCRC(toBoolArr(binary), polynomBool)));

    }

    private static boolean[] getCRC(boolean[] data, boolean[] polynom) {
        int currentIndex = polynom.length;
        boolean[] currentSubWord = split(data, 0, currentIndex);
        boolean[] remainder = XOR(currentSubWord, polynom);

        while (currentIndex < data.length) {
            int difference = polynom.length - remainder.length;
            for (int i = 0; i < difference; i++) {
                remainder = addBit(remainder, data[currentIndex]);
                currentIndex++;
            }
            remainder = XOR(remainder, polynom);
        }
        return remainder;
    }

    private static boolean[] addBit(boolean[] arr, boolean bit) {
        boolean[] result = new boolean[arr.length + 1];
        for (int i = 0; i < arr.length; i++) {
            result[i] = arr[i];
        }
        result[result.length - 1] = bit;
        return result;
    }

    private static boolean[] split(boolean[] arr, int startIndex, int endIndex) {
        boolean[] result = new boolean[endIndex - startIndex];
        for (int i = startIndex, j = 0; i < endIndex; i++, j++) {
            result[j] = arr[i];
        }
        return result;
    }

    private static boolean[] XOR(boolean[] arr1, boolean[] arr2) {
        boolean[] result = new boolean[arr1.length];
        for (int i = 0; i < arr1.length; i++) {
            result[i] = arr1[i] ^ arr2[i];
        }
        for (int i = 0; i < result.length; i++) {
            if (result[i] == false) {
                result = removeElement(result, i);
                i--;
            }
            else break;
        }
        return result;
    }

    private static boolean[] removeElement(boolean[] arr, int index) {
        boolean[] result = new boolean[arr.length - 1];
        for (int i = 0, k = 0; i < arr.length; i++) {
            if (i != index) {
                result[k++] = arr[i];
            }
        }
        return result;
    }

    private static boolean[] toBoolArr(String str) {
        boolean[] result = new boolean[str.length()];
        for (int i = 0; i < result.length; i++) {
            result[i] = str.charAt(i) == '0' ? false : true;
        }
        return result;
    }

    private static String toString(boolean[] arr) {
        String result = "";
        for (int i = 0; i < arr.length; i++) {
            result += arr[i] == false ? "0" : "1";
        }
        return result;
    }

    private static int[][] getPixels(BufferedImage image, int color) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] result = new int[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Color c = new Color(image.getRGB(col, row));
                switch (color) {
                    case 0:
                        result[row][col] = c.getRed();
                        break;
                    case 1:
                        result[row][col] = c.getGreen();
                        break;
                    case 2:
                        result[row][col] = c.getBlue();
                        break;
                }
            }
        }
        return result;
    }

    private static void print(String text) {
        System.out.println(text);
    }

}
