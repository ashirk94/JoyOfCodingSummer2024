README for Project3
Alan Shirk

usage: java -jar target/phonebill-1.0.0.jar [options] <args>
    args are (in this order):
        customer Person whose phone bill we’re modeling
        callerNumber Phone number of caller
        calleeNumber Phone number of person who was called
        begin Date and time (am/pm) call began
        end Date and time (am/pm) call ended
    options are (options may appear in any order):
        -pretty file Pretty print the phone bill to a text file
            or standard out (file -)
        -textFile file Where to read/write the phone bill
        -print Prints a description of the new phone call
        -README Prints a README for this project and exits

This program builds on the functionality of projects 1 and 2, with the following features:

Printing usage information and the README.
Printing a phone call to the command line.
Saving a phone bill to a txt file.
Reading a phone bill from a txt file.
Pretty printing phone bill information to a file or to standard out.
Sorting phone calls by their start times. If the start times are the same,
calls are sorted by the caller's phone number.