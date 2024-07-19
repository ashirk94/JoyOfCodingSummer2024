README for Project1
Alan Shirk

This program extends the classes AbstractPhoneBill and AbstractPhoneCall from
the edu.pdx.cs.joy package.

The program implements the PhoneCall class which takes in arguments received from the command line.
These arguments passed to the constructor are caller, callee, beginTime, and endTime. It also implements the
abstract getter methods of AbstractPhoneCall. This allows the PhoneCall data to be easily extracted.

The program also implements the PhoneBill class which has a collection of PhoneCall objects. These objects store the customer
String that is passed in as the first argument, and adds the PhoneCall data to an ArrayList of PhoneCalls made by
the customer.

Lastly the program implements the Project1 class which contains the primary logic for the program inside the main method. It
also contains a method that prints this README. That method is invoked with the -README flag, otherwise the program
adds a PhoneCall to a PhoneBill for a particular customer, and optionally prints the PhoneCall data if the -print flag
is provided. It also involves boundary checking and error handling for the command line arguments.
