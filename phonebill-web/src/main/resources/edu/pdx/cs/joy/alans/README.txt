README for Project4
Alan Shirk

usage: java -jar target/phonebill-client.jar [options] <args>
    args are (in this order):
        customer Person whose phone bill we’re modeling
        callerNumber Phone number of caller
        calleeNumber Phone number of person who was called
        begin Date and time call began
        end Date and time call ended
    options are (options may appear in any order):
        -host hostname Host computer on which the server runs
        -port port Port on which the server is listening
        -search Search for phone calls
        -print Prints a description of the new phone call
        -README Prints a README for this project and exits

This program is a PhoneBill REST API that can be invoked from the command line.

Its features include:
Printing usage information and the README.
Interacting with a server to manage phone bills.
Adding phone calls to a customer's phone bill.
Searching for phone calls between specific dates.
Pretty printing the entire phone bill or filtered calls based on search criteria.