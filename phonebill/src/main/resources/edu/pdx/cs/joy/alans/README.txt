README for Project2
Alan Shirk

usage: java -jar target/phonebill-1.0.0.jar [options] <args>
    args are (in this order):
        customer        The person whose phone bill we’re modeling
        callerNumber    Phone number of the caller (format: nnn-nnn-nnnn)
        calleeNumber    Phone number of the person who was called (format: nnn-nnn-nnnn)
        begin           Date and time the call began (format: mm/dd/yyyy hh:mm)
        end             Date and time the call ended (format: mm/dd/yyyy hh:mm)
    options are (options may appear in any order):
        -textFile file  Specifies the file to read/write the phone bill
        -print          Prints a description of the new phone call
        -README         Prints a README for this project and exits

The program can read a PhoneBill from a text file or write a new one. The text file can be specified using the -textFile option. If the file does not exist, the program creates a new one. If the customer name from the command line does not match the one in the file, an error is reported, ensuring data consistency.

The TextParser class is responsible for reading a PhoneBill from a text file. It parses the file line by line, extracts customer and phone call details, and constructs the corresponding PhoneBill object. Key features include:

-Parsing the customer name and phone call details from a file
-Handling and reporting parsing errors, such as malformed data

The TextDumper class is used to write the contents of a PhoneBill to a text file. It outputs the customer name and each phone call's details in a structured format. Key functionalities include:

-Writing the customer name and phone call details to a file
-Ensuring the data is written in a consistent and readable format

The program includes robust error handling for various scenarios, including:

-Missing or extraneous command line arguments
-Invalid date/time formats
-Unknown command line options
-Mismatched customer names between the command line and the file