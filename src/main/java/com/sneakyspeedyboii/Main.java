package com.sneakyspeedyboii;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.manager.windows.powershell.PowerShellOperations;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.sensors.Temperature;
import com.profesorfalken.jsensors.util.OSDetector;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws MalformedURLException, URISyntaxException, InterruptedException {
        WebSocketClient websoc = new WebSocketClient(new URI( "ws://127.0.0.1:7887" )) {
            @Override
            public void onMessage( String message ) {
                System.out.println( "received: " + message );
            }

            @Override
            public void onOpen( ServerHandshake handshake ) {
                System.out.println( "opened connection" );
            }

            @Override
            public void onClose( int code, String reason, boolean remote ) {
                System.out.println( "closed connection" );
                System.exit(0);
            }

            @Override
            public void onError( Exception ex ) {
                ex.printStackTrace();
            }

        };

        if (!OSDetector.isWindows()) {
            System.out.println("You are not running in windows, exiting!");
            System.exit(-1);
        }

        if (!PowerShellOperations.isAdministrator()) {
            System.out.println("You do not have administrator permissions, exiting!");
            System.exit(-1);
        }

        websoc.connect();
        Thread.sleep(1000);
        while (true) {
            ArrayList<Double> temperatures = new ArrayList<>();

            for (Cpu cpu : JSensors.get.components().cpus) {
                List<Temperature> temps = cpu.sensors.temperatures;

                for (Temperature temp : temps) {
                    System.out.println("Data Map Temp: " + temp.name + temp.value + ", ");
                    temperatures.add(temp.value);
                }
            }
            websoc.send(String.valueOf(temperatures.toString()));
            System.out.println("Sent");
            Thread.sleep(1000);
        }
    }
}