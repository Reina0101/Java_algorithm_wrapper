package wekawrapper;

import java.util.Arrays;

public class MainOptions {
    /**
     * First the arguments will be parsed via CLI, once they are valid, they are saved under the
     * optionsProvider element.
     *
     * This element is given to the second file, AlgorithmEngine, which is needed because of the "getters".
     * The getters are already filled by CLIOptions and usable there.
     * The engine will also generate the ending result which is shown to the user
     * @param args users' command line data that has been given by the user
     */
    public static void main(String[] args) {
        System.out.println("The complete set of arguments given is " + Arrays.toString(args));

        // First file with the task of parsing all the elements
        CLIOptions optionsProvider = new CLIOptions(args);

        // The second file which is tasked with connecting to the algorithm model and predicting the classifier
        AlgorithmEngine engine = new AlgorithmEngine();
        engine.makeInstance(optionsProvider);
    }
}
