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
                System.out.println("Attack");
                System.out.println(a.duration);
                System.out.println(a.serials);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}