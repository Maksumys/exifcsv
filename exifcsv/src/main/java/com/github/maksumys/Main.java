package com.github.maksumys;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class Main {

  public static ArrayList<HashMap<String, String>> getPhotoData(List<File> photos) {
    ArrayList<HashMap<String, String>> result = new ArrayList<>();

    for (var photo : photos) {
      var photoData = new HashMap<String, String>();

      Metadata metadata;
      try {
        metadata = ImageMetadataReader.readMetadata(photo);
      } catch (ImageProcessingException e) {
        System.out.println("Meta data read fail! Ptuk!");
        continue;
      } catch (IOException e) {
        System.out.println("Meta data read fail! Ptuk! In file: [" + photo.getAbsolutePath() + "]");
        continue;
      }

      for (var directory : metadata.getDirectories()) {
        for (var tag : directory.getTags()) {
          if (photoData.containsKey(directory.getName() + "_" + tag.getTagName())) {
            System.out.println("bug?");
          }
          photoData.put(directory.getName() + "_" + tag.getTagName(), tag.getDescription());
        }

        if (directory.hasErrors()) {
          for (String error : directory.getErrors()) {
            System.err.format("OOPS!!!! ERROR: %s", error);
          }
        }
      }
      result.add(photoData);
    }

    return result;
  }

  public static void createCSVFile(ArrayList<HashMap<String, String>> photosData) throws IOException {
    FileWriter out = new FileWriter("data.csv");

    Set<String> headers = new HashSet<>();

    for(var photo : photosData) {
      headers.addAll(photo.keySet());
    }

    try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(headers.stream().toArray(String[]::new)))) {
      for(var photo : photosData) {
        for(var header : headers) {
          printer.print(photo.getOrDefault(header, " "));
        }
        printer.println();
      }
    }
  }

  public static void main(String[] args) throws Exception {
    for (String s : args) {
      System.out.println(s);
    }

    if (args.length == 1) {
      System.out.println("Питюк ты указал путь до папки с фотками: " + args[0]);
    } else {
      System.out.println("Необходимо указать путь до папки с фотками, питюк!");
      return;
    }

    System.out.println("Бросок!!!!");

    var pituk = new Random().nextInt();

    if (pituk % 2 == 0) {
      System.out
          .println("У вас выпало [" + pituk + "]. Вам не повезло. Попытайте удачу в другой раз!");
      return;
    }

    File dir = new File(args[0]);
    File[] arrFiles = dir.listFiles();
    assert arrFiles != null;
    List<File> lst = Arrays.asList(arrFiles);

    var photoData = getPhotoData(lst);

    createCSVFile(photoData);

    System.out.println("Файл: [" + "data.csv" + "] успешно сгенерирован! Enjoy!");
  }
}