package pl.edu.pwr.filechecker;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FileChecker {
    private final String currentPath;
    private final Map<String, String> currentHashList;
    private final Map<String, String> previousHashList;
    private final ArrayList<String> editedFiles;
    private ArrayList<String> filesList;
    private ArrayList<String> hashFileList;

    public FileChecker(String currentPath) {
        this.currentPath = currentPath;
        filesList = new ArrayList<>();
        currentHashList = new HashMap<>();
        previousHashList = new HashMap<>();
        hashFileList = new ArrayList<>();
        editedFiles = new ArrayList<>();
    }

    // test
    public static void main(String[] args) {
        FileChecker fileChecker = new FileChecker("D:\\uczelnia\\semestr VI\\test");
        ArrayList<String> list = fileChecker.compareFilesChecksum();
        for (String item : list)
            System.out.println(item);
    }

    // compare hashes of files in current directory with previously rendered one
    public ArrayList<String> compareFilesChecksum() {
        renderPath();                                                                               // create .md5 dir
        filesList = loadFileList(currentPath);                                                      // load file list from dir
        hashFileList = loadFileList(currentPath + "\\.md5");                                   // load hash file list
        loadFilesHashFromFile();                                                                    // load previous hashes from files
        createHashFiles();                                                                          // create new hashes - override previous one if exist
        compareFilesHash();                                                                         // compare rendered hashes with loaded from files
        return editedFiles;                                                                         // return ArrayList with name of edited files
    }

    // comparison of previously rendered hashes with current one
    private void compareFilesHash() {
        previousHashList.forEach((key, value) -> {
            if (!value.equals(currentHashList.get(key))) {
                editedFiles.add(key);
            }
        });
    }

    // check if directory .md5 exist, if not create it
    private void renderPath() {
        if (!Files.exists(Path.of(currentPath + "\\.md5"))) {
            try {
                Path path = Paths.get(currentPath + "\\.md5");
                Files.createDirectory(path);
            } catch (IOException exp) {
                System.out.println("Create dir error!");
            }
        }
    }

    // load already existing hashes from .md5 directory to map < key = directory, value = hash >
    private void loadFilesHashFromFile() {
        BufferedReader bufferedReader;
        for (String name : hashFileList) {
            try {
                bufferedReader = new BufferedReader(new FileReader(currentPath + "\\.md5\\" + name));
                String hash = bufferedReader.readLine();
                previousHashList.put(name, hash);
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // create a hash and file for all files from given directory
    private void createHashFiles() {
        for (String name : filesList) {
            File file = new File(currentPath + "\\.md5\\" + name + ".txt");
            try {
                Files.deleteIfExists(Paths.get(currentPath + "\\.md5\\" + name + ".txt"));
                file.createNewFile();
                String hash = hashFile(name);
                Files.write(Paths.get(currentPath + "\\.md5\\" + name + ".txt"), hash.getBytes(), StandardOpenOption.APPEND);
                currentHashList.put(name + ".txt", hash);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // create a hash string of file given by 'name' value
    private String hashFile(String name) {
        String md5 = "";
        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        DigestInputStream dis = null;
        InputStream is = null;

        try {
            is = Files.newInputStream(Path.of(currentPath + "\\" + name));
            dis = new DigestInputStream(is, md);
            assert md != null;
            md5 = Arrays.toString(md.digest(dis.readAllBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            is.close();
            dis.close();
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
        return md5;
    }

    // load a list of existing files in given dir - load only files not other directories
    private ArrayList<String> loadFileList(String path) {
        File folder = new File(path);
        ArrayList<String> list = new ArrayList<>();
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File listOfFile : listOfFiles) {
                if (!listOfFile.isDirectory()) list.add(listOfFile.getName());
            }
        }
        return list;
    }
}
