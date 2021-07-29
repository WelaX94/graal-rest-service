package TEMPLATE;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Test2 {
    public static void main(String[] args) {
        PrintStream old = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);
        System.out.println("Foofoofoo!");
        System.out.flush();

        System.setOut(old);

        System.out.println("Here: " + baos.toString());

    }
}
