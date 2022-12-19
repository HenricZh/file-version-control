package gitlet;

import java.io.File;

/**
 * Driver class for Gitlet, the tiny stupid version-control system.
 *
 * @author Henric Zhang
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND> ....
     */
    public static void main(String... args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        Repository r = new Repository();
        if (args[0].equals("init")) {
            one(args);
            r.init();
        } else if (args[0].equals("add") && commandNeedFile()) {
            two(args);
            r.addToStage(args[1]);
        } else if (args[0].equals("commit") && commandNeedFile()) {
            two(args);
            r.commit(args[1]);
        } else if (args[0].equals("rm") && commandNeedFile()) {
            two(args);
            r.rm(args[1]);
        } else if (args[0].equals("log") && commandNeedFile()) {
            one(args);
            r.log();
        } else if (args[0].equals("global-log") && commandNeedFile()) {
            one(args);
            r.globalLog();
        } else if (args[0].equals("find") && commandNeedFile()) {
            two(args);
            r.find(args[1]);
        } else if (args[0].equals("status") && commandNeedFile()) {
            one(args);
            r.status();
        } else if (args[0].equals("checkout") && commandNeedFile()) {
            sped(args);
            r.checkout(args);
        } else if (args[0].equals("branch") && commandNeedFile()) {
            two(args);
            r.branch(args[1]);
        } else if (args[0].equals("rm-branch") && commandNeedFile()) {
            two(args);
            r.removeBranch(args[1]);
        } else if (args[0].equals("reset") && commandNeedFile()) {
            two(args);
            r.reset(args[1]);
        } else if (args[0].equals("merge") && commandNeedFile()) {
            two(args);
            r.merge(args[1]);
        } else if (args[0].equals("add-remote")) {
            r.addRemote(args[1], args[2]);
        } else if (!checkers(r, args)) {
            System.out.println("No command with that name exists.");
        }
        System.exit(0);
    }

    /**
     * Handle mrce cases.
     * @param r THe repo.
     * @param args The args.
     * @return Whether error or no.
     */
    public static boolean checkers(Repository r, String... args) {
        if (args[0].equals("rm-remote")) {
            r.rmRemote(args[1]);
            return true;
        }
        if (args[0].equals("push")) {
            r.push(args[1], args[2]);
            return true;
        }
        if (args[0].equals("fetch")) {
            r.fetch(args[1], args[2]);
            return true;
        }
        if (args[0].equals("pull")) {
            r.pull(args[1], args[2]);
            return true;
        }
        return false;
    }

    /**
     * Handles if gitlet exists.
     * @return Whether there .gitlet or not.
     */
    public static boolean commandNeedFile() {
        File f = new File(".gitlet");
        if (!f.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
            return false;
        }
        return true;
    }

    /**
     * Special input case.
     * @param args Inputs.
     */
    public static void sped(String... args) {
        if (args.length > 4 || args.length <= 1) {
            System.out.println("Incorrect Operands.");
            System.exit(0);
        }
        if (args.length == 4 && !args[2].equals("--")) {
            System.out.println("Incorrect Operands.");
            System.exit(0);
        }
        if (args.length == 3 && !args[1].equals("--")) {
            System.out.println("Incorrect Operands.");
            System.exit(0);
        }
    }

    /**
     * Does stuff to wrong inputs.
     * @param args Wrong inputs.
     */
    public static void one(String... args) {
        if (args.length != 1) {
            System.out.println("Incorrect Operands.");
            System.exit(0);
        }
    }

    /**
     * Does stuff if there are wrong input.
     * @param args The input.
     */
    public static void two(String... args) {
        if (args.length != 2) {
            System.out.println("Incorrect Operands.");
            System.exit(0);
        }
    }
}
