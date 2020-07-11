package Main;

import static Main.GlobalVariables.THREAD;

public class Main
{

    private static String infile, outfile;
    private static boolean identifiers_retained = true;
    private static boolean force_align = false;

    public static void main(String[] args)
    {
        parse(args);
        print_args();
        SuffixTreeAlignment.Main.align(infile, force_align).output(outfile, identifiers_retained);
    }

    // 修改参数需要修改parse, arg_help, print_args三个函数, 其中arg_help可能有两个地方需要更改
    private static void parse(String[] args)
    {
        if (args.length == 0) args_help();
        for (int i = 0; i < args.length; ++i)
        {
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
                    try { THREAD = Integer.parseInt(args[i]); }
                    catch (NumberFormatException e) { args_help(); }
                }
                else if (args[i].charAt(1) == 's')
                {
                    identifiers_retained = false;
                }
                else if (args[i].charAt(1) == 'F')
                {
                    force_align = true;
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
                if (outfile == null) outfile = infile + ".aligned";
                final int core_num = Runtime.getRuntime().availableProcessors();
                if (THREAD < 1 || THREAD > core_num)
                {
                    THREAD = core_num > 2 ? core_num / 2 : 1;
                }
            }
        }
    }

    private static void args_help()
    {
        System.out.println("usage: java -jar " + System.getProperty("java.class.path") + " [-h] [-o] [-t] [-F] [-s] infile" );
        System.out.println();
        System.out.println("positional argument: ");
        System.out.println("  infile   nucleotide sequences in fasta format");
        System.out.println();
        System.out.println("optional arguments: ");
        System.out.println("  -h       show this help message and exit");
        System.out.println("  -o       target file");
        System.out.println("  -t       multi-thread");
        System.out.println("  -F       align all the sequences despite the huge difference of some sequences from the mainstream");
        System.out.println("  -s       output alignments without sequence identifiers, i.e. in plain txt format but with sequence order retained");
        System.out.println();
        System.exit(0);
    }

    private static void print_args()
    {
        System.out.println("\t     infile: " + infile);
        System.out.println("\t    outfile: " + outfile);
        System.out.println("\t     thread: " + THREAD);
        System.out.println("\t identifier: " + (identifiers_retained ? "retained" : "not retained"));
        System.out.println("\tforce align: " + (force_align ? "true" : "false"));
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