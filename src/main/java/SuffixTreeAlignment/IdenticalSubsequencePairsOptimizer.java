package SuffixTreeAlignment;

import Utilities.Fasta;
import Utilities.Pseudo;
import Utilities.UtilityFunctions;
import SequenceDistance.OptimalStringAlignmentDistance;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

class IdenticalSubsequencePairsOptimizer
{

    static int[] topological_sort(int[][] dag)
    {
        final int size = dag.length;
        var indegree = new int[size];
        for (int j = 0; j != size; ++j)
            for (int i = 0; i != size; ++i)
                if (dag[i][j] > 0)
                    ++indegree[j];

        var topological_sequence = new int[size];
        for (int pending = size, count = 0; pending != 0; )
            for (int i = 0; i != size; ++i)
                if (indegree[i] == 0)
                {
                    indegree[i] = Integer.MAX_VALUE;
                    topological_sequence[count++] = i;
                    --pending;

                    for (int j = 0; j != size; ++j)
                        if (dag[i][j] > 0) --indegree[j];
                }

        return topological_sequence;
    }

    static int[] dp(int[][] weight)
    {
        final int size = weight.length;

        var distance = new int[size];
        var path = new int[size];

        var topological_sequence = topological_sort(weight);
        for (int to = 1; to != size; ++to)
            for (int station = 0; station != to; ++station)
                if (weight[topological_sequence[station]][topological_sequence[to]] != 0)
                {
                    int challenger = distance[topological_sequence[station]] +
                            weight[topological_sequence[station]][topological_sequence[to]];
                    if (challenger > distance[topological_sequence[to]])
                    {
                        distance[topological_sequence[to]] = challenger;
                        path[topological_sequence[to]] = station;
                    }
                }

        return path;
    }

    static int[] trace_back(int[] path)
    {
        final int size = path.length;

        var al = new ArrayList<Integer>();
        al.ensureCapacity(size);
        for (int i = size - 1; i != 0; i = path[i])
            al.add(i);
        al.add(0);
        return UtilityFunctions.reverse(al);
    }

    static int[][] remove_overlap(int[][] isp)
    {
        var al = new ArrayList<Integer>();

        for (int i = 1; i != isp.length - 1; ++i)
        {
            al.add(isp[i][0]);
            al.add(isp[i][1]);
            al.add(isp[i][2]);

            int overlap = Math.max(0, Math.max(isp[i][0] + isp[i][2] - isp[i + 1][0], isp[i][1] + isp[i][2] - isp[i + 1][1]));
            isp[i + 1][0] += overlap;
            isp[i + 1][1] += overlap;
            isp[i + 1][2] -= overlap;
        }

        return UtilityFunctions.to_2d_array(al, 3);
    }

    static int len;

    // TODO: 改用邻接表
    static int[][] optimize(int[][] isp)
    {
        if (isp.length == 0) return new int[0][3];

        len = isp.length;

        var dag = new int[len][len]; // graph
        for (int i = 0; i != len; ++i)
            for (int j = 0; j != len; ++j)
                if (i != j && isp[i][0] < isp[j][0] + isp[j][2]
                        && isp[i][1] < isp[j][1] + isp[j][2])
                {
                    int possible_overlap = Math.max(isp[i][0] + isp[i][2] - isp[j][0], isp[i][1] + isp[i][2] - isp[j][1]);
                    dag[i][j] = possible_overlap > 0 ? isp[j][2] - possible_overlap : isp[j][2];
                }

        var path = dp(dag);
        path = trace_back(path);

        var r = new int[path.length][];
        for (int i = 0; i != path.length; ++i) r[i] = isp[path[i]];
        return remove_overlap(r);
    }

    public static void main(String[] args)
    {
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        var fasta = new Fasta("dataset/SARS-CoV-2_20200417.fasta");
//        var fasta = new Fasta("dataset/gisaid_hcov-19_2020_09_01_08_tmp.fasta");
        var differences = new int[fasta.get_sequence_number()][2];

//        var sequences = Pseudo.string_to_pseudo(fasta.get_sequences());
//        var distance_matrix = new int[sequences.length][sequences.length];
//        for (int i = 0; i != sequences.length - 1; ++i)
//        {
//            for (int j = i + 1; j != sequences.length; ++j)
//                distance_matrix[i][j] = SequenceDistance.OptimalStringAlignmentDistance.get_distance(sequences[i], sequences[j]);
//            System.out.println("" + i);
//        }

        long total_time_consuming = 0, counterpart_total_time_consuming = 0;
        for (int c = 0; c != fasta.get_sequence_number(); ++c)
        {
            int[] coverages = new int[fasta.get_sequence_number()], counterpart_coverages = new int[fasta.get_sequence_number()];
            int sum = 0, counterpart_sum = 0;
            int min = Integer.MAX_VALUE;
            int plus = 0, minus = 0;
            int plus_sum = 0, minus_sum = 0;

            var centre = Pseudo.string_to_pseudo(fasta.get_sequence(c));
            var st = new SuffixTree(centre);
            long total_length_of_sequences = 0;
            for (int i = 0; i != fasta.get_sequence_number(); ++i)
            {
                long stop;
                var rhs = Pseudo.string_to_pseudo(fasta.get_sequence(i));
                total_length_of_sequences += rhs.length;

                stop = System.currentTimeMillis();
                var result = IdenticalSubsequencePairsOptimizer.optimize(st.get_identical_subsequence_pairs(rhs));
                total_time_consuming += System.currentTimeMillis() - stop;
                check_pairs(centre, rhs, result);
                int coverage = 0;
                for (int j = 0; j != result.length; ++j) coverage += result[j][2];
                coverages[i] = coverage;
                sum += coverage;
                if (coverage < min) min = coverage;

                stop = System.currentTimeMillis();
                var counterpart_result = st.align_with(rhs);
                counterpart_total_time_consuming += System.currentTimeMillis() - stop;
                check_pairs(centre, rhs, counterpart_result);
                int counterpart_coverage = 0;
                for (int j = 0; j != counterpart_result.length; ++j) counterpart_coverage += counterpart_result[j][2];
                counterpart_coverages[i] = counterpart_coverage;
                counterpart_sum += counterpart_coverage;
                if (coverage > counterpart_coverage) { ++plus; plus_sum += coverage - counterpart_coverage; }
                if (coverage < counterpart_coverage) { ++minus; minus_sum += counterpart_coverage - coverage; }
            }

            for (int i = 0; i != fasta.get_sequence_number(); ++i)
                if (coverages[i] != counterpart_coverages[i])
                {
                    differences[c][0] += coverages[i];
                    differences[c][1] += counterpart_coverages[i];
                }
//            System.out.printf("      sum = %d\n", sum);
//            System.out.printf("    c_sum = %d\n", counterpart_sum);
//            System.out.printf("     plus = %d\n", plus);
//            System.out.printf("    minus = %d\n", minus);
//            System.out.printf(" plus_sum = %d\n", plus_sum);
//            System.out.printf("minus_sum = %d\n", minus_sum);
//            System.out.printf("      min = %d\n", min);
//            System.out.printf("  average = %.1f\n", 1. *             sum / fasta.get_sequence_number());
//            System.out.printf("c_average = %.1f\n", 1. * counterpart_sum / fasta.get_sequence_number());
//            System.out.printf("     time = %d\n", total_time_consuming);
//            System.out.printf("   c_time = %d\n", counterpart_total_time_consuming);
//            System.out.printf(" a_length = %.1f\n", (float)total_length_of_sequences / fasta.get_sequence_number());
//            Arrays.sort(            coverages); for (int i = 0; i != 100 ; ++i) System.out.printf("%7d",             coverages[i]); System.out.println();
//            Arrays.sort(counterpart_coverages); for (int i = 0; i != 100; ++i) System.out.printf("%7d", counterpart_coverages[i]); System.out.println();
//            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        }
        System.out.printf("     time = %d\n", total_time_consuming);
        System.out.printf("   c_time = %d\n", counterpart_total_time_consuming);

//        try (var bw = new BufferedWriter(new FileWriter("D:/tmp3.txt")))
//        {
//            for (int i = 0; i != fasta.get_sequence_number(); ++i)
//            {
//                bw.write("" + differences[i][0] + "," + differences[i][1]);
//                if (i != fasta.get_sequence_number()) bw.newLine();
//                bw.flush();
//            }
//        }
//        catch (IOException e)
//        {
//            System.err.println("fatal error: cannot write file ");
//            System.exit(1);
//        }
    }

    static private void print_name(int[][] name)
    {
        for (int i = 0; i != name.length; ++i)
            System.out.printf("%7d%7d%7d%7d%7d\n",
                    name[i][0], name[i][0] + name[i][2],
                    name[i][1], name[i][1] + name[i][2],
                    name[i][2]);
        System.out.println();
    }

    static private void print_matrix(int[][] matrix)
    {
        final int len = matrix.length;
        System.out.print("         ");
        for (int j = 0; j != len; ++j) System.out.printf("%9d", j);
        System.out.println();
        for (int i = 0; i != len; ++i)
        {
            System.out.printf("%9d", i);
            for (int j = 0; j != len; ++j) System.out.printf("%9d", matrix[i][j]);
            System.out.println();
        }
        System.out.println();
    }

    static void check_pairs(byte[] centre, byte[] sequence, int[][] pairs)
    {
        for (int j = 0; j != pairs.length; ++j)
        {
            assert pairs[j][0] >= 0;
            assert pairs[j][1] >= 0;
            assert pairs[j][2] >  0;
            assert pairs[j][0] + pairs[j][2] <= centre.length;
            assert pairs[j][1] + pairs[j][2] <= sequence.length;
            assert j == 0 ||
                    pairs[j][0] >= pairs[j - 1][0] + pairs[j - 1][2] && pairs[j][1] >= pairs[j - 1][1] + pairs[j - 1][2];
            for (int k = pairs[j][0], l = pairs[j][1]; k != pairs[j][0] + pairs[j][2]; ++k, ++l)
                assert centre[k] == sequence[l];
        }
    }

}
