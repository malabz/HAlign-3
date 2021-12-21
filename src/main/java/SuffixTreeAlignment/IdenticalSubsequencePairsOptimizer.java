package SuffixTreeAlignment;

import Utilities.Fasta;
import Utilities.Pseudo;
import Utilities.UtilityFunctions;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
//        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        final String[] similarities = { "60", "70", "80", "85", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99" };
        final String infile = "C:\\Users\\heartunderblade\\Documents\\papers\\papers\\experiment\\tangfurong\\simulate_", postfix = ".fasta";

        for (var similarity : similarities)
        {
            final var fasta = new Fasta(infile + similarity + postfix);
            final int n = fasta.get_sequence_number();
            var lengths = new int[2][n];
            var counts = new int[2][n];
            for (int c = 0; c != 1; ++c)
            {
                final var centre = Pseudo.string_to_pseudo(fasta.get_sequence(c));
                final var st = new SuffixTree(centre);
                for (int i = 1; i != n; ++i)
                {
                    final var rhs = Pseudo.string_to_pseudo(fasta.get_sequence(i));

                    final var optimized_common_substrings = IdenticalSubsequencePairsOptimizer.optimize(st.get_identical_subsequence_pairs(rhs));
                    check_pairs(centre, rhs, optimized_common_substrings);
                    for (int j = 0; j != optimized_common_substrings.length; ++j)
                        lengths[0][i] += optimized_common_substrings[j][2];
                    counts[0][i] = optimized_common_substrings.length;

                    final var original_common_substrings = st.align_with(rhs);
                    check_pairs(centre, rhs, original_common_substrings);
                    for (int j = 0; j != original_common_substrings.length; ++j)
                        lengths[1][i] += original_common_substrings[j][2];
                    counts[1][i] = original_common_substrings.length;
                }
            }

            // 中心序列名, 参比序列名, 中心序列长度, 相似性, 新同源字符串长度和, 新同源子字符串数量, 新同源子字符串平均长度, 旧同源子字符串长度和, 旧同源子字符串数量, 旧同源子字符串平均长度
            try (var bw = new BufferedWriter(new FileWriter(infile + similarity + ".txt")))
            {
                final int length_of_centre = fasta.get_sequence(0).length();
                for (int c = 0; c != 1; ++c)
                    for (int i = 1; i != n; ++i)
                    {
                        bw.write(fasta.get_sequence_identifier(0));
                        bw.write(','); bw.write(fasta.get_sequence_identifier(i));
                        bw.write(','); bw.write(String.valueOf(length_of_centre));
                        bw.write(','); bw.write(similarity);
                        bw.write(','); bw.write(String.valueOf(lengths[0][i]));
                        bw.write(','); bw.write(String.valueOf(counts[0][i]));
                        bw.write(','); bw.write(String.valueOf(counts[0][i] == 0 ? -1 : lengths[0][i] / counts[0][i]));
                        bw.write(','); bw.write(String.valueOf(lengths[1][i]));
                        bw.write(','); bw.write(String.valueOf(counts[1][i]));
                        bw.write(','); bw.write(String.valueOf(counts[1][i] == 0 ? -1 : lengths[0][i] / counts[0][i]));
                        bw.newLine();
                    }
            }
            catch (IOException e)
            {
                System.err.println("fatal error: cannot write file ");
                System.exit(1);
            }
        }
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
