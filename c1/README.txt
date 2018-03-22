Rudhra Raveendran
rooday@bu.edu
U55156224
CS 350
C1

1. How to Run
For each part, enter the respective subdirectory and run javac *.java to compile.
Then run java Controller to run the simulation. 

2. Dependencies
None, only the files in each subdirectory are needed.

3. Explanation of Code
This code is based on the HW3 Code provided on Piazza resources.
For the most part the code is the same, except for the addition of the Process class.
Process keeps track of processes as they move throughout the system by keeping a history of requests.
There is also the MM2 class, which extends the MM1 class, and is used to simulate the 2 core CPU system.
This class overrides certain functions from MM1 to work in a MM2 system.
Further changes are described in code comments.