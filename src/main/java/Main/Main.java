package Main;

import Utilities.Fasta;
import Utilities.Pseudo;

import static Main.GlobalVariables.thread;

public class Main
{

    private static String infile, outfile;
    private static boolean identifiers_retained = true;
    private static boolean realign_endings = false;

    public static void main(String[] args)
    {
        parse(args);
        print_args();
        var input = new Fasta(infile);

        var result = SuffixTreeAlignment.SuffixTreeAligner.align(Pseudo.string_to_pseudo(input.get_sequences()));
        if (realign_endings) result = Pairwise.PoolAligner.align(result);

        var output_sequences = new String[result.length];
        var output_sequence_identifiers = new String[result.length];
        for (int i = 0; i != result.length; ++i)
        {
            output_sequences[i] = Pseudo.merge(result[i], input.get_sequence(i));
            output_sequence_identifiers[i] = input.get_sequence_identifiers(i);
        }
        new Fasta(output_sequences, output_sequence_identifiers).output(outfile, identifiers_retained);
    }

    // 修改参数需要修改parse, arg_help, print_args三个函数, 其中arg_help可能有两个地方需要更改
    private static void parse(String[] args)
    {
        if (args.length == 0) args_help();
        for (int i = 0; i < args.length; ++i)
            if (args[i].charAt(0) == '-')
            {
                if (args[i].length() != 2) args_help();
                if (args[i].charAt(1) == 'o')
                {
                    if (i == args.length - 1 || args[++i].charAt(0) == '-') args_help();
                    outfile = args[i];
                }
                else if (args[i].charAt(1) == 't')
                {
                    if (i == args.length - 1 || args[++i].charAt(0) == '-') args_help();
                    try { thread = Integer.parseInt(args[i]); }
                    catch (NumberFormatException e) { args_help(); }
                }
                else if (args[i].charAt(1) == 's')
                {
                    identifiers_retained = false;
                }
                else if (args[i].charAt(1) == 'r')
                {
                    realign_endings = true;
                }
                else
                {
                    args_help();
                }
            }
            else if (i != args.length - 1)
            {
                args_help();
            }
            else
            {
                infile = args[i];
            }

        if (infile == null) args_help();
        if (outfile == null) outfile = infile + ".aligned";
        final int core_num = Runtime.getRuntime().availableProcessors();
        if (thread < 1 || thread > core_num) thread = core_num > 2 ? core_num / 2 : 1;
    }

    private static void args_help()
    {
        System.out.println("usage: java -jar " + System.getProperty("java.class.path") + " [-h] [-o] [-t] [-r] [-s] infile" );
        System.out.println();
        System.out.println("positional argument: ");
        System.out.println("  infile   nucleotide sequences in fasta format");
        System.out.println();
        System.out.println("optional arguments: ");
        System.out.println("  -h       show this help message and exit");
        System.out.println("  -o       target file");
        System.out.println("  -t       multi-thread");
        System.out.println("  -r       realign the endings for better results");
        System.out.println("  -s       output alignments without sequence identifiers, i.e. in plain txt format but with sequence order retained");
        System.out.println();
        System.exit(0);
    }

    private static void print_args()
    {
        System.out.println("\t         infile: " + infile);
        System.out.println("\t        outfile: " + outfile);
        System.out.println("\t         thread: " + thread);
        System.out.println("\t     identifier: " + (identifiers_retained ? "retained" : "not retained"));
        System.out.println("\trealign endings: " + (realign_endings ? "true" : "false"));
        System.out.println();
    }

    @SuppressWarnings("unused")
    private static void count(int cnt)
    {
        int percent = 0;
        System.out.println("Progress...");
        System.out.print(percent + "%");
        for (int i = 0; i != cnt; ++i)
        {
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            for (int j = 0; j <= String.valueOf(percent).length(); ++j) System.out.print('\b');
            percent = i * 100 / cnt;
            System.out.print(percent + "%");
        }
    }

}