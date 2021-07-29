package TEMPLATE;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.IOException;

public class Test {
    public static final String BENCHFILE = "src/bench.js";
    public static final String SOURCE = ""
                                + "function TestFunction() {\n"
                                + "    var x = 10;\n"
                                + "    var y = 20;\n"
                                + "    var z = x + y;\n"
                                + "    console.log('GraalJS: The calculation is almost complete');\n"
                                + "    return z;\n"
                                + "}\n";


    public static void main(String[] args) {

        try (Context context = Context.create()) {
            context.eval("js", "console.log('GraalJS: First message');");
            context.eval(Source.newBuilder("js", SOURCE, BENCHFILE).build());
            Value testFunction = context.getBindings("js").getMember("TestFunction").execute();
            int a = testFunction.asInt();
            System.out.println("Totally: " + a);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try (Context context = Context.create()) {
//            context.eval(Source.newBuilder("js", SOURCE, BENCHFILE).build());
//            Value testFunction = context.getBindings("js").getMember("TestFunction");
//            testFunction.execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}
