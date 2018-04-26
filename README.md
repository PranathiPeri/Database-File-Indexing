# Database-File-Indexing-
Implemented B+ Tree structure for a file with data records containing a key and other attribute. Used Metadata and indices to retrieve a specified value from the original file. Implemented using Java, MySQL. 




1. To run the program,first you need to have jdk installed on your computer and all the environment varibles set properly.

2. The extracted project file structure should be "Desktop\BPTree\dbassignment" 

3. In the folder open command prompt and compile the java files using the command  javac *.java

4. Set the directory of the command prompt to the directory where the BPtree folder is present.

4. To create index file  type in the following command.

java -classpath "BPTree" dbassignment.Main -create ":\Users\This PC\Desktop\BPTree\dbassignment\CS6360Asg5TestData.txt" CS6360Asg5.indx 15

5. To find a record type in the following command.

java -classpath "BPTree" dbassignment.Main -find CS6360Asg5.indx 93288157045562A


6. To insert a record into existing index file type in the following command

java -classpath "BPTree" dbassignment.Main -insert CS6360Asg5.indx  "12222222222222C test data I added"

7. To display a set of recording starting from certain record type in the following command.

java -classpath "BPTree" dbassignment.Main -list CS6360Asg5.indx 38417813544394A 12


