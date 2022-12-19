package gitlet;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Queue;
import java.util.Collections;

/**
 * Repostiory representaiton.
 *
 * @author henric zhang
 */
@SuppressWarnings("unchecked")
public class Repository {

    /**
     * remote repos.
     */
    private LinkedHashMap<String, String> remote;
    /**
     * The head SHA.
     */
    private String head = "";
    /**
     * The currBran SHa.
     */
    private String currBran = "";
    /**
     * THe current stage.
     */
    private Stage stage = null;
    /**
     * CWD path.
     */
    private File _CWD;

    /**
     * Construcotr for repository that searches for files.
     */
    public Repository() {
        _CWD = new File(".");
        if (new File(".gitlet/staging/stage.txt").exists()) {
            File ap = new File(".gitlet/staging/stage.txt");
            stage = Utils.readObject(ap, Stage.class);
        }
        if (new File(".gitlet/branches/head.txt").exists()) {
            File one = new File(".gitlet/branches/head.txt");
            head = Utils.readContentsAsString(one);
            File two = new File(".gitlet/currBran.txt");
            currBran = Utils.readContentsAsString(two);
        }
        File fs = new File(".gitlet/remote.txt");
        if (fs.exists()) {
            remote = Utils.readObject(fs, LinkedHashMap.class);
        }
    }

    /**
     * Method to intialize gitlet.
     */
    public void init() {
        File f = new File(".gitlet");
        if (!f.exists()) {
            try {
                f.mkdir();
                File jerome = new File(".gitlet/remote.txt");
                jerome.createNewFile();
                remote = new LinkedHashMap<String, String>();
                Utils.writeObject(jerome, remote);
                File a = new File(".gitlet/blobs");
                a.mkdir();
                File b = new File(".gitlet/branches");
                b.mkdir();
                File c = new File(".gitlet/commits");
                c.mkdir();
                File e = new File(".gitlet/staging");
                e.mkdir();
                stage = new Stage();
                File d = Utils.join(e, "stage.txt");
                d.createNewFile();
                Utils.writeObject(d, stage);
                Commit initialCom = new Commit("initial commit");
                String path = ".gitlet/commits/" + initialCom.getID() + ".txt";
                File fe = new File(path);
                fe.createNewFile();
                Utils.writeObject(fe, initialCom);
                File asdfff = new File(".gitlet/currBran.txt");
                asdfff.createNewFile();
                File g = new File(".gitlet/branches/master.txt");
                File h = new File(".gitlet/branches/head.txt");
                g.createNewFile();
                h.createNewFile();
                Utils.writeContents(asdfff, "master.txt");
                File one = new File(".gitlet/branches/master.txt");
                Utils.writeContents(one, initialCom.getID());
                File two = new File(".gitlet/branches/head.txt");
                Utils.writeContents(two, initialCom.getID());
                File three = new File(".gitlet/branches/master.txt");
                head = Utils.readContentsAsString(three);
            } catch (java.io.IOException e) {
                System.out.println("ff");
            }
        } else {
            String s1 = "A Gitlet version-control system";
            s1 += "already exists in the current directory";
            System.out.println(s1);
        }
    }

    /**
     * Method to add files to stage.
     *
     * @param file THe file to add.
     */
    public void addToStage(String file) {
        File f = new File(file);
        Commit com = getCurrentCommit();
        if (f.exists()) {
            LinkedHashMap tempBlob = com.getBlob();
            String blobString = Utils.sha1(Utils.readContents(f));
            if (tempBlob.get(file) != null) {
                if (tempBlob.get(file).equals(blobString)) {
                    if (stage.getRemovedFiles().contains(file)) {
                        stage.getRemovedFiles().remove(file);
                        File r = new File(".gitlet/staging/stage.txt");
                        Utils.writeObject(r, stage);
                    }
                    return;
                } else {
                    if (stage.getRemovedFiles().contains(file)) {
                        stage.getRemovedFiles().remove(file);
                    }
                }
            }
            byte[] blob = Utils.readContents(f);
            File temp = new File(".gitlet/blobs/" + blobString + ".txt");
            if (!temp.exists()) {
                Utils.writeContents(temp, blob);
            }
            stage.add(file, blobString);
            Utils.writeObject(new File(".gitlet/staging/stage.txt"), stage);
        } else {
            System.out.println("File does not exist.");
        }
    }

    /**
     * Return the current head commit.
     *
     * @return Return the current head commit.
     */
    public Commit getCurrentCommit() {
        String com = head;
        File f = new File(".gitlet/commits/" + com + ".txt");
        return Utils.readObject(f, Commit.class);
    }

    /**
     * Second commitment method.
     *
     * @param mes       The mesage.
     * @param parentTwo THe second parent SHA1.
     */
    public void commitTwo(String mes, String parentTwo) {
        Commit prevCom = getCurrentCommit();
        LinkedHashMap tempBlob = (LinkedHashMap) prevCom.getBlob().clone();
        ArrayList<String> filesToAdd = new ArrayList<>();
        for (String s : stage.getAddedFiles().keySet()) {
            filesToAdd.add(s);
        }
        for (String fileName : filesToAdd) {
            tempBlob.put(fileName, stage.getAddedFiles().get(fileName));
        }
        for (String fileToRemove : stage.getRemovedFiles()) {
            tempBlob.remove(fileToRemove);
        }
        Commit nextCom = new Commit(mes, tempBlob, prevCom.getID(), parentTwo);
        File e = new File(".gitlet/commits/" + nextCom.getID() + ".txt");
        try {
            e.createNewFile();
        } catch (java.io.IOException a) {
            System.out.println("ff");
        }
        Utils.writeObject(e, nextCom);
        File one = new File(".gitlet/branches/head.txt");
        Utils.writeContents(one, nextCom.getID());
        File two = new File(".gitlet/branches/" + currBran);
        Utils.writeContents(two, nextCom.getID());
        stage.empty();
        Utils.writeObject(new File(".gitlet/staging/stage.txt"), stage);
    }

    /**
     * Original commit method.
     *
     * @param message Commit messsage.
     */
    public void commit(String message) {
        if (message == null || message.length() == 0) {
            System.out.println("Please enter a commit message.");
            return;
        }
        if (stage.nothing()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        Commit prevCom = getCurrentCommit();
        LinkedHashMap tempBlob = (LinkedHashMap) prevCom.getBlob().clone();

        ArrayList<String> filesToAdd = new ArrayList<>();
        for (String s : stage.getAddedFiles().keySet()) {
            filesToAdd.add(s);
        }
        for (String fileName : filesToAdd) {
            tempBlob.put(fileName, stage.getAddedFiles().get(fileName));
        }
        for (String fileToRemove : stage.getRemovedFiles()) {
            tempBlob.remove(fileToRemove);
        }
        Commit nextCom = new Commit(message, tempBlob, prevCom.getID(), null);
        File e = new File(".gitlet/commits/" + nextCom.getID() + ".txt");
        try {
            e.createNewFile();
        } catch (java.io.IOException a) {
            System.out.println("ff");
        }
        Utils.writeObject(e, nextCom);
        File sf = new File(".gitlet/branches/head.txt");
        Utils.writeContents(sf, nextCom.getID());
        File sfp = new File(".gitlet/branches/" + currBran);
        Utils.writeContents(sfp, nextCom.getID());
        stage.empty();
        Utils.writeObject(new File(".gitlet/staging/stage.txt"), stage);
    }

    /**
     * Remove a file.
     *
     * @param remFile The file to remove.
     */
    public void rm(String remFile) {
        Commit com = getCurrentCommit();

        ArrayList<String> committedFiles = new ArrayList<>();
        for (String s : com.getBlob().keySet()) {
            committedFiles.add(s);
        }

        boolean a = stage.getAddedFiles().containsKey(remFile);
        boolean b = false;
        for (String f : committedFiles) {
            if (f.equals(remFile)) {
                b = true;
            }
        }
        if (b) {
            Utils.restrictedDelete(remFile);
            stage.addToRemovedFiles(remFile);
            if (a) {
                stage.getAddedFiles().remove(remFile);
            }
            Utils.writeObject(new File(".gitlet/staging/stage.txt"), stage);
            return;
        }
        if (a) {
            stage.getAddedFiles().remove(remFile);
            Utils.writeObject(new File(".gitlet/staging/stage.txt"), stage);
            return;
        }
        System.out.print("No reason to remove the file.");
    }

    /**
     * Method to spit out the log.
     */
    public void log() {
        Commit m = getCurrentCommit();
        while (m != null) {
            System.out.println("===");
            System.out.println("commit " + m.getID());
            if (m.getParentTwoID() != null) {
                String s = m.getParentOneID();
                s = s.substring(0, 7);
                String v = m.getParentTwoID();
                v = v.substring(0, 7);
                System.out.println("Merge: " + s + " " + v);
            }
            System.out.println("Date: " + m.getTime());
            System.out.println(m.getMessage());
            System.out.println();
            if (m.getParentOneID() != null) {
                String s1 = ".gitlet/commits/";
                s1 += m.getParentOneID() + ".txt";
                File f = new File(s1);
                m = Utils.readObject(f, Commit.class);
            } else {
                m = null;
            }
        }
    }

    /**
     * method to spit out the global log.
     */
    public void globalLog() {
        File f = new File(".gitlet/commits");
        List<String> s = Utils.plainFilenamesIn(f);
        for (String temp : s) {
            File sdf = new File(".gitlet/commits/" + temp);
            Commit m = Utils.readObject(sdf, Commit.class);
            System.out.println("===");
            System.out.println("commit " + m.getID());
            if (m.getParentTwoID() != null) {
                String w = m.getParentOneID();
                String v = m.getParentTwoID();
                w = w.substring(0, 7);
                v = v.substring(0, 7);
                System.out.println("Merge: " + w + " " + v);
            }
            System.out.println("Date: " + m.getTime());
            System.out.println(m.getMessage());
            System.out.println();
        }
    }

    /**
     * Method to spit out the status.
     */
    public void status() {
        System.out.println("=== Branches ===");
        File f = new File(".gitlet/branches");
        List<String> s = Utils.plainFilenamesIn(f);
        for (String temp : s) {
            if (!temp.equals("head.txt")) {
                File df = new File(".gitlet/branches/" + temp);
                String sdf = temp.substring(0, temp.length() - 4);
                if (Utils.readContentsAsString(df).equals(head)) {
                    System.out.println("*" + sdf);
                } else {
                    System.out.println(sdf);
                }
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        Set<String> filesAdded = stage.getAddedFiles().keySet();
        List<String> mainList = new ArrayList<String>();
        mainList.addAll(filesAdded);
        Collections.sort(mainList);
        for (String staged : mainList) {
            System.out.println(staged);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        Collections.sort(stage.getRemovedFiles());
        for (String staged : stage.getRemovedFiles()) {
            System.out.println(staged);
        }
        List<String> currFile = Utils.plainFilenamesIn(_CWD);
        currFile.remove(".gitlet");
        statusHelp();
        LinkedHashMap<String, String> tempBlobs = getCurrentCommit().getBlob();
        System.out.println();
        System.out.println("=== Untracked Files ===");
        ArrayList<String> removed = stage.getRemovedFiles();
        for (String one : currFile) {
            if (!filesAdded.contains(one) && !tempBlobs.containsKey(one)) {
                System.out.println(one);
            } else if (removed.contains(one)) {
                System.out.println(one);
            }
        }
    }

    /**
     * Status helper method.
     */
    public void statusHelp() {
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        LinkedHashMap<String, String> tempBlobs = getCurrentCommit().getBlob();
        Set<String> tempos = tempBlobs.keySet();
        for (String ffi : tempos) {
            File fils = new File(ffi);
            if (!fils.exists()) {
                if (stage.stagedForAdd(ffi)) {
                    System.out.println(ffi + " " + "(deleted)");
                } else if (stage.notStageRM(ffi)) {
                    if (tempBlobs.containsKey(ffi)) {
                        System.out.println(ffi + " " + "(deleted)");
                    }
                }
            } else {
                String hashcurr = Utils.sha1(Utils.readContents(new File(ffi)));
                if (stage.notStaged(ffi)) {
                    if (tempBlobs.containsKey(ffi)) {
                        if (!tempBlobs.containsValue(hashcurr)) {
                            System.out.println(ffi + " " + "(modified)");
                        }
                    }
                }
                if (stage.stagedForAdd(ffi)) {
                    if (!hashcurr.equals(tempBlobs.get(ffi))) {
                        System.out.println(ffi + " " + "(modified)");
                    }
                }
            }
        }
    }

    /**
     * Method to find a specific commit.
     *
     * @param message The commit message to find.
     */
    public void find(String message) {
        int c = 0;
        File f = new File(".gitlet/commits");
        List<String> s = Utils.plainFilenamesIn(f);
        for (String temp : s) {
            File fa = new File(".gitlet/commits/" + temp);
            Commit m = Utils.readObject(fa, Commit.class);
            if (message.equals(m.getMessage())) {
                c++;
                System.out.println(m.getID());
            }
        }
        if (c == 0) {
            System.out.println("Found no commit with that message.");
        }
    }

    /**
     * Method to create a branch pointer.
     *
     * @param file File name to point at.
     */
    public void branch(String file) {
        File f = new File(".gitlet/branches/" + file + ".txt");
        if (f.exists()) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        Utils.writeContents(f, head);
    }

    /**
     * Method to remove a branch.
     *
     * @param branch Branch to remove.
     */
    public void removeBranch(String branch) {
        File branchFile = new File(".gitlet/branches/" + branch + ".txt");
        if (!branchFile.exists()) {
            System.out.print("A branch with that name does not exist.");
            return;
        }
        if (branch.equals(currBran.substring(0, currBran.length() - 4))) {
            System.out.print("Cannot remove the current branch.");
            return;
        }
        branchFile.delete();
    }

    /**
     * Checkout helper 1.
     *
     * @param args First argument.
     */
    public void check1(String args) {
        Commit m = getCurrentCommit();
        LinkedHashMap tempBlob = (LinkedHashMap) m.getBlob().clone();
        if (!tempBlob.containsKey(args)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        String name = args;
        if ((new File(name)).exists()) {
            Utils.restrictedDelete(args);
        }
        String s1 = ".gitlet/blobs/";
        s1 += m.getBlob().get(name) + ".txt";
        File currentblob = new File(s1);
        byte[] contents = Utils.readContents(currentblob);
        File f = new File(args);
        Utils.writeContents(f, contents);
    }

    /**
     * Checkout helper 2.
     *
     * @param one  First argument.
     * @param args Second argument.
     */
    public void check2(String one, String args) {
        File f = new File(".gitlet/commits");
        List<String> s = Utils.plainFilenamesIn(f);
        Commit m = null;
        int c = 0;
        for (String temp : s) {
            File as = new File(".gitlet/commits/" + temp);
            m = Utils.readObject(as, Commit.class);
            if (one.equals(m.getID())) {
                c++;
                break;
            }
            if (one.equals(m.getID().substring(0, one.length()))) {
                c++;
                break;
            }
        }
        if (c == 0) {
            System.out.println("No commit with that id exists.");
            return;
        }
        LinkedHashMap tempBlob = (LinkedHashMap) m.getBlob().clone();
        if (!tempBlob.containsKey(args)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        String hash = (String) tempBlob.get(args);
        if ((new File(args)).exists()) {
            Utils.restrictedDelete(args);
        }
        File currentblob = new File(".gitlet/blobs/" + hash + ".txt");
        byte[] contents = Utils.readContents(currentblob);
        File fe = new File(args);
        Utils.writeContents(fe, contents);
    }

    /**
     * Method to checkout a branch.
     *
     * @param args The arguments.
     */
    public void checkout(String... args) {
        if (args.length == 3) {
            check1(args[2]);
        }
        if (args.length == 4) {
            check2(args[1], args[3]);
        }
        if (args.length == 2) {
            File branchFile = new File(".gitlet/branches/" + args[1] + ".txt");
            if (checkErr2(branchFile, args[1])) {
                return;
            }
            String s = Utils.readContentsAsString(branchFile);
            Commit currHead = getCurrentCommit();
            File sd = new File(".gitlet/commits/" + s + ".txt");
            Commit newHead = Utils.readObject(sd, Commit.class);
            LinkedHashMap oldBlobs = (LinkedHashMap) currHead.getBlob().clone();
            LinkedHashMap newBlobs = (LinkedHashMap) newHead.getBlob().clone();
            List<String> filesInCWD = Utils.plainFilenamesIn(_CWD);
            for (String siuuu : filesInCWD) {
                File f = new File(siuuu);
                if (!oldBlobs.containsKey(f.getName())) {
                    if (newBlobs.containsKey(f.getName())) {
                        String s1 = "There is an untracked file in ";
                        s1 += "the way; delete it, or add and commit it first.";
                        System.out.println(s1);
                        return;
                    }
                }
            }
            for (String siuuu : filesInCWD) {
                File f = new File(siuuu);
                if (!newBlobs.containsKey(f.getName())) {
                    if (oldBlobs.containsKey(f.getName())) {
                        if (!siuuu.equals("gitlet")) {
                            Utils.restrictedDelete(f);
                        }
                    }
                }
            }
            Set<String> t = newHead.getBlob().keySet();
            for (String siuuu : t) {
                String blobHash = newHead.getBlob().get(siuuu);
                String blobPath = ".gitlet/blobs/" + blobHash + ".txt";
                File newFile = new File(blobPath);
                byte[] contents = Utils.readContents(newFile);
                Utils.writeContents(new File(siuuu), contents);
            }

            stage.empty();
            Utils.writeObject(new File(".gitlet/staging/stage.txt"), stage);
            File fs = new File(".gitlet/branches/head.txt");
            Utils.writeContents(fs, newHead.getID());
            currBran = args[1] + ".txt";
            File g = new File(".gitlet/currBran.txt");
            Utils.writeContents(g, currBran);
            File sds = new File(".gitlet/branches/" + currBran);
            Utils.writeContents(sds, newHead.getID());
        }
    }

    /**
     * Checker error 2.
     *
     * @param branchFile The branch file.
     * @param sas        The STringsas.
     * @return Error or no.
     */
    public boolean checkErr2(File branchFile, String sas) {
        if (!branchFile.exists()) {
            System.out.println("No such branch exists.");
            return true;
        }
        String tem = (currBran.substring(0, currBran.length() - 4));
        if (tem.equals(sas)) {
            System.out.println("No need to checkout the current branch.");
            return true;
        }
        return false;
    }

    /**
     * Mege errorer handler.
     *
     * @param branch Given branch name.
     * @return Whether error or not.
     */
    public boolean errors(String branch) {
        File f = new File(".gitlet/branches");
        if (!stage.nothing()) {
            System.out.println("You have an uncommitted changes.");
            return true;
        }
        if (currBran.equals(branch + ".txt")) {
            System.out.println("Cannot merge a branch with itself");
            return true;
        }
        if (branch.indexOf("/") != -1) {
            File t = new File(".gitlet/branches/" + branch + ".txt");
            if (!t.exists()) {
                System.out.println("A branch with that name does not exist.");
                return true;
            }
        } else {
            List<String> s = Utils.plainFilenamesIn(f);
            if (!s.contains(branch + ".txt")) {
                System.out.println("A branch with that name does not exist.");
                return true;
            }
        }
        return false;
    }

    /**
     * Error merge function.
     *
     * @param c      The current branch.
     * @param bcom   The brancom.
     * @param sp     The split point.
     * @param branch The current branch name.
     * @return Whether error or not.
     */
    public boolean errorMerge(Commit c, Commit bcom, Commit sp, String branch) {
        boolean error = false;
        if (sp.getBlob().equals(c.getBlob())) {
            checkout("checkout", branch);
            System.out.println("Current branch fast-forwarded.");
            error = true;
        }
        if (sp.getID().equals(bcom.getID())) {
            String s1 = "Given branch is an ";
            s1 += "ancestor of the current branch.";
            System.out.println(s1);
            error = true;
        }
        return error;
    }

    /**
     * The merge method.
     *
     * @param branch The given branch to merge.
     */
    public void merge(String branch) {
        Boolean merged = false;
        File f = new File(".gitlet/branches");
        List<String> s = Utils.plainFilenamesIn(f);
        if (errors(branch)) {
            return;
        }
        Commit curr = getCurrentCommit();
        File one = new File(".gitlet/branches/" + branch + ".txt");
        String tem = Utils.readContentsAsString(one);
        File two = new File(".gitlet/commits/" + tem + ".txt");
        Commit branCom = Utils.readObject(two, Commit.class);
        String lc = findLatCommon(branch);
        if (lc.equals("")) {
            return;
        }
        File thr = new File(".gitlet/commits/" + lc + ".txt");
        Commit splitPoint = Utils.readObject(thr, Commit.class);
        if (errorMerge(curr, branCom, splitPoint, branch)) {
            return;
        }
        LinkedHashMap blobcurrcom = curr.getBlob();
        LinkedHashMap blobcurrbran = branCom.getBlob();
        LinkedHashMap blobsplit = splitPoint.getBlob();
        List<String> fi = Utils.plainFilenamesIn(new File("."));
        for (String fileN : fi) {
            String o = (String) blobsplit.get(fileN);
            String t = (String) blobcurrbran.get(fileN);
            String l = (String) blobcurrcom.get(fileN);
            if (o == null) {
                merged = mergehelp5(t, l, fileN, branCom) || merged;
            } else {
                if (t != null && l != null) {
                    merged = mergehelp3(o, l, t, fileN) || merged;
                } else if (t == null && l != null) {
                    merged = mergehelp4(branCom, l, o, fileN) || merged;
                }
            }
        }
        merged = mergehelp2(curr, branCom, splitPoint) || merged;
        String taaaaa = currBran.substring(0, currBran.length() - 4);
        String mess = "Merged " + branch + " into " + taaaaa + ".";
        commitTwo(mess, branCom.getID());
        if (merged) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /**
     * Merge helper method 4.
     *
     * @param branC BranCom commit.
     * @param l     Stirng l.
     * @param o     STring o.
     * @param fileN STring file name.
     * @return Merged or not.
     */
    public boolean mergehelp4(Commit branC, String l, String o, String fileN) {
        LinkedHashMap blobcurrbran = branC.getBlob();
        if (l.equals(o)) {
            stage.addToRemovedFiles(fileN);
            Utils.restrictedDelete(new File(fileN));
            blobcurrbran.remove(fileN);
        } else {
            File merge = new File(fileN);
            byte[] asss = "<<<<<<< HEAD\n".getBytes(StandardCharsets.UTF_8);
            File ssd = new File(".gitlet/blobs/" + l + ".txt");
            byte[] everything = byteToContents(asss, Utils.readContents(ssd));
            byte[] asddf = "=======\n".getBytes(StandardCharsets.UTF_8);
            everything = byteToContents(everything, asddf);
            everything = byteToContents(everything, new byte[]{});
            byte[] mmsm = ">>>>>>>\n".getBytes(StandardCharsets.UTF_8);
            everything = byteToContents(everything, mmsm);
            Utils.writeContents(merge, everything);
            stage.add(fileN, Utils.sha1(everything));
            return true;
        }
        return false;
    }

    /**
     * Merge help 5.
     *
     * @param t     String t.
     * @param l     String l.
     * @param fileN String filen.
     * @param branC String brancom.
     * @return Error or no.
     */
    public boolean mergehelp5(String t, String l, String fileN, Commit branC) {
        LinkedHashMap blobcurrbran = branC.getBlob();
        if (t != null && l == null) {
            File on = new File(".gitlet/blobs/" + t + ".txt");
            byte[] blob = Utils.readContents(on);
            Utils.writeContents(new File(fileN), blob);
            stage.add(fileN, t);
        } else if (t != null && l != null && !blobcurrbran.containsValue(l)) {
            File merge = new File(fileN);
            byte[] tempp = "<<<<<<< HEAD\n".getBytes(StandardCharsets.UTF_8);
            File k = new File(".gitlet/blobs/" + l + ".txt");
            byte[] everything = byteToContents(tempp, Utils.readContents(k));
            byte[] mppp = "=======\n".getBytes(StandardCharsets.UTF_8);
            everything = byteToContents(everything, mppp);
            File mm = new File(".gitlet/blobs/" + t + ".txt");
            everything = byteToContents(everything, Utils.readContents(mm));
            byte[] assm = ">>>>>>>\n".getBytes(StandardCharsets.UTF_8);
            everything = byteToContents(everything, assm);
            Utils.writeContents(merge, everything);
            stage.add(fileN, Utils.sha1(everything));
            return true;
        }
        return false;
    }

    /**
     * Merge helper method 3.
     *
     * @param o     STring o.
     * @param l     String l.
     * @param t     STring t.
     * @param fileN File name.
     * @return Merge conlflict or not.
     */
    public boolean mergehelp3(String o, String l, String t, String fileN) {
        if (o.equals(t) && !o.equals(l)) {
            File asdf = new File(".gitlet/blobs/" + l + ".txt");
            byte[] blob = Utils.readContents(asdf);
            Utils.writeContents(new File(fileN), blob);
            stage.add(fileN, l);
        } else if (!o.equals(t) && o.equals(l)) {
            File moon = new File(".gitlet/blobs/" + t + ".txt");
            byte[] blob = Utils.readContents(moon);
            Utils.writeContents(new File(fileN), blob);
        } else if (!o.equals(l) && !o.equals(t) && !t.equals(l)) {
            File merge = new File(fileN);
            byte[] moo = "<<<<<<< HEAD\n".getBytes(StandardCharsets.UTF_8);
            File oi = new File(".gitlet/blobs/" + l + ".txt");
            byte[] cow = Utils.readContents(oi);
            byte[] everything = byteToContents(moo, cow);
            byte[] geor = "=======\n".getBytes(StandardCharsets.UTF_8);
            everything = byteToContents(everything, geor);
            File ase = new File(".gitlet/blobs/" + t + ".txt");
            everything = byteToContents(everything, Utils.readContents(ase));
            byte[] mncn = ">>>>>>>\n".getBytes(StandardCharsets.UTF_8);
            everything = byteToContents(everything, mncn);
            Utils.writeContents(merge, everything);
            stage.add(fileN, Utils.sha1(everything));
            return true;
        }
        return false;
    }

    /**
     * Merge helper function.
     *
     * @param curr    The current branch.
     * @param branCom The given branch.
     * @param sp      The split point.
     * @return Whether conflict occured or not.
     */
    public boolean mergehelp2(Commit curr, Commit branCom, Commit sp) {
        LinkedHashMap blobcurrcom = curr.getBlob();
        LinkedHashMap blobcurrbran = branCom.getBlob();
        LinkedHashMap blobsplit = sp.getBlob();
        boolean merged = false;
        Set<String> ss = blobcurrbran.keySet();
        for (String file : ss) {
            String l = (String) blobcurrbran.get(file);
            if (!blobcurrcom.containsKey(file)) {
                if (!blobsplit.containsKey(file)) {
                    File oi = new File(".gitlet/blobs/" + l + ".txt");
                    byte[] blob = Utils.readContents(oi);
                    Utils.writeContents(new File(file), blob);
                    stage.add(file, (String) blobcurrbran.get(file));
                } else if (blobsplit.containsKey(file)) {
                    if (!blobsplit.containsValue(l)) {
                        File merge = new File(file);
                        String t = "<<<<<<< HEAD\n";
                        byte[] mkk = t.getBytes(StandardCharsets.UTF_8);
                        byte[] e = byteToContents(mkk, new byte[]{});
                        String w = "=======\n";
                        byte[] mksfse = w.getBytes(StandardCharsets.UTF_8);
                        e = byteToContents(e, mksfse);
                        File ford = new File(".gitlet/blobs/" + l + ".txt");
                        e = byteToContents(e, Utils.readContents(ford));
                        String y = ">>>>>>>\n";
                        byte[] jame = y.getBytes(StandardCharsets.UTF_8);
                        e = byteToContents(e, jame);
                        Utils.writeContents(merge, e);
                        stage.add(file, Utils.sha1(e));
                        merged = true;
                    }
                }
            }
        }
        return merged;
    }

    /**
     * Turn the byte to STrings.
     *
     * @param a         The stuff to add.
     * @param newStuffs The new stuff to add.
     * @return The byte array of stuff.
     */
    private byte[] byteToContents(byte[] a, byte[] newStuffs) {
        byte[] endResult = new byte[a.length + newStuffs.length];
        System.arraycopy(a, 0, endResult, 0, a.length);
        System.arraycopy(newStuffs, 0, endResult, a.length, newStuffs.length);
        return endResult;
    }

    /**
     * Find the most recent common ancestor.
     *
     * @param branch The given branch.
     * @return SHA1 of split point commit
     */
    public String findLatCommon(String branch) {
        String tem = "";
        Commit curr = getCurrentCommit();
        File jj = new File(".gitlet/branches/" + branch + ".txt");
        String branchComID = Utils.readContentsAsString(jj);
        File se = new File(".gitlet/commits/" + branchComID + ".txt");
        Commit branchCom = Utils.readObject(se, Commit.class);
        if (!findLatError(curr, branchCom)) {
            Queue<Commit> fringeCurr = new ArrayDeque<>();
            ArrayList<String> visitedCurr = new ArrayList<>();
            fringeCurr.add(curr);
            while (!fringeCurr.isEmpty()) {
                Commit m = fringeCurr.poll();
                if (!visitedCurr.contains(m)) {
                    visitedCurr.add(m.getID());
                    if (m.getParentOneID() != null) {
                        String dd = ".gitlet/commits/";
                        dd += m.getParentOneID() + ".txt";
                        File kj = new File(dd);
                        fringeCurr.add(Utils.readObject(kj, Commit.class));
                    }
                    if (m.getParentTwoID() != null) {
                        String dd = ".gitlet/commits/";
                        dd += m.getParentTwoID() + ".txt";
                        File kj = new File(dd);
                        fringeCurr.add(Utils.readObject(kj, Commit.class));
                    }
                }
            }
            Queue<Commit> fringeBran = new ArrayDeque<>();
            ArrayList<String> visitedBran = new ArrayList<>();
            fringeBran.add(branchCom);
            while (!fringeBran.isEmpty()) {
                Commit m = fringeBran.poll();
                if (!visitedBran.contains(m)) {
                    visitedBran.add(m.getID());
                    if (m.getParentOneID() != null) {
                        String dd = ".gitlet/commits/";
                        dd += m.getParentOneID() + ".txt";
                        File fred = new File(dd);
                        fringeBran.add(Utils.readObject(fred, Commit.class));
                    }
                    if (m.getParentTwoID() != null) {
                        String dd = ".gitlet/commits/";
                        dd += m.getParentTwoID() + ".txt";
                        File fre = new File(dd);
                        fringeBran.add(Utils.readObject(fre, Commit.class));
                    }
                }
            }
            for (String c : visitedCurr) {
                if (visitedBran.contains(c)) {
                    tem = c;
                    break;
                }
            }
        }
        return tem;
    }

    /**
     * Error test for findSplit.
     *
     * @param curr      Current com.
     * @param branchCom Branch Com.
     * @return Error or no
     */
    public boolean findLatError(Commit curr, Commit branchCom) {
        List<String> filesinCWD = Utils.plainFilenamesIn(_CWD);
        for (String asdf : filesinCWD) {
            if (!curr.getBlob().containsKey(asdf)) {
                if (branchCom.getBlob().containsKey(asdf)) {
                    String o = "There is an untracked file in the way;";
                    o += " delete it, or add and commit it first.";
                    System.out.println(o);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Reset method.
     *
     * @param comID The given ID.
     */
    public void reset(String comID) {
        File sasdf = new File(".gitlet/commits/" + comID + ".txt");
        if (!sasdf.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit m = Utils.readObject(sasdf, Commit.class);
        Commit currCom = getCurrentCommit();
        LinkedHashMap oldBlobs = (LinkedHashMap) m.getBlob().clone();
        LinkedHashMap newBlobs = (LinkedHashMap) currCom.getBlob().clone();
        List<String> filesInCWD = Utils.plainFilenamesIn(_CWD);
        for (String siuuu : filesInCWD) {
            if (oldBlobs.containsKey(siuuu) && !newBlobs.containsKey(siuuu)) {
                String dd = "There is an untracked file in the way; ";
                dd += "delete it, or add and commit it first.";
                System.out.println(dd);
                return;
            }
        }
        for (String siuuu : filesInCWD) {
            File fa = new File(siuuu);
            if (newBlobs.containsKey(siuuu) && !oldBlobs.containsKey(siuuu)) {
                Utils.restrictedDelete(fa);
            }
        }
        Set<String> ss = oldBlobs.keySet();
        for (String siu : ss) {
            File f = new File(".gitlet/blobs/" + oldBlobs.get(siu) + ".txt");
            Utils.writeContents(new File(siu), Utils.readContents(f));
        }
        stage.empty();
        Utils.writeObject(new File(".gitlet/staging/stage.txt"), stage);
        Utils.writeContents(new File(".gitlet/branches/head.txt"), comID);
        Utils.writeContents(new File(".gitlet/branches/" + currBran), comID);
    }

    /**
     * EC remote add.
     * @param name The file.
     * @param directory The directory.
     */
    public void addRemote(String name, String directory) {
        String s = File.separator;
        String dir = "";
        for (int i = 0; i < directory.length(); i++) {
            if (directory.charAt(i) == '/') {
                dir += s;
            } else {
                dir += directory.charAt(i);
            }
        }
        if (remote.containsKey(name)) {
            System.out.println("A remote with that name already exists.");
        } else {
            remote.put(name, dir);
            File fs = new File(".gitlet/remote.txt");
            Utils.writeObject(fs, remote);
        }
    }

    /**
     * Remove but remote.
     * @param name The name.
     */
    public void rmRemote(String name) {
        if (!remote.containsKey(name)) {
            System.out.println("A remote with that name does not exist");
        } else {
            remote.remove(name);
            Utils.writeObject(new File(".gitlet/remote.txt"), remote);
        }
    }

    /**
     * Pull method.
     * @param name The repo.
     * @param branNa The branch.
     */
    public void pull(String name, String branNa) {
        fetch(name, branNa);
        merge(name + "/" + branNa);
    }

    /**
     * Fetch method.
     * @param name The repo.
     * @param branName The branch.
     */
    public void fetch(String name, String branName) {
        String temCWD = remote.get(name);
        File f = new File(temCWD);
        if (f.exists()) {
            File he = new File(temCWD + "/branches/" + branName + ".txt");
            if (!he.exists()) {
                System.out.println("That remote does not have that branch.");
                return;
            }
            String headCom = Utils.readContentsAsString(he);
            ArrayList<Commit> commits = new ArrayList<>();
            while (headCom != null) {
                File dog = new File(temCWD + "/commits/" + headCom + ".txt");
                Commit t = Utils.readObject(dog, Commit.class);
                commits.add(t);
                headCom = t.getParentOneID();
            }
            Collections.reverse(commits);
            LinkedHashMap<String, byte[]> blobTrack = new LinkedHashMap<>();
            for (String s: Utils.plainFilenamesIn(temCWD + "/blobs")) {
                File r = new File(temCWD + "/blobs/" + s);
                byte[] ssf = Utils.readContents(r);
                blobTrack.put(Utils.sha1(ssf), ssf);
            }
            for (Commit c: commits) {
                String p = ".gitlet/commits/";
                p += c.getID() + ".txt";
                File comPath = new File(p);
                if (!comPath.exists()) {
                    Utils.writeObject(comPath, c);
                    LinkedHashMap<String, String> blobs = c.getBlob();
                    for (String s: blobs.keySet()) {
                        String m = ".gitlet/blobs/";
                        m += blobs.get(s) + ".txt";
                        File tem = new File(m);
                        if (!tem.exists()) {
                            String pf = Utils.sha1(blobTrack.get(blobs.get(s)));
                            Utils.writeContents(tem, pf);
                        }
                    }
                }
            }
            File sm = new File(temCWD + "/branches/head.txt");
            String headID = Utils.readContentsAsString(sm);
            File t = new File(".gitlet/branches/" + name);
            t.mkdir();
            String ns = ".gitlet/branches/" + name + "/" + branName + ".txt";
            File ges = new File(ns);
            Utils.writeContents(ges, headID);
        } else {
            System.out.println("Remote directory not found.");
        }
    }

    /**
     * Push error.
     * @param name The string.
     * @return String of dir.
     */
    public String pushErr(String name) {
        if (remote.containsKey(name)) {
            return remote.get(name);
        } else {
            System.out.println("Remote directory not found.");
            return null;
        }
    }

    /**
     * push method.
     * @param name The repo.
     * @param branName The branch.
     */
    public void push(String name, String branName) {
        String dir = pushErr(name);
        File t = new File(dir);
        if (dir != null && t.exists()) {
            File f = new File(dir + "/branches/" + branName + ".txt");
            if (f.exists()) {
                File w = new File(dir + "/branches/head.txt");
                String remHead = Utils.readContentsAsString(w);
                ArrayList<String> visitedCurr = help();
                if (visitedCurr.contains(remHead)) {
                    Commit c = null;
                    LinkedHashMap<String, byte[]> blobTrack;
                    blobTrack = new LinkedHashMap<>();
                    for (String s: Utils.plainFilenamesIn(dir + "/blobs")) {
                        File j = new File(dir + "/blobs/" + s);
                        byte[] fa = Utils.readContents(j);
                        blobTrack.put(s, fa);
                    }
                    int a = visitedCurr.indexOf(remHead) - 1;
                    for (int i = a; i >= 0; i--) {
                        String lk = ".gitlet/commits/";
                        lk += visitedCurr.get(i) + ".txt";
                        File mo = new File(lk);
                        c = Utils.readObject(mo, Commit.class);
                        String mk = dir + "/commits/";
                        mk += visitedCurr.get(i) + ".txt";
                        Utils.writeObject(new File(mk), c);
                        for (String s: c.getBlob().values()) {
                            File lll = new File(dir + "/blobs/" + s + ".txt");
                            if (!lll.exists()) {
                                String op = ".gitlet/blobs/";
                                op += s + ".txt";
                                File k = new File(op);
                                Utils.writeContents(lll, Utils.readContents(k));
                            }
                        }
                    }
                    String b = dir;
                    b += "/branches/head.txt";
                    File mo = new File(b);
                    Utils.writeContents(mo, c.getID());
                    File ms = new File(dir + "/currBran.txt");
                    String sdf = Utils.readContentsAsString(ms);
                    String bow = dir;
                    bow += "/branches/" + sdf + ".txt";
                    File je = new File(bow);
                    Utils.writeContents(je, c.getID());
                } else {
                    String m = "Please pull down ";
                    m += "remote changes before pushing.";
                    System.out.println(m);
                }
            } else {
                Utils.writeContents(f, "");
            }
        } else {
            System.out.println("Remote directory not found.");
        }
    }

    /**
     * Helper for push.
     * @return Arraylist of sttuff.
     */
    public ArrayList<String> help() {
        Queue<Commit> fringeCurr = new ArrayDeque<>();
        ArrayList<String> visitedCurr = new ArrayList<>();
        fringeCurr.add(getCurrentCommit());
        while (!fringeCurr.isEmpty()) {
            Commit m = fringeCurr.poll();
            if (!visitedCurr.contains(m)) {
                visitedCurr.add(m.getID());
                if (m.getParentOneID() != null) {
                    String dd = ".gitlet/commits/";
                    dd += m.getParentOneID() + ".txt";
                    File kj = new File(dd);
                    fringeCurr.add(Utils.readObject(kj, Commit.class));
                }
                if (m.getParentTwoID() != null) {
                    String dd = ".gitlet/commits/";
                    dd += m.getParentTwoID() + ".txt";
                    File kj = new File(dd);
                    fringeCurr.add(Utils.readObject(kj, Commit.class));
                }
            }
        }
        return visitedCurr;
    }

}
