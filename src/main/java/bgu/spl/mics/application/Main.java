package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;

import java.io.IOException;

public class Main
{
    public static void main(String[] args)
    {
        try {
            Input input = JsonInputReader.getInputFromJson(args[0]);
            simulate(input);
            writeOutput();


            System.out.println(input.getEwoks());
            System.out.println(input.getLando());
            System.out.println(input.getR2D2());
            for (Attack a: input.getAttacks()) {
                System.out.println("passiveObjects.Attack");
                System.out.println(a.getDuration());
                System.out.println(a.getSerials());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void simulate(Input input)
    {
        Ewoks.getInstance().loadEwoks(input.getEwoks()); // load ewoks
        MicroService Leia = new LeiaMicroservice(input.getAttacks());
        MicroService Hansolo  = new HanSoloMicroservice();
        MicroService C3PO  = new C3POMicroservice();
        MicroService R2D2  = new R2D2Microservice(input.getR2D2());
        MicroService Lando  = new LandoMicroservice(input.getLando());

        Thread LeiaThread = new Thread(Leia);
        Thread HansoloThread = new Thread(Hansolo);
        Thread C3POThread = new Thread(C3PO);
        Thread R2D2Thread = new Thread(R2D2);
        Thread LandoThread = new Thread(Lando);

        LeiaThread.start();
        HansoloThread.start();
        C3POThread.start();
        R2D2Thread.start();
        LandoThread.start();
    }

    private static void writeOutput()
    {
        Diary.getInstance().executionOutput();
    }

}