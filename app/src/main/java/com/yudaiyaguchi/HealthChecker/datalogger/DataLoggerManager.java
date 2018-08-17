package com.yudaiyaguchi.HealthChecker.datalogger;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

/*
 * Copyright 2013-2017, Kaleb Kircher - Kircher Engineering, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 * Created by KircherEngineerH on 4/27/2016.
 */
public class DataLoggerManager implements Runnable {
    // # just return the class name
    private static final String tag = DataLoggerManager.class.getSimpleName();

    public final static String DEFAULT_APPLICATION_DIRECTORY = "HealthChecker";

    private final static long THREAD_SLEEP_TIME = 20;
    public final static String FILE_NAME_SEPARATOR = "-";

    // boolean to indicate if the data should be written to a file.
    private volatile boolean logData = false;

    // Log output time stamp
    protected long logTime = 0;

    private ArrayList<String> csvHeaders;
    private ArrayList<String> csvValues;

    private DataLoggerInterface dataLogger;

    private Context context;

    private volatile ArrayList<String> rotation;
    private volatile ArrayList<String> acceleration;
    private volatile ArrayList<String> magnetic;
//    private volatile ArrayList<String> orientation;
    private volatile ArrayList<String> latilong;



    private Thread thread;

    public DataLoggerManager(Context context) {
        // # instantiate everything
        this.context = context;
        csvHeaders = getCsvHeaders();
        csvValues = new ArrayList<>();
        rotation = new ArrayList<>();
        acceleration = new ArrayList<>();
        magnetic = new ArrayList<>();
        latilong = new ArrayList<>();
//        orientation = new ArrayList<>();
    }

    @Override
    public void run() {
        while (logData && !Thread.currentThread().isInterrupted()) {
            // Check if the row is filled and ready to be written to the
            // log.
            logData();

            try {
                Thread.sleep(THREAD_SLEEP_TIME);
            } catch (InterruptedException e) {
                // very important to ensure the thread is killed
                Thread.currentThread().interrupt();
            }
        }

        // very important to ensure the thread is killed
        Thread.currentThread().interrupt();
    }


    public void startDataLog() throws IllegalStateException {
        if (!logData) {
            logData = true;
            logTime = System.currentTimeMillis();

            // Call CsvDataLogger class
            dataLogger = new CsvDataLogger(context, getFile(this.getFilePath(), this.getFileName()));
//            Log.d("myTag", this.getFilePath() + this.getFileName());
            dataLogger.setHeaders(csvHeaders);

            // # make a new thread
            thread = new Thread(this);
            thread.start();
        } else {
            throw new IllegalStateException("Logger is already started!");
        }
    }

    public String stopDataLog() throws IllegalStateException {
        if (logData) {
            logData = false;
            thread.interrupt();
            thread = null;
            return dataLogger.writeToFile();
        }else {
            throw new IllegalStateException("Logger is already stopped!");
        }
    }

    // called from Gyroscope activity
    public void setRotation(float[] rotation) {
        if(rotation != null) {
            synchronized (rotation) {
                this.rotation.clear();
                for (int i = 0; i < 3; i++) {
                    this.rotation.add(String.valueOf(rotation[i]));
                }
            }
        }



    }

    public void setAcceleration(float[] acceleration) {
        if(acceleration != null) {
            synchronized (acceleration) {
                this.acceleration.clear();
                for (int i = 0; i < 3; i++) {
                    this.acceleration.add(String.valueOf(acceleration[i]));
                }
            }
        }
    }

    public void setMagnetic(float[] magnetic) {
        if(magnetic != null) {
            synchronized (magnetic) {
                this.magnetic.clear();
                for (int i = 0; i < 3; i++) {
                    this.magnetic.add(String.valueOf(magnetic[i]));
                }
            }
        }
    }

//    public void setOrientation(float[] orientation) {
//        if(orientation != null) {
//            synchronized (orientation) {
//                this.orientation.clear();
//                for (int i = 0; i < 3; i++) {
//                    this.orientation.add(String.valueOf(orientation[i]));
//                }
//            }
//        }
//    }



    public void setLatiLong(float[] latilong) {
        if(latilong != null) {
            synchronized (latilong) {
                this.latilong.clear();
                for (int i = 0; i < 2; i++) {
                    this.latilong.add(String.valueOf(latilong[i]));
                }
            }
        }
    }



    private void logData() {
        csvValues.clear();
        csvValues.add(String.valueOf((System.currentTimeMillis() - logTime) / 1000.0f));

        synchronized (rotation) {
            csvValues.addAll(rotation);
        }

        synchronized (acceleration) {
            csvValues.addAll(acceleration);
        }

//        synchronized (orientation) {
//            csvValues.addAll(orientation);
//        }

        synchronized (magnetic) {
            csvValues.addAll(magnetic);
        }

        synchronized (latilong) {
            csvValues.addAll(latilong);
        }


        dataLogger.addRow(csvValues);
    }

    private File getFile(String filePath, String fileName) {
        File dir = new File(filePath);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        return new File(dir, fileName);
    }

    private String getFilePath() {
        return new StringBuilder().append(Environment.getExternalStorageDirectory()).append(File.separator).append
                (DEFAULT_APPLICATION_DIRECTORY).append(File.separator).toString();
    }

    private String getFileName() {
        Calendar c = Calendar.getInstance();

        return new StringBuilder().append(DEFAULT_APPLICATION_DIRECTORY).append(FILE_NAME_SEPARATOR)
                .append(c.get(Calendar.YEAR)).append(FILE_NAME_SEPARATOR).append(c.get(Calendar.MONTH) + 1).append
                        (FILE_NAME_SEPARATOR).
                        append(c.get(Calendar.DAY_OF_MONTH)).append(FILE_NAME_SEPARATOR).append(c.get(Calendar.HOUR))
                .append("-").append(c.get(Calendar.MINUTE)).append(FILE_NAME_SEPARATOR).append(c.get(Calendar.SECOND)
                ).append(".csv").toString();
    }

    private ArrayList<String> getCsvHeaders() {
        ArrayList<String> headers = new ArrayList<>();

        headers.add("Timestamp");
        headers.add("RO X");
        headers.add("RO Y");
        headers.add("RO Z");
        headers.add("AC X");
        headers.add("AC Y");
        headers.add("AC Z");
        headers.add("MG X");
        headers.add("MG Y");
        headers.add("MG Z");
        headers.add("Latitude");
        headers.add("Longitude");




        return headers;
    }
}
