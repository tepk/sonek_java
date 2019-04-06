package com.sensor.app;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.components.Disk;
import com.profesorfalken.jsensors.model.components.Gpu;
import com.profesorfalken.jsensors.model.sensors.Fan;
import com.profesorfalken.jsensors.model.sensors.Load;
import com.profesorfalken.jsensors.model.sensors.Temperature;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class sensor {

    static Firestore db;
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        FileInputStream serviceAccount = new FileInputStream("assets\\serviceAccount.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://sonek-1500824321149.firebaseio.com")
                .build();
        FirebaseApp.initializeApp(options);
        db = FirestoreClient.getFirestore();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //System.out.println("Task running");
                readSensors();
            }
        },1000, 30*1000);
        //printUsage();
    }

    static void importFireBase() throws IOException, ExecutionException, InterruptedException {
        FileInputStream serviceAccount = new FileInputStream("assets\\serviceAccount.json");
        /*FirestoreOptions options =
                FirestoreOptions.newBuilder().setTimestampsInSnapshotsEnabled(true).build();
        Firestore firestore = options.getService();*/

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://sonek-1500824321149.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);

        Firestore db = FirestoreClient.getFirestore();

        // Create a Map to store the data we want to set
        Map<String, Object> docData = new HashMap<>();
        docData.put("Computername",namePC());
        docData.put("Processor"," ");
        docData.put("GPU", " ");
        docData.put("DISK", " ");
// Add a new document (asynchronously) in collection "cities" with id "LA"
        ApiFuture<WriteResult> future = db.collection("Computers").document(namePC()).set(docData);
// ...
// future.get() blocks on response
        System.out.println("Update time : " + future.get().getUpdateTime());
    }
    static void readSensors() {
        Components components = JSensors.get.components();
        Map<String, Object> result = new HashMap<String, Object>();
        List<Cpu> cpus = components.cpus;

        if (cpus != null) {
            List<Object> cpusList = new ArrayList<>();
            for (final Cpu cpu : cpus) {
                Map<String, Object> cpuMap = new HashMap<>();
                System.out.println("Found CPU component: " + cpu.name);
                if (cpu.sensors != null) {
                    System.out.println("Sensors: ");
                    Map<String, Object> cpusTempMap = new HashMap<>();

                    //Print temperatures
                    List<Temperature> temps = cpu.sensors.temperatures;
                    for (final Temperature temp : temps) {
                        System.out.println(temp.name + ": " + temp.value + " C");
                        cpusTempMap.put(temp.name, temp.value);
                    }

                    //Print fan speed
                    List<Fan> fans = cpu.sensors.fans;
                    for (final Fan fan : fans) {
                        System.out.println(fan.name + ": " + fan.value + " RPM");
                    }
                    cpuMap.put(cpu.name,cpusTempMap);
                    cpusList.add(cpuMap);
                }
            }
            result.put("cpus", cpusList);
        }
        List<Gpu> gpus = components.gpus;

        if (gpus != null) {
            List<Object> gpusList = new ArrayList<>();
            for (final Gpu gpu : gpus) {
                Map<String, Object> gpuMap = new HashMap<>();
                System.out.println("Found GPU component: " + gpu.name);
                if (gpu.sensors != null) {
                    System.out.println("Sensors: ");
                    Map<String, Object> gpusTempMap = new HashMap<>();
                    //Print temperatures
                    List<Temperature> temps = gpu.sensors.temperatures;
                    for (final Temperature temp : temps) {
                        System.out.println(temp.name + ": " + temp.value + " C");
                        gpusTempMap.put(temp.name, temp.value);
                    }

                    //Print fan speed
                    List<Fan> fans = gpu.sensors.fans;
                    for (final Fan fan : fans) {
                        System.out.println(fan.name + ": " + fan.value + " RPM");
                    }
                    gpuMap.put(gpu.name,gpusTempMap);
                    gpusList.add(gpuMap);
                }
            }
            result.put("gpus", gpusList);
        }
        List<Disk> disks = components.disks;

        if (disks != null) {
            List<Object> disksList = new ArrayList<>();
            for (final Disk disk : disks) {
                Map<String, Object> diskMap = new HashMap<>();
                System.out.println("Found DISK component: " + disk.name);
                if (disk.sensors != null) {
                    System.out.println("Sensors: ");
                    Map<String, Object> disksTempMap = new HashMap<>();
                    //Print temperatures
                    List<Temperature> temps = disk.sensors.temperatures;
                    for (final Temperature temp : temps) {
                        System.out.println(temp.name + ": " + temp.value + " C");
                        disksTempMap.put(temp.name, temp.value);
                    }

                    //Print fan speed
                    List<Fan> fans = disk.sensors.fans;
                    for (final Fan fan : fans) {
                        System.out.println(fan.name + ": " + fan.value + " RPM");
                    }
                    diskMap.put(disk.name,disksTempMap);
                    disksList.add(diskMap);
                }
            }
            result.put("disks", disksList);
        }
        ApiFuture<WriteResult> future = db.collection("Computers").document(namePC()).set(result);
    }
     static String namePC() {
            String hostname = "Unknown";

        try
        {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
        }
        catch (UnknownHostException ex)
        {
            System.out.println("Hostname can not be resolved");}
        //System.out.println(hostname);
        return hostname;
    }

    }
