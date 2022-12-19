package gitlet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * My Commit class that represents one instance of the revision history.
 *
 * @author Henric Zhang
 **/
@SuppressWarnings("unchecked")
public class Commit implements Serializable {

    /** Commit Id. */
    private String comId;
    /** Parent one ID. */
    private String parentOneID;
    /** Parent two ID. */
    private String parentTwoID;
    /** Commit message. */
    private String message;
    /** Commit Time. */
    private String commitTime;
    /** Blobs in the hash. */
    private LinkedHashMap<String, String> blobs;

    /**
     * Commit construcotr.
     * @param m Passed in message.
     * @param b Passed in blobs.
     * @param f Passed in parents.
     * @param p Passed in parents.
     */
    public Commit(String m, LinkedHashMap b, String f, String p) {
        message = m;
        blobs = b;
        parentOneID = f;
        parentTwoID = p;
        LocalDateTime current = LocalDateTime.now();
        String s = "EEE MMM dd HH:mm:ss yyyy";
        DateTimeFormatter d = DateTimeFormatter.ofPattern(s);
        commitTime = current.format(d) + " -0800";
        byte[] commitObj = Utils.serialize(this);
        comId =  Utils.sha1(commitObj);
    }

    /**
     * Commit constructor twoo.
     * @param me Passed in message.
     */
    public Commit(String me) {
        message = me;
        blobs = new LinkedHashMap<String, String>();
        parentOneID = null;
        parentTwoID = null;
        commitTime = "Wed Dec 31 16:00:00 1969 -0800";
        byte[] commitObj = Utils.serialize(this);
        comId = Utils.sha1(commitObj);
    }

    /**
     * Returns ID.
     * @return The Id.
     */
    public String getID() {
        return comId;
    }

    /**
     * Returns parents ID.
     * @return Parent One ID.
     */
    public String getParentOneID() {
        return parentOneID;
    }

    /**
     * Returns parent Two ID.
     * @return Parent Two ID.
     */
    public String getParentTwoID() {
        return parentTwoID;
    }

    /**
     * Returns the commit message.
     * @return The commit message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the time.
     * @return The time.
     */
    public String getTime() {
        return commitTime;
    }

    /**
     * Stores references to txt files.
     * First string is text file name.
     * Second is SHA-1 value
     * @return The blobs.
     */
    public LinkedHashMap<String, String> getBlob() {
        return blobs;
    }

}
