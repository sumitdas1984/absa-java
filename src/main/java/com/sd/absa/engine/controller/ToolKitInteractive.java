package com.sd.absa.engine.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by sumitd on 1/19/16.
 */
public class ToolKitInteractive {

    public static void runInteractiveMode() {

        while (true) {

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                System.out.println("===========================================================================================");
                System.out.print("Enter Text:\n");
                String input = br.readLine();
                double startTime = System.currentTimeMillis();

                if (input == null) {
                    continue;
                }
                String text = input;

                System.out.println("\n" + text);
                System.out.println(System.currentTimeMillis() - startTime + " ms");
                System.out.println("===========================================================================================");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String [] args) throws Exception {

        runInteractiveMode();
    }
}
