/*
 * Copyright 2014 Indiana University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.iu.benchmark.bcast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class DataGen {

  static void generateBcastData(long totalBytes, Path bcastDataDirPath,
    FileSystem fs) throws IOException {
    Random random = new Random();
    double[] data = null;
    if (fs.exists(bcastDataDirPath))
      fs.delete(bcastDataDirPath, true);
    if (!fs.mkdirs(bcastDataDirPath)) {
      throw new IOException("Mkdirs failed to create "
        + bcastDataDirPath.toString());
    }
    data = new double[(int) (totalBytes / (long) 8)];
    for (int i = 0; i < data.length; i++) {
      data[i] = random.nextDouble() * 1000;
    }
    Path bcastFilePath = new Path(bcastDataDirPath, BcastConstants.BCAST_FILE);
    System.out.println("Generate bcast data." + bcastFilePath.toString());
    FSDataOutputStream out = fs.create(bcastFilePath, true);
    for (int i = 0; i < data.length; i++) {
      out.writeDouble(data[i]);
    }
    out.flush();
    out.close();
    System.out.println("Wrote centroids data to file");
  }

  static void generateMapperInputs(int numMappers, String localDirName,
    Path dataDirPath, FileSystem fs) throws IOException, InterruptedException,
    ExecutionException {
    // Check data directory
    if (fs.exists(dataDirPath)) {
      fs.delete(dataDirPath, true);
    }
    // Check local directory
    // If existed, regenerate data
    File localDir = new File(localDirName);
    if (localDir.exists() && localDir.isDirectory()) {
      for (File file : localDir.listFiles()) {
        file.delete();
      }
      localDir.delete();
    }
    boolean success = localDir.mkdir();
    if (success) {
      System.out.println("Directory: " + localDir + " created");
    }
    // Create random data points
    int poolSize = Runtime.getRuntime().availableProcessors();
    ExecutorService service = Executors.newFixedThreadPool(poolSize);
    List<Future<?>> futures = new ArrayList<Future<?>>();
    for (int k = 0; k < numMappers; k++) {
      Future<?> f = service.submit(new DataGenRunnable(localDirName, Integer
        .toString(k)));
      futures.add(f);
    }
    for (Future<?> f : futures) {
      f.get();
    }
    // Shut down the executor service so that this thread can exit
    service.shutdownNow();
    // Copy to HDFS
    Path localDirPath = new Path(localDirName);
    fs.copyFromLocalFile(localDirPath, dataDirPath);
  }

  static void generateData(long totalBytes, int numMappers, Path dataDirPath,
    Path bcastDataDirPath, String localDirName, FileSystem fs)
    throws IOException, InterruptedException, ExecutionException {
    System.out.println("Generating data..... ");
    generateMapperInputs(numMappers, localDirName, dataDirPath, fs);
    // generateBcastData(totalBytes, bcastDataDirPath, fs);
  }
}
