package wekawrapper;

import jdk.swing.interop.SwingInterOpUtils;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import java.util.ArrayList;
import java.util.List;

public class AlgorithmEngine {
    /**
     * The method makeInstance will make an instance. Which sounds easy, but working with data from the command line,
     * attribute labels need to be generated and match the input. A lot can go wrong, which is why the CLI options are
     * already validated.
     *
     * First the columns/attributes are labeled, for each string value a column needs to have options.
     * The instance type ONLY accepts numeric values.
     * So for example, {NO| YES} with a given 1 is the YES value.
     * The numeric values can be directly inserted under the correct instance column
     * @param optionsProvider is needed for the getters of the CLI options class, in order to grab the parsed
     *                        commandline options
     */
    void makeInstance(CLIOptions optionsProvider) {
        ArrayList<Attribute> attributes = new ArrayList<>(30);
        ArrayList<String> classLabels = new ArrayList<>();

        // Sick options
        // These labels will be used in the end result of the algorithm
        classLabels.add("Healthy");
        classLabels.add("Sick");

        // True or False options
        // The CLI parser will give the numbers which will match the False (f) and True (t) options
        List<String> DEFAULTTrueFalse = new ArrayList<>(2);
        DEFAULTTrueFalse.add("f");
        DEFAULTTrueFalse.add("t");

        // Referral sources options
        // CLI will give a number and the matching number will be picked from the attribute possibilities
        List<String> DEFAULTReferralSource = new ArrayList<>(6);
        DEFAULTReferralSource.add("SVHC");
        DEFAULTReferralSource.add("other");
        DEFAULTReferralSource.add("SVI");
        DEFAULTReferralSource.add("STMW");
        DEFAULTReferralSource.add("SVHD");
        DEFAULTReferralSource.add("WEST");

        // Labeling attributes
        // Unfortunately the model requires all the columns in order to predict correctly
        attributes.add(0, new Attribute("age"));
        attributes.add(1, new Attribute("sex"));
        attributes.add(2, new Attribute("on_thyroxine", DEFAULTTrueFalse));
        attributes.add(3, new Attribute("query_on_thyroxine", DEFAULTTrueFalse));
        attributes.add(4, new Attribute("on_antithyroid_medication", DEFAULTTrueFalse));
        attributes.add(5, new Attribute("sick", DEFAULTTrueFalse));
        attributes.add(6, new Attribute("pregnant", DEFAULTTrueFalse));
        attributes.add(7, new Attribute("thyroid_surgery", DEFAULTTrueFalse));
        attributes.add(8, new Attribute("I131_treatment", DEFAULTTrueFalse));
        attributes.add(9, new Attribute("query_hypothyroid", DEFAULTTrueFalse));
        attributes.add(10, new Attribute("query_hyperthyroid", DEFAULTTrueFalse));
        attributes.add(11, new Attribute("lithium", DEFAULTTrueFalse));
        attributes.add(12, new Attribute("goitre", DEFAULTTrueFalse));
        attributes.add(13, new Attribute("tumor", DEFAULTTrueFalse));
        attributes.add(14, new Attribute("hypopituitary", DEFAULTTrueFalse));
        attributes.add(15, new Attribute("psych", DEFAULTTrueFalse));
        attributes.add(16, new Attribute("TSH_measured", DEFAULTTrueFalse));
        attributes.add(17, new Attribute("TSH"));
        attributes.add(18, new Attribute("T3_measured", DEFAULTTrueFalse));
        attributes.add(19, new Attribute("T3"));
        attributes.add(20, new Attribute("TT4_measured", DEFAULTTrueFalse));
        attributes.add(21, new Attribute("TT4"));
        attributes.add(22, new Attribute("T4U_measured"));
        attributes.add(23, new Attribute("T4U"));
        attributes.add(24, new Attribute("FTI_measured"));
        attributes.add(25, new Attribute("FTI"));
        attributes.add(26, new Attribute("TBG_measured"));
        attributes.add(27, new Attribute("TBG"));
        attributes.add(28, new Attribute("referral_source", DEFAULTReferralSource));
        attributes.add(29, new Attribute("Class",classLabels));

        // Generating label structure
        Instances dataRaw = new Instances("sick",attributes,0);

        // Making instance type and getting commandline data
        Instance inst = new DenseInstance(30);
        inst.setValue(0, optionsProvider.getAge());
        inst.setValue(2, optionsProvider.getOn_thyroxine());
        inst.setValue(17, optionsProvider.getTSH());
        inst.setValue(18, optionsProvider.getT3_measured());
        inst.setValue(19, optionsProvider.getT3());
        inst.setValue(21, optionsProvider.getTT4());
        inst.setValue(25, optionsProvider.getFTI());

        // String values will receive numbers, which match the options in the attribute
        inst.setValue(28, optionsProvider.getReferral_source());

        dataRaw.setClassIndex(dataRaw.numAttributes() - 1);
        dataRaw.add(inst);

        // Starts generate classifier function
        generateClassifier(dataRaw);
    }

    /**
     * With the command line instance, the dataset "dataRaw" has been made.
     * First the model is loaded in order to start the classification process,
     * then the new instance is classified with the model and will be shown to the user as end result
     *
     * @param dataRaw The data is called raw since it hasn't been classified yet.
     *                only the matching columns that the algorithm needs are filled,
     *                all the others are needed for the model
     */
    private void generateClassifier(Instances dataRaw) {
        try {
            // Reading/readying the model made with J48 in Weka
            Classifier cls = (Classifier) weka.core.SerializationHelper.read("data/model.model");

            // Using the working classifier to predict the made Instance (type) class
            // index is always 0 because not more than 1 instance can be run through the command line
            double value = cls.classifyInstance(dataRaw.instance(0));

            // Using the labels of the data the user
            String prediction = dataRaw.classAttribute().value((int)value);

            // Output to the user if their commandline patient is sick or healthy
            System.out.println("VERDICT: The giving instance is " + prediction); //+ dataRaw.classAttribute().toString());

        } catch (Exception error) {
            // If something happens to the algorithm while classifying, which isn't found while paring
            // this error will occur, which is a rare situation but prevented
            System.err.println("Something went wrong with the algorithm!" + error.getMessage());
        }
    }

}
