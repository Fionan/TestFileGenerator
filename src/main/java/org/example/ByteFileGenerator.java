package org.example;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class ByteFileGenerator {

    public static void generateFile(String filePath, int fileSizeInBytes) throws IOException {
        byte[] bytes = new byte[fileSizeInBytes];

        for (int i = 0; i < fileSizeInBytes; i++) {
            bytes[i] = (byte) (i % 256);
        }

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(bytes);
        }
    }

    public static void generateCustomFile(String filePath, int fileSizeInBytes) throws IOException {
        byte[] bytes = new byte[fileSizeInBytes];

        for (int i = 0; i < fileSizeInBytes; i++) {
            bytes[i] = (byte) (i / 16 % 256); // 16 bytes of the same value
        }

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(bytes);
        }
    }

    public static void analyzeFile(String filePath) throws IOException {
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
        boolean isValid = true;

        for (int i = 0; i < fileContent.length; i++) {
            byte expectedValue = (byte) (i / 16 % 256);
            if (fileContent[i] != expectedValue) {
                isValid = false;
                break;
            }
        }

        if (isValid) {
            System.out.println("File passes the pattern check.");
        } else {
            System.out.println("File does not follow the specified pattern.");
        }
    }

    public static String calculateSHA256(String filePath) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] byteArray = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesRead);
            }
        }

        byte[] hashBytes = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }
    public static void main(String[] args) throws IOException {

        if(args.length < 2){

            System.out.println("Create new test file : <new> <file name>  <number> eg. \"new\" \"testFile_\" \"1024\"");
            System.out.println("Check file <chk/check> <file name> eg. chk \"testFile_1024.bin\"");

            System.exit(1);
        }

       String command = args[0].toLowerCase();

        switch (args[0]){
            case "chk":
            case "check":
                if (args.length == 2){
                    String fileName = args[1];
                    analyzeFile(fileName);
                    try {
                        calculateSHA256(fileName);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    System.exit(1);
                }
                break;
            case "new":
                if(args.length==3){
                    String fileName = args[1];
                    int fileSizeInBytes = Integer.parseInt(args[2]);
                    String filePath = fileName + fileSizeInBytes + ".bin";
                    generateCustomFile(filePath, fileSizeInBytes);
                    try {
                        calculateSHA256(fileName);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }else{

                    System.exit(1);
                }

            default:
                System.out.println("Create new test file : <new> <file name>  <number> eg. \"new\" \"testFile_\" \"1024\"");
                System.out.println("Check file <chk/check> <file name> eg. chk \"testFile_1024.bin\"");

                System.exit(1);




        }




    }
}