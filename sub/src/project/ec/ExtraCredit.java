package project.ec;

import project.nfa.NFA;
import project.nfa.NFAUtil;
import project.nfa.State;
import project.nfa.Transition;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: toriscope
 */
public class ExtraCredit {


    // statename(*) | trans| state| trans| state ... \n
    // || empty trans

    public static void main(String[] args) {

        File inputFile = null;

        if (args.length > 0) {
            inputFile = new File(args[0]);
        } else {
            JFileChooser fileChooser = new JFileChooser("Choose NFA formatted input file");
            int result = fileChooser.showOpenDialog(null);
            if (result != JFileChooser.APPROVE_OPTION) {
                System.exit(1);
            } else {
                inputFile = fileChooser.getSelectedFile();
            }
        }
        Scanner scannerInput = null;
        try {
            scannerInput = new Scanner(inputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        HashMap<String, project.nfa.State> nameToState = new HashMap<String, project.nfa.State>();
        HashMap<State, List<String[]>> stateToTrans = new HashMap<State, List<String[]>>();
        List<String> testStrings = new LinkedList<String>();

        State startState = null;

        while (scannerInput.hasNextLine()) {

            String stateInfo = scannerInput.nextLine();
            String[] info = stateInfo.split("\\|");

            if (stateInfo.trim().isEmpty()) {
                continue;
            }

            // Trim everything
            for (int i = 0; i < info.length; i++) {
                info[i] = info[i].trim();
            }

            if (info[0].startsWith("#")) {
                continue;
            }

            if (info[0].startsWith("$")) {
                testStrings.add(stateInfo.replaceFirst("\\$", "").trim());
                continue;
            }

            boolean isFinal = info[0].charAt(info[0].length() - 1) == '*';
            if (isFinal) {
                info[0] = info[0].substring(0, info[0].length() - 1);
            }

            State state = new State(info[0], isFinal);

            if (startState == null && nameToState.isEmpty()) {
                startState = state;
            }

            nameToState.put(info[0], state);

            List<String[]> trans = new LinkedList<String[]>();
            for (int i = 1; i < info.length; i += 2) {
                trans.add(new String[]{info[i], info[i + 1]});
            }
            stateToTrans.put(state, trans);
        }

        for (Map.Entry<String, State> entry : nameToState.entrySet()) {
            if (stateToTrans.get(entry.getValue()) != null)
                for (String[] transTuple : stateToTrans.get(entry.getValue())) {
                    boolean isEmpty = transTuple[0].isEmpty();
                    Transition transition = new Transition(transTuple[0], nameToState.get(transTuple[1]));
                    transition.setIsEmptyTransition(isEmpty);
                    entry.getValue().addTransition(transition);
                }
        }

        NFA nfa = new NFA(startState);
        System.out.println(nfa);

        System.out.print("NFA Tests:\n");

        for (String test : testStrings) {
            System.out.println("\nResults for: " + test);
            System.out.println(NFAUtil.isValidVerbose(nfa, test));
        }

        System.out.println("\n---\nDFA Tests:\n");
        nfa = NFAUtil.convertToDFA(NFAUtil.convertToDFA(nfa));
        System.out.println(nfa);

        for (String test : testStrings) {
            System.out.println("\nResults for: " + test);
            System.out.println( NFAUtil.isValidVerbose(nfa, test));
        }

        /*System.out.print("\nDFA (Minimized) Tests:\n");
        NFAUtil.minimizeDFA(nfa);
        System.out.println(nfa);

        for (String test : testStrings) {
            System.out.println("\nResults for: " + test);
            System.out.println(NFAUtil.isValidVerbose(nfa, test));
        }*/
    }
}
