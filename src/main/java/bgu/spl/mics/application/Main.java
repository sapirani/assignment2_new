package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;

public class Main
{
    public static void main(String[] args)
    {
        try
        {
            Input input = JsonInputReader.getInputFromJson(args[0]); // Read the input file and create Input object
            simulate(input); // Start the application
            writeOutput(args[1]); // Write the information to the output file

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * The function starts the application
     * @param input = the input object that was created from the input file data
     * Output:
     *        none
     */
    private static void simulate(Input input)
    {
        final int NUMBER_OF_MICROSERVICES = 5;

        Ewoks.getInstance().loadEwoks(input.getEwoks()); // Load ewoks

        // Initialize MicroServices
        MicroService Leia = new LeiaMicroservice(input.getAttacks());
        MicroService Hansolo  = new HanSoloMicroservice();
        MicroService C3PO  = new C3POMicroservice();
        MicroService R2D2  = new R2D2Microservice(input.getR2D2());
        MicroService Lando  = new LandoMicroservice(input.getLando());

        // Initialize thread for each microservice
        Thread LeiaThread = new Thread(Leia);
        Thread HansoloThread = new Thread(Hansolo);
        Thread C3POThread = new Thread(C3PO);
        Thread R2D2Thread = new Thread(R2D2);
        Thread LandoThread = new Thread(Lando);

        // Update number of threads in the Latch object
        LatchSingleton.getInstance().setLatch(NUMBER_OF_MICROSERVICES);

        // Start the application by start the threads, causing them to start the run function
        LeiaThread.start();
        HansoloThread.start();
        C3POThread.start();
        R2D2Thread.start();
        LandoThread.start();

        try
        {
            LeiaThread.join();
            HansoloThread.join();
            C3POThread.join();
            R2D2Thread.join();
            LandoThread.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Writing the results of the application to an output file.
     * @param path = the path of the output file
     * Output:
     *        none.
     */
    private static void writeOutput(String path)
    {
        try
        {
            // Create json file
            Gson gson = new Gson();
            FileWriter myWriter = new FileWriter(path);

            // Convert the Diary instance to json format and write it to the file
            gson.toJson(Diary.getInstance(), myWriter);
            myWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}