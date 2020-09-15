package SuffixTreeAlignment;

import static Main.GlobalVariables.*;
import Utilities.Fasta;
import Utilities.Pseudo;
import Utilities.UtilityFunctions;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

// import java.text.SimpleDateFormat;
// import java.util.Date;

public final class Main
{

    private final String file_name;
    private final byte[][] sequences;
    private final int[] lengths;

    private Main(String infile)
    {
        file_name = infile;
        String[] source = new Fasta(file_name).get_sequences();
        this.sequences = new byte[source.length][];
        initialise(source);
        lengths = new int[sequences.length];
        for (int i = 0; i != sequences.length; ++i) lengths[i] = sequences[i].length;
    }

    private void initialise(String[] source)
    {
        for (int i = 0; i != source.length; ++i) sequences[i] = Pseudo.string2pseudo(source[i]);
    }

    public static Fasta align(String infile, boolean force_align)
    {
        return new Main(infile).align(force_align);
    }

    private boolean[] detect_outliers(int[][] groups)
    {
        var group_average = new double[groups.length];
        for (int i = 0; i != groups.length; ++i)
        {
            long sum = 0;
            for (int j = 0; j != groups[i].length; ++j) sum += lengths[groups[i][j]];
            group_average[i] = (double)sum / groups[i].length;
        }
        double average = UtilityFunctions.average_of(lengths);
        double standard_deviation = UtilityFunctions.standard_deviation_of(lengths);

//        for (int i = 0; i != group_average.length; ++i) System.out.printf("%9.1f", Math.abs(group_average[i] - average));
//        System.out.printf("\n%9.1f%9.1f\n", average, standard_deviation);

        var is_outlier = new boolean[groups.length];
        int threshold_factor = 0;
        do // 确保一定有比对内容
        {
            for (int i = 0; i != groups.length; ++i)
                if (Math.abs(group_average[i] - average) > standard_deviation * ++threshold_factor)
                    is_outlier[i] = true;
        } while (UtilityFunctions.count(is_outlier) == groups.length);

        return is_outlier;
    }

    private Fasta align(boolean force_align)
    {
        var groups = Clustering.LengthClustering.cluster(lengths);
        var is_outlier = detect_outliers(groups);
        int outlier_number = UtilityFunctions.count(is_outlier);

        var jobs = new byte[groups.length][][];
        var aligned_jobs = new byte[groups.length][][];
        for (int i = 0; i != groups.length; ++i)
        {
            if (force_align || !is_outlier[i])
            {
                System.out.println("blocks[" + i + "]: ");
                jobs[i] = new byte[groups[i].length][];
                for (int j = 0; j != groups[i].length; ++j) jobs[i][j] = sequences[groups[i][j]];
                aligned_jobs[i] = SuffixTreeAligner.align(jobs[i]);
            }
            else
            {
                System.out.println("block[" + i + "] has been recognised as an outlier, which is automatically ignored");
                System.out.println();
            }
        }

        System.out.println("merge: ");
        var centres = new byte[jobs.length - (force_align ? 0 : outlier_number)][];
        for (int i = 0, cnt = 0; i != jobs.length; ++i)
            if (force_align || !is_outlier[i]) centres[cnt++] = extract_centre_sequence(aligned_jobs[i]);
        for (int i = 0; i != centres.length; ++i)
            if (centres[i] != null)
                for (int j = 0; j != centres[i].length; ++j)
                    if (centres[i][j] == GAP) centres[i][j] = UNKNOWN;
        var spaces = SuffixTreeAligner.spaces(centres);
//        for (int i = 0; i != centres.length; ++i) System.out.println(Pseudo.pseudo2string(Pseudo.insert_spaces(centres[i], spaces[i])));

        var aligned = new byte[sequences.length][];
        for (int i = 0, cnt = 0; i != aligned_jobs.length; ++i)
            if (aligned_jobs[i] != null)
            {
                for (int j = 0; j != aligned_jobs[i].length; ++j)
                    aligned[groups[i][j]] = Pseudo.insert_spaces(aligned_jobs[i][j], spaces[cnt]);
                ++cnt;
            }

        var infile = new Fasta(file_name);
        if (!force_align && outlier_number != 0)
        {
            var outlier_file_name = (file_name.endsWith(".fasta") ? file_name.substring(0, file_name.length() - 6) : file_name) + ".outlier.fasta";
            try (var bw = new BufferedWriter(new FileWriter(outlier_file_name)))
            {
                for (int i = 0; i != sequences.length; ++i)
                {
                    if (aligned[i] == null)
                    {
                        bw.write(infile.get_sequence_identifiers(i));
                        bw.newLine();
                        bw.write(infile.get_sequence(i));
                        bw.newLine();
                        bw.flush();
                    }
                }
            }
            catch (IOException e)
            {
                System.err.println("cannot write file " + outlier_file_name);
            }
            System.out.println("the outliers have been stored to file " + outlier_file_name);
            System.out.println("you could use [-F] to align all the sequences included in the input file");
            System.out.println();
        }

        var output_sequences = new String[sequences.length];
        var output_sequence_identifiers = new String[sequences.length];
        for (int i = 0; i != sequences.length; ++i)
        {
            if (aligned[i] != null)
            {
                output_sequences[i] = merge(aligned[i], infile.get_sequence(i));
                output_sequence_identifiers[i] = infile.get_sequence_identifiers(i);
            }
        }
        return new Fasta(output_sequences, output_sequence_identifiers);
    }

    private byte[] extract_centre_sequence(byte[][] aligned)
    {
        int row = aligned.length, column = aligned[0].length;
        var frequency = new int[column][CHAR_KIND];
        for (int i = 0; i != row; ++i) for (int j = 0; j != column; ++j) ++frequency[j][aligned[i][j]];

        var ret = new byte[column];
        for (int j = 0; j != column; ++j)
        {
            byte most_char = 0;
            for (byte curr_char = 1; curr_char != CHAR_KIND; ++curr_char)
                if (frequency[j][curr_char] > frequency[j][most_char]) most_char = curr_char;
            ret[j] = most_char;
        }
        return ret;
    }

    private String merge(byte[] pseudo, String origin)
    {
        var sb = new StringBuilder(pseudo.length);
        for (int i = 0, origin_index = 0; i != pseudo.length; ++i)
            sb.append(pseudo[i] == GAP ? '-' : origin.charAt(origin_index++));
        return sb.toString();
    }

    public static void main(String[] args)
    {
        THREAD = 1;
        long start = System.currentTimeMillis();
//        Main.align("data-set\\SARS-CoV-2_20200417_refined.fasta", false).output("debug\\a.txt", false);
//        Main.align("data-set\\mt_genome_1x.fasta", false).output("debug\\a.txt", false);
//        Main.align("data-set\\NCBI_H7N9_HA.fasta", false).output("debug\\a.txt", false);
//        Main.align("data-set\\Animals_organelles_9685_9680SEQ_CDHIT9648.fasta", false).output("debug\\a.txt", false);
        Main.align("c:\\Users\\heartunderblade\\Documents\\lab\\temporary\\20200902\\gisaid_hcov-19_2020_09_01_08.fasta", false).output("debug\\1.txt", false);
        System.out.println("total time consumed: " + (System.currentTimeMillis() - start));
    }

}
