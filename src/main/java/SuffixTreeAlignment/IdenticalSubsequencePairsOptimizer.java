package SuffixTreeAlignment;

import Utilities.Fasta;
import Utilities.Pseudo;
import Utilities.UtilityFunctions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

class IdenticalSubsequencePairsOptimizer
{

    static int len;

    // TODO: 改用邻接表
    static int[][] optimize(int[][] isp)
    {
        len = isp.length;
//        print_name(isp);

        var dag = new int[len][len]; // graph
        for (int i = 0; i != len; ++i)
            for (int j = 0; j != len; ++j)
                if (i != j && isp[i][0] < isp[j][0] + isp[j][2]
                           && isp[i][1] < isp[j][1] + isp[j][2])
                {
                    int overlap = Math.max(isp[i][0] + isp[i][2] - isp[j][0], isp[i][1] + isp[i][2] - isp[j][1]);
                    dag[i][j] = overlap > 0 ? isp[j][2] - overlap : isp[j][2];
                }
//        print_matrix(dag);

//        int[][] dag =
//        {
//            { 0, 3, 2, 0 },
//            { 0, 0, 0, 5 },
//            { 0, 2, 0, 4 },
//            { 0, 0, 0, 0 },
//        };

        var path = dp(dag);
//        print_matrix(dp_matrix);
//        print_matrix(path_matrix);
        path = trace_back(path);

        var r = new int[path.length][];
        for (int i = 0; i != path.length; ++i) r[i] = isp[path[i]];
        return remove_overlap(r);
    }

    static int[] dp(int[][] weight)
    {
        var distance = new int[len];
        var path = new int[len];

        for (int to = 1; to != len; ++to)
            for (int station = 0; station != to; ++station)
                if (weight[station][to] != 0)
                {
                    int curr = distance[station] + weight[station][to];
                    if (curr > distance[to])
                    {
                        distance[to] = curr;
                        path[to] = station;
                    }
                }
        return path;
    }

    static int[] trace_back(int[] path)
    {
        var al = new ArrayList<Integer>();
        al.ensureCapacity(path.length);

        for (int i = len - 1; i != 0; i = path[i])
            al.add(i);
        al.add(0);
        return UtilityFunctions.reverse(al);
    }

    static int[][] remove_overlap(int[][] isp)
    {
        var al = new ArrayList<Integer>();

        for (int i = 1; i < isp.length - 2; ++i)
            if (isp[i][2] != 0)
            {
                al.add(isp[i][0]);
                al.add(isp[i][1]);
                al.add(isp[i][2]);

                int overlap = Math.max(0, Math.max(isp[i][0] + isp[i][2] - isp[i + 1][0], isp[i][1] + isp[i][2] - isp[i + 1][1]));
                isp[i + 1][0] += overlap;
                isp[i + 1][1] += overlap;
                isp[i + 1][2] -= overlap;
            }

        al.add(isp[isp.length - 2][0]);
        al.add(isp[isp.length - 2][1]);
        al.add(isp[isp.length - 2][2]);

        return UtilityFunctions.to_2d_array(al, 3);
    }

    public static void main(String[] args)
    {
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        var fasta = new Fasta("data-set/SARS-CoV-2_20200417.fasta");
//        var fasta = new Fasta("data-set/gisaid_hcov-19_2020_09_01_08_tmp.fasta");
        int[] coverages = new int[fasta.get_sequence_number()], counterpart_coverages = new int[fasta.get_sequence_number()];
        int sum = 0, counterpart_sum = 0;
        int min = Integer.MAX_VALUE;
        int plus = 0, minus = 0;
        int plus_sum = 0, minus_sum = 0;
        long total_time_consuming = 0, counterpart_total_time_consuming = 0;

        var st = new SuffixTree(Pseudo.string_to_pseudo(fasta.get_sequence(0)));
        for (int i = 0; i != fasta.get_sequence_number(); ++i)
        {
            long stop;
            var rhs = Pseudo.string_to_pseudo(fasta.get_sequence(i));

            stop = System.currentTimeMillis();
            var result = IdenticalSubsequencePairsOptimizer.optimize(st.get_identical_subsequence_pairs(rhs));
            total_time_consuming += System.currentTimeMillis() - stop;
            int coverage = 0;
            for (int j = 0; j != result.length; ++j) coverage += result[j][2];
            coverages[i] = coverage;
            sum += coverage;
            if (coverage < min) min = coverage;

            stop = System.currentTimeMillis();
            var counterpart_result = st.align_with(rhs);
            counterpart_total_time_consuming += System.currentTimeMillis() - stop;
            int counterpart_coverage = 0;
            for (int j = 0; j != counterpart_result.length; ++j) counterpart_coverage += counterpart_result[j][2];
            counterpart_coverages[i] = counterpart_coverage;
            counterpart_sum += counterpart_coverage;
            if (coverage > counterpart_coverage) { ++plus; plus_sum += coverage - counterpart_coverage; }
            if (coverage < counterpart_coverage) { ++minus; minus_sum += counterpart_coverage - coverage; }
        }
        System.out.printf("     plus = %d\n", plus);
        System.out.printf("    minus = %d\n", minus);
        System.out.printf(" plus_sum = %d\n", plus_sum);
        System.out.printf("minus_sum = %d\n", minus_sum);
        System.out.printf("      min = %d\n", min);
        System.out.printf("  average = %.1f\n", 1. *             sum / fasta.get_sequence_number());
        System.out.printf("c_average = %.1f\n", 1. * counterpart_sum / fasta.get_sequence_number());
        System.out.printf("     time = %d\n", total_time_consuming);
        System.out.printf("   c_time = %d\n", counterpart_total_time_consuming);
        Arrays.sort(            coverages); for (int i = 0; i != 20 ; ++i) System.out.printf("%7d",             coverages[i]); System.out.println();
        Arrays.sort(counterpart_coverages); for (int i = 0; i != 200; ++i) System.out.printf("%7d", counterpart_coverages[i]); System.out.println();
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
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

}
