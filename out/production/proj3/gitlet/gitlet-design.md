# Gitlet Design Document

**Name**: Henric Zhang

## Classes and Data Structures
* Commit Class
  * Variables are String hashCode,
    String parentHashCode,
    String message,
    String datetime, and 
     HashMap<String, String> blobs. 
  * This class reperesents each and every commit that is added to the tree and each commit will hold blobs and meta data like time and author. This is similar to one part of a linked list except that it has a lot more data.
* Repo Class
  * String HEAD = "master",
  Stageing stage, and
  File workingDir are the variables
  * The point of the repo class is to represent each repository that hold all the commit history of the files inside the repo.
* Staging Class
  * The variables will be HashMap<String, String> toAdd
and ArrayList<String> removed. This will represent the files that will need to be added and the commit class will retrieve data to change the past commits.
* Main Class
  * No Data strucutres. This class will simply handle inputs. Will use a switch statement to parse through specific commands and if commands exist, call functions that impact files using Commit or Repo class.
  
## Algorithms
* Commit Class
  * The commit class will first keep track of meta data like time and message. These will be stored with Strings. The commit will also have important data like its SHA-1 and the SHA-1 of its parents. These will be stored in strings as well. A hashmap will be used to store blobs of changed code and the rest of the methods of the commit class will be used to alter the files in the current directory while creating a whole new commit. The old commits will be copied and saved. For example, I will have a merge functino to handle merging.
* Repo Class
  * The repo class has init, commit, rm, and global functions. The init class will initialize a Repo class to represent the input. The repo class will keep track of the "head"  of the commit tree using a string value while keeping track of the items that were recently added to the staging area. The staging area is represented by the Stage class. There will also be a file keeping track of the CWD.
* Staging Class
  * The staging class will hold information on what will be added and removed form the current commit head in the current repo. I will use a hash map and a arraylist to track these. The hash map will hold sha-1 values of items while the arraylist will track the string values of what will be removed.
* Main Class
  * The only algorithm in the Main class involves using a swithc statement detecting different commmands. Inside each command, the code will also check for a specific amount of parameters because it doesn't make sense to run code if there is not sufficient information to do so.

## Persistence
* Repo Class
  * All persistence will be handeled through the repo class. The repo will be initialized when the Main class recieves "init" as a command. After doing so, I will use File to store the current cwd and will also remember all the locations of the commits, blobs, and branches. Inside the .gitlet folder, there will be more files like blobs, brnahces, commits, and staging to keep track of changes and past history of each change.
