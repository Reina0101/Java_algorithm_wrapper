Weka Java wrapper

In order to run the program Java is required, preferably the latest version of Java.
The entire application is made with the JVM of 16. Anything higher is not recommended in order to prevent errors

The end result is a classification of the unknown instance, which is healthy or sick (examples below).

An instance and the matching data can be given via the commandline. The instance needs to have multiple arguments:
-t3 is the expression value of the T3 hormone
-t4 is the expression value of the TT4 hormone
-fti is the free TT4 index
-tsh which is the thyroid stimulating hormone
-a is the age of the instance
-r is the referral source for the instance
-o if the instance uses medication called thyroxine
-m3 is the measured t3, which is proven via the test

Examples
Healthy example:
-t3 1 -t4 92 -fti 112 -tsh 0 -a 10 -r SVI -o f -m3 t

Sick example:
-t3 1 -t4 92 -fti 112 -tsh 0 -a 10 -r SVI -o f -m3 f