package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;

import java.io.IOException;

public class Main
{
    public static void main(String[] args)
    {
        try {
            Input input = JsonInputReader.getInputFromJson(args[0]);
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
}