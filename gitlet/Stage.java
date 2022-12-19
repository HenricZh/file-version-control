package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Stage class that handles files added and removed.
 * @author Henric Zhang
 */
public class Stage implements Serializable {

    /** Files staged for addition. */
    private LinkedHashMap<String, String> stagedForAdd;
    /** Files staged for removal. */
    private ArrayList<String> stagedForRem;

    /**
     * Construcotr for Stage class.
     */
    public Stage() {
        stagedForAdd = new LinkedHashMap<>();
        stagedForRem = new ArrayList<>();
    }

    /**
     * Make the current folder all empty.
     */
    public void empty() {
        stagedForAdd = new LinkedHashMap<>();
        stagedForRem = new ArrayList<>();
    }

    /**
     * Retrieve removed files.
     * @return The removed files.
     */
    public ArrayList<String> getRemovedFiles() {
        return stagedForRem;
    }

    /**
     * Check if everything is empty.
     * @return Whether everything is empty.
     */
    public boolean nothing() {
        return stagedForAdd.isEmpty() && stagedForRem.isEmpty();
    }

    /**
     * Add files to the folders.
     * @param fName File name to add.
     * @param sha1 SHA value to add.
     */
    public void add(String fName, String sha1) {
        stagedForAdd.put(fName, sha1);
    }

    /**
     * Returns the files that need to be added.
     * @return The files to add.
     */
    public LinkedHashMap<String, String> getAddedFiles() {
        return stagedForAdd;
    }

    /**
     * Files that needs to be removed.
     * @param fName File name of stuff to remove.
     */
    public void addToRemovedFiles(String fName) {
        stagedForRem.add(fName);
    }

    /**
     * Returns if file staged or not.
     * @param fName File name of stuff to remove.
     * @return Staged or no.
     */
    public boolean notStaged(String fName) {
        if (!stagedForRem.contains(fName)) {
            if (!stagedForAdd.containsValue(fName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns if staged for add.
     * @param fName The file.
     * @return Whetehr staged or not.
     */
    public boolean stagedForAdd(String fName) {
        if (stagedForAdd.containsValue(fName)) {
            return true;
        }
        return false;
    }

    /**
     * Returns if staged for remove.
     * @param fName The file.
     * @return Whetehr staged for remvoed or not.
     */
    public boolean notStageRM(String fName) {
        if (!stagedForRem.contains(fName)) {
            return true;
        }
        return false;
    }

}
