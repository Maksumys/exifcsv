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
import java.util.List;
import java.util.Random;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class Main {

  public static HashMap<String, ArrayList<String>> getPhotoData(List<File> photos)
      throws Exception {
    var photoData = new HashMap<String, ArrayList<String>>();

    int countIter = 0;

    for (var photo : photos) {
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
          if (!photoData.containsKey(tag.getTagName())) {
            photoData.put(tag.getTagName(), new ArrayList<>());
          }

          var tagName = tag.getTagName();

          var exifList = photoData.get(tagName);

          if (exifList.size() != countIter) {
            System.out.println("OOPS!!!! Повторяющиеся данные!!");
          } else {
            exifList.add(tag.getDescription());
          }
        }

        if (directory.hasErrors()) {
          for (String error : directory.getErrors()) {
            System.err.format("OOPS!!!! ERROR: %s", error);
          }
        }
      }

      countIter++;

      int finalCountIter = countIter;

      photoData.forEach((k, v) -> {
        if (v.size() != finalCountIter) {
          v.add(" ");
        }
      });
    }

    return photoData;
  }

  public static void createCSVFile(HashMap<String, ArrayList<String>> photosData)
      throws IOException {
    FileWriter out = new FileWriter("data.csv");

    var headers = photosData.keySet().stream().toArray(String[]::new);

    var countRecords = photosData.get(headers[0]).size();

    try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(headers))) {
      for (int i = 0; i < countRecords; i++) {
        for (var header : headers) {
          printer.print(photosData.get(header).get(i));
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