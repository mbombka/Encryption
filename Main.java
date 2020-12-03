package com.company;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;




public class Main {




    public static void main(String[] args) {

        String mode = "enc";
        int key = 0;
        String original = "";
        String result = "";
        Boolean useFileIn = false;
        Boolean useFileOut = false;
        String pathIn = "";
        String pathOut = "";
        String algorithmType = "shift";

        for (int i=0; i < args.length; i++) {
            //get mode
            if (args[i].equals("-mode") && i < args.length - 1) {
                mode = args[i+1];
            }
            //get key
            if (args[i].equals("-key") && i < args.length - 1) {
                key = Integer.parseInt(args[i+1]);
            }
            //get text from -data or file
            if (args[i].equals("-data") && i < args.length - 1) {
                original = args[i+1];
            } else   //get text from -in file
                if (args[i].equals("-in") && i < args.length - 1) {
                    pathIn = args[i+1];
                    useFileIn = true;
                }
            //get path for out
            if (args[i].equals("-out") && i < args.length - 1) {
                pathOut = args[i+1];
                useFileOut = true;
            }
            //get algorith type - alg
            if (args[i].equals("-alg") && i < args.length - 1) {
                algorithmType = args[i+1];

            }

        }

        //get original text from file
        if (useFileIn) {
            File fileIn = new File(pathIn);
            try (Scanner scanner = new Scanner(fileIn)) {
                while (scanner.hasNext()) {
                    original = scanner.nextLine();
                }
            } catch (Exception e) {
                System.out.println("Error, " + e.getMessage());
            }
        }


        AlgorithmType algorithmToProduce;
        //decide aout algorith:
        if ("unicode".equals(algorithmType) && "enc".equals(mode) ){
            algorithmToProduce = AlgorithmType.UnicodeEncryption;
        } else if ("unicode".equals(algorithmType) && "dec".equals(mode) ){
            algorithmToProduce = AlgorithmType.UnicodeDecryption;
        } else if ("shift".equals(algorithmType) && "enc".equals(mode) ){
            algorithmToProduce = AlgorithmType.ShiftEnryption;
        } else if ("shift".equals(algorithmType) && "dec".equals(mode) ){
            algorithmToProduce = AlgorithmType.ShiftDecription;
        } else {
            algorithmToProduce = AlgorithmType.ShiftEnryption;
        }
        final AlgorithmFactory algorithmFactory = new AlgorithmFactory();
        final Algorithm algorithm= algorithmFactory.produce(algorithmToProduce);
        result = algorithm.operation(original, key);



        //print result to file or console
        if (useFileOut) {
            File fileOut = new File(pathOut);

            try (PrintWriter printWriter = new PrintWriter(fileOut)) {
                printWriter.print(result); // prints a string

            } catch (IOException e) {
                System.out.printf("Error, An exception occurs %s", e.getMessage());
            }
        } else {
            System.out.print(result);
        }

    }

}



interface Algorithm {

    String operation(String text, int key);

}
enum AlgorithmType { //with this one we chose right algorith
    UnicodeEncryption,
    UnicodeDecryption,
    ShiftEnryption,
    ShiftDecription
}
class UnicodeAlgEnc implements Algorithm {

    @Override
    public String operation(String text, int key) {
        key = key%127;
        String result = "";
        for (char ch : text.toCharArray()) {
            ch += key;
            result += ch;
        }
        return result;
    }
}

class UnicodeAlgDec implements Algorithm {

    @Override
    public String operation(String text, int key) {
        key = key%127;
        String result = "";
        for (char ch : text.toCharArray()) {
            ch -= key;
            result += ch;
        }
        return result;
    }
}

class ShiftAlgEnc implements Algorithm {

    @Override
    public String operation(String text, int key) {
        key = key%26;
        String result = "";
        for (char ch : text.toCharArray()) {
            if (ch >= 97 && ch <=122) { //this are small letters, a = 97, z =122
                ch = (ch + key > 122 ) ? (char) (96 + (ch + key - 122)): (char) (ch + key);
            } else if (ch >= 65 && ch <=90){ // this are capital letters,  A=65, Z = 90
                ch = (ch + key > 90 ) ? (char) (64 + (ch + key - 90)): (char) (ch + key);
            }

            result += ch;
        }
        return result;
    }
}

class ShiftAlgDec implements Algorithm {

    @Override
    public String operation(String text, int key) {
        key = key%26;
        String result = "";
        for (char ch : text.toCharArray()) {
            if (ch >= 97 && ch <=122) { //this are small letters, a = 97, z =122
                ch = (ch - key < 97 ) ? (char) (122 - (key - (ch - 96)) ): (char) (ch - key);
            } else if (ch >= 65 && ch <=90)  { // this are capital letters,  A=65, Z = 90
                ch = (ch - key < 65 ) ? (char) (90 - (key - (ch - 64)) ): (char) (ch - key);
            }


            result += ch;
        }
        return result;
    }
}

class AlgorithmFactory { //create right algorith depending of parameter
    public Algorithm produce(AlgorithmType type) {

        switch (type){
            case UnicodeEncryption:
                return new UnicodeAlgEnc();
            case UnicodeDecryption:
                return new UnicodeAlgDec();
            case ShiftEnryption:
                return new ShiftAlgEnc();
            case ShiftDecription:
                return new ShiftAlgDec();
            default:
                return null;

        }

    }

}

