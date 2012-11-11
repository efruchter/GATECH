import nfa.NFABuilder;
import nfa.NFAUtil;
import nfa.NFAUtil.NFASegment;
import spec.Spec;
import spec.SpecReader;
import spec.TokenType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class TheProject {
    final InputStream specFileInputStream;
    final InputStream programFileInputStream;

    public TheProject(InputStream specFileInputStream, InputStream programFileInputStream) {
        this.specFileInputStream = specFileInputStream;
        this.programFileInputStream = programFileInputStream;
    }

    public void doStuff() {
        //SpecReader specReader = new SpecReader(this.specFileInputStream);
        //Spec spec = specReader.specify();
        //System.out.println(spec);

        Spec spec2 = new Spec();
        spec2.addTokenType(new TokenType("NUM", "(0|1|2)(a|b|c)", spec2.iterCharClasses()));
        //spec2.addTokenType(new TokenType("WORD", "(a|b|c)", spec2.iterCharClasses()));
        NFASegment nfa = NFABuilder.buildNFAFromSpec(spec2);
        System.out.println(NFAUtil.isValid(nfa, "2a"));
        System.out.println(NFAUtil.isValid(nfa, "0c"));
        System.out.println(NFAUtil.isValid(nfa, "1"));
        System.out.println(NFAUtil.isValid(nfa, "b"));
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Parameters: <spec-file> <program-file>");
            System.exit(1);
        }

        String specFilePath = args[0];
        String programFilePath = args[1];

        TheProject project = null;

        try {
            InputStream specFileInputStream = new FileInputStream(specFilePath);
            InputStream programFileInputStream = new FileInputStream(programFilePath);
            project = new TheProject(specFileInputStream, programFileInputStream);
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
            System.exit(1);
        }

        project.doStuff();
    }
}
