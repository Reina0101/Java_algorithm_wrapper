package wekawrapper;

import org.apache.commons.cli.*;
import java.util.ArrayList;
import java.util.List;

public class CLIOptions implements Options {
    // Setup CLI
    private org.apache.commons.cli.Options options;
    private CommandLine cmd;
    
    // Variables for the algorithm
    private double T3;
    private int TT4;
    private int FTI;
    private double TSH;
    private int age;
    private int referral_source;
    private int on_thyroxine;
    private int T3_measured;

    public List<String> allowedReferralSource = new ArrayList<>();

    /**
     * Constructor for the argument parser
     * Also fills up the allowed referral sources which is needed to check
     * */
    public CLIOptions (String[] args) {
        // Required to check if the given argument matches the
        allowedReferralSource.add("SVHC");
        allowedReferralSource.add("other");
        allowedReferralSource.add("SVI");
        allowedReferralSource.add("STMW");
        allowedReferralSource.add("SVHD");
        allowedReferralSource.add("WEST");

        initializeCLI();
        parseArgs(args);
    }

    /**
     * Init initializes the command line options that are needed for the algorithm to work via the commandline
     * All the values are needed for the algorithm to classify the data.
     * */
    private void initializeCLI() {
        this.options = new org.apache.commons.cli.Options();
        options.addOption(new Option("t3",
                "T3",
                true,
                "The T3 - Hormone expression value for the T3 hormone"));

        options.addOption(new Option("t4",
                "TT4",
                true,
                "The TT4 - Hormone expression value for the TT4 hormone"));

        options.addOption(new Option("fti",
                "FTI",
                true,
                "The fti - Hormone expression value for the free TT4 index"));

        options.addOption(new Option("tsh",
                "TSH",
                true,
                "The TSH - Hormone expression value for thyroid stimulating hormone"));

        options.addOption(new Option("a",
                "age",
                true,
                "The age - The age of the person in years"));

        options.addOption(new Option("r",
                "referralsource",
                true,
                "The Referral source - The external factors surrounding the person"));

        options.addOption(new Option("o",
                "on_thyroxine",
                true,
                "The thyroxine usage - Boolean if the person uses thyroid medication: thyroxine," +
                        "this can be 1, t, T or 0, f, F"));

        options.addOption(new Option("m3",
                "measuredT3",
                true,
                "The measured T3 - if there is T3 measured by the person" +
                        "this can be 1, t, T or 0, f, F"));

    }


    /**
     * parse arguments prepares the variables to be filled and be used by the engine of the program
     * Has a try because Parse exception is a checked error and needs to be surrounded by a try loop in order
     * to not break everything. If something goes wrong, it prints the help file.
     * @param args needed because the command line has the values and information
     */
    private void parseArgs(String[] args) {
        CommandLineParser parser = new DefaultParser();
        try {
            this.cmd = parser.parse( options, args);
            verifyArgs(); // throws a parse exception and is handled here
        } catch (ParseException error) {
            System.err.println("Something went wrong while parsing!\nCause: " + error.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "java -jar MyApp.jar [options]", options );
        }
    }

    /**
     * Verifies the given arguments, which could cause a lot of errors, since there are a lot of variables,
     * this is one of the bigger methods. But preventing errors is better than bad working software.
     * @throws ParseException It can happen in certain scenario's that the option is not provided,
     * this may not crash the entire program
     */
    private void verifyArgs() throws ParseException {
        // T3 needs to be present and not negative, 0 is tolerated!
        if (cmd.hasOption("t3")){
            try {
                final double tmp_T3 = Double.parseDouble(cmd.getOptionValue("t3"));
                if (tmp_T3 >= 0) {
                    this.T3 = tmp_T3;
                } else {throw new ParseException("T3 is negative and impossible!");}
            } catch (NumberFormatException error) {
                throw new ParseException("An invalid number has been provided!");}
        } else {throw new ParseException("No T3 value provided!");}

        // TT4 needs to be present and not negative, 0 is also tolerated
        if (cmd.hasOption("t4")){
            try {
                final int tmp_tt4 = Integer.parseInt(cmd.getOptionValue("t4"));
                if (tmp_tt4 >= 0) {
                    this.TT4 = tmp_tt4;
                } else {throw new ParseException("TT4 is negative and impossible!");}
            } catch (NumberFormatException error) {
                throw new ParseException("An invalid number has been provided!");}
        } else {throw new ParseException("There is no TT4 provided!");}

        // FTI needs to be present and not negative, 0 is also tolerated
        if (cmd.hasOption("fti")){
            try {
                final int tmp_fti = Integer.parseInt(cmd.getOptionValue("fti"));
                if (tmp_fti >= 0) {
                    this.FTI = tmp_fti;
                } else {throw new ParseException("FTI is negative and impossible!");}
            } catch (NumberFormatException error) {
                throw new ParseException("An invalid number has been provided!");}
        } else {throw new ParseException("There is no FTI provided!");}

        // TSH needs to be present and not negative also 0 is tolerated
        if (cmd.hasOption("tsh")){
            try {
                final double tmp_tsh = Double.parseDouble(cmd.getOptionValue("tsh"));
                if (tmp_tsh >= 0) {
                    this.TSH = tmp_tsh;
                } else {throw new ParseException("TSH is negative and impossible!");}
            } catch (NumberFormatException error) {
                throw new ParseException("An invalid number has been provided!");}
        } else {throw new ParseException("There is no TSH provided!");}

        // Age needs to be present and has an extra requirement of not being above 120 and not 0
        if (cmd.hasOption("a")){
            try {
                final int tmp_age = Integer.parseInt(cmd.getOptionValue("a"));
                if (tmp_age > 0 && tmp_age < 120) {
                    this.age = tmp_age;
                } else {throw new ParseException("An impossible age has been given!");}
            } catch (NumberFormatException error) {
                throw new ParseException("An invalid number has been provided!");}
        } else {throw new ParseException("There is no age provided!");}

        // Referral resource is a string that needs to match a certain type
        // Uses a list of "allowed" options, if the user input is in the list, it grabs the index.
        // For the instance type, it allows for only numbers, so the attribute labeling matches the list
        if (cmd.hasOption("r")){
            final String tmp_referral_source = cmd.getOptionValue("r"); //.toUpperCase();
                if (allowedReferralSource.contains(tmp_referral_source)) {
                    this.referral_source = allowedReferralSource.indexOf(tmp_referral_source);
                } else {throw new ParseException("A wrong referral source has been given!");}
        } else {throw new ParseException("There is no referral source provided!");}

        // Thyroxine user boolean value if the instance uses it
        if (cmd.hasOption("o")){
            try {
                final int tmp_on_thyroxine = Integer.parseInt(cmd.getOptionValue("o"));

                if (tmp_on_thyroxine == 1) {
                    this.on_thyroxine = 1; // True option
                } else if (tmp_on_thyroxine == 0) {
                    this.on_thyroxine = 0; // False option
                } else {
                    throw new ParseException("Thyroxine usage has not been included correctly!");
                }
            } catch (NumberFormatException error) {
                final String tmp_on_thyroxine = cmd.getOptionValue("o");
                if (tmp_on_thyroxine.equals("T") || tmp_on_thyroxine.equals("t")) {
                    this.on_thyroxine = 1; // True option
                }
                else if (tmp_on_thyroxine.equals("F") || tmp_on_thyroxine.equals("f")) {
                    this.on_thyroxine = 0; // False option
                }
                else {
                    throw new ParseException("Thyroxine usage has not been included correctly!");
                }
            }
        } else {throw new ParseException("Thyroxine usage has not been included!");}

        // If there is T3 measured with the new instance
        if (cmd.hasOption("m3")) {
            try {
            final int tmp_T3_measured = Integer.parseInt(cmd.getOptionValue("m3"));
            if (tmp_T3_measured == 1) {
                this.T3_measured = 1; // True option
            } else if (tmp_T3_measured == 0) {
                this.T3_measured = 0; // False option
            } else {
                throw new ParseException("If T3 is measured has not been included!");
            }
        } catch (NumberFormatException error) {
                final String tmp_T3_measured = cmd.getOptionValue("m3");
                if (tmp_T3_measured.equals("T") || tmp_T3_measured.equals("t")) {
                    this.T3_measured = 1; // True option
                }
                else if (tmp_T3_measured.equals("F") || tmp_T3_measured.equals("f")) {
                    this.T3_measured = 0; // False option
                }
                else {
                    throw new ParseException("Thyroxine usage has not been included correctly!");
                }
            }
        } else {throw new ParseException("Measured T3 has not been included!!");}
    }


    @Override
    public double getT3() {
        return T3;
    }

    @Override
    public int getTT4() {
        return TT4;
    }

    @Override
    public int getFTI() {
        return FTI;
    }

    @Override
    public double getTSH() {
        return TSH;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public int getReferral_source() {
        return referral_source;
    }

    @Override
    public int getOn_thyroxine() {
        return on_thyroxine;
    }

    @Override
    public int getT3_measured() {
        return T3_measured;
    }
}
