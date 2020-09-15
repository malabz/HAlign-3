package SuffixTreeAlignment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import Utilities.Pair;
import Utilities.Pseudo;
import Utilities.UtilityFunctions;

import static Main.GlobalVariables.*;

/**
 * 用于对dna, rna比对结果的后续处理
 */
@SuppressWarnings("unused")
class PostProcessor implements Pseudo
{

    private byte[][] pseudo;
    private int row, old_column;
    private boolean[] contains_gap;
    private byte[] most_char_of_column;
    private int[] endpoints;
    private ArrayList<Pair<Integer, Integer>> chaotic_areas;
    private byte[][][] substitutes;

    /**
     * 处理函数
     *
     * @param pseudo 比对完成的伪序列
     * @return 后续处理完成的伪序列
     */
    public byte[][] post_process(byte[][] pseudo)
    {
        initialise(pseudo);
        select();
        if (chaotic_areas.size() != 0)
        {
            post_align();
            // output_chaotic_part();
            return put_together();
        }
        else
        {
            return pseudo;
        }
    }

    private void initialise(byte[][] pseudo)
    {
        this.pseudo = pseudo;
        old_column = pseudo[0].length;
        row = pseudo.length;
        count();
    }

    private void count()
    {
        contains_gap = new boolean[old_column];
        most_char_of_column = new byte[old_column];
        var char_frequency = new int[old_column][CHAR_KIND];
        for (int i = 0; i != row; ++i) for (int j = 0; j != old_column; ++j) ++char_frequency[j][pseudo[i][j]];
        for (int j = 0; j != old_column; ++j)
        {
            if (char_frequency[j][GAP] != 0) contains_gap[j] = true;
            for (byte curr_char = 1; curr_char != CHAR_KIND; ++curr_char)
                if (char_frequency[j][curr_char] > char_frequency[j][most_char_of_column[j]])
                    most_char_of_column[j] = curr_char;
        }
        var al = new ArrayList<Integer>();
        al.add(0);
        for (int j = 1; j != old_column; ++j)
            if ((most_char_of_column[j - 1] == GAP) != (most_char_of_column[j] == GAP)) al.add(j);
        al.add(old_column);
        endpoints = UtilityFunctions.to_array(al);
    }

    private void select()
    {
        var is_unstable = new boolean[old_column];
        for (int i = most_char_of_column[0] == GAP ? 1 : 0; i < endpoints.length - 1; i += 2)
            if (determine_if_unstable(endpoints[i], endpoints[i + 1]))
                for (int j = endpoints[i]; j != endpoints[i + 1]; ++j) is_unstable[j] = true;
        var al = new ArrayList<Integer>();
        for (int j = 0; j < old_column; ++j) // 待检查
            if (is_unstable[j])
            {
                int l = j, r = j + 1;
                while (l != 0 && most_char_of_column[l - 1] == GAP) --l;
                if (l != 0)
                {
                    int probe = l;
                    byte fringe = most_char_of_column[probe - 1];
                    while (probe != 0 && most_char_of_column[probe - 1] == fringe) --probe;
                    while (probe != l && !contains_gap[probe]) ++probe;
                    l = probe;
                }
                while (r != old_column && (is_unstable[r] || most_char_of_column[r] == GAP)) ++r;
                if (r != old_column)
                {
                    int probe = r;
                    byte fringe = most_char_of_column[probe];
                    while (probe != old_column && most_char_of_column[probe] == fringe) ++probe;
                    while (probe != r && !contains_gap[probe - 1]) --probe;
                    r = probe;
                }
                al.add(l);
                al.add(r);
                j = r;
            }
        for (int i = 2; i < al.size(); i += 2) if (al.get(i) < al.get(i - 1)) al.set(i - 1, al.get(i));
        chaotic_areas = new ArrayList<>(al.size() / 2);
        for (int i = 0; i != al.size(); i += 2)
            if (al.get(i).compareTo(al.get(i + 1)) < 0) chaotic_areas.add(new Pair<>(al.get(i), al.get(i + 1)));
    }

    private boolean determine_if_unstable(int lhs, int rhs)
    {
        final int UNSTABLE_IF_LESS_THAN = 4, LENGTH_CEILING = 32, DIVISOR = 8;
        int length = rhs - lhs;
        if (length < UNSTABLE_IF_LESS_THAN) return true;
        if (length < LENGTH_CEILING)
        {
            var counts = new int[CHAR_KIND];
            for (; lhs != rhs; ++lhs) ++counts[most_char_of_column[lhs]];
            Arrays.sort(counts);
            return counts[CHAR_KIND - 3] == 0 && counts[CHAR_KIND - 2] <= Math.max(2, counts[CHAR_KIND - 1] / DIVISOR);
        }
        return false;
    }

    private void post_align()
    {
        substitutes = new byte[chaotic_areas.size()][][];
        for (int i = 0; i != chaotic_areas.size(); ++i)
        {
            var pending_alignment = new byte[row][];
            for (int j = 0; j != row; ++j)
                pending_alignment[j] = Pseudo.remove_spaces(pseudo[j], chaotic_areas.get(i).get_first(), chaotic_areas.get(i).get_second());
            substitutes[i] = new Realigner(pending_alignment).align();
        }
        // for (int i = 0; i != chaotic_areas.size(); ++i) substitutes[i] = post_align(chaotic_areas.get(i).get_first(), chaotic_areas.get(i).get_second());
    }

    private byte[][] put_together()
    {
        int new_column = old_column;
        for (int i = 0; i != chaotic_areas.size(); ++i)
            new_column += chaotic_areas.get(i).get_first() - chaotic_areas.get(i).get_second() + substitutes[i][0].length;
        var ret = new byte[row][];
        for (int i = 0; i != row; ++i)
        {
            var curr = new byte[new_column];
            int index = 0;
            for (int j = 0; j != chaotic_areas.size(); ++j)
            {
                int src_pos = j == 0 ? 0 : chaotic_areas.get(j - 1).get_second(), length_added = chaotic_areas.get(j).get_first() - src_pos;
                System.arraycopy(pseudo[i], src_pos, curr, index, length_added);
                index += length_added;
                for (int k = 0; k != substitutes[j][i].length; ++k)
                    curr[index++] = substitutes[j][i][k];
            }
            System.arraycopy(pseudo[i], chaotic_areas.get(chaotic_areas.size() - 1).get_second(), curr, index, old_column - chaotic_areas.get(chaotic_areas.size() - 1).get_second());
            ret[i] = curr;
        }
        return ret;
    }

//    private byte[][] post_align(int l, int r) {
//        Comparator<byte[]> basic_comparator = (byte[] lhs, byte[] rhs) -> { // 长度和较低位字符
//            if (lhs.length != rhs.length) return rhs.length - lhs.length;
//            else for (int i = 0; i != lhs.length; ++i) if (lhs[i] != rhs[i]) return rhs[i] - lhs[i];
//            return 0;
//        };
//        var sub_original_without_dash = new byte[row][];
//        var frequency_map = new TreeMap<byte[], Integer>(basic_comparator);
//        for (int i = 0; i != row; ++i) { // 统计频数
//            sub_original_without_dash[i] = Pseudo.remove_dash_from(pseudo[i], l, r);
//            frequency_map.merge(sub_original_without_dash[i], 1, Integer::sum);
//        }
//        int max_length = frequency_map.firstEntry().getKey().length;
//        var result_map = new TreeMap<byte[], byte[]>(basic_comparator);
//        var realigner = new Realigner(max_length);
//        frequency_map.forEach((k, v) -> {
//            if (k.length == max_length) {
//                result_map.put(k, k);
//                realigner.add(k, v);
//            } else {
//                var k_realigned = realigner.realign(k);
//                result_map.put(k, k_realigned);
//                realigner.add(k_realigned, v);
//            }
//        });
//        var ret = new byte[row][max_length];
//        for (int i = 0; i != row; ++i) ret[i] = result_map.get(sub_original_without_dash[i]);
//        return ret;
//    }
//
//    static private class Realigner {
//
//        private int column, row;
//        private int[][] frequency;
//        private double[][] relative_frequency;
//
//        Realigner(int length) {
//            row = 0;
//            column = length;
//            frequency = new int[column][CHAR_KIND];
//            relative_frequency = new double[column][CHAR_KIND];
//        }
//
//        void add(byte[] realigned_substr, int number) {
//            row += number;
//            for (int i = 0; i != column; ++i) frequency[i][realigned_substr[i]] += number;
//            for (int i = 0; i != column; ++i) for (byte j = 0; j != CHAR_KIND; ++j) relative_frequency[i][j] = (double) frequency[i][j] / row;
//        }
//
//        private double calculate_sp_of_one_column(byte ch, int curr_column) {
//            double ret = 0;
//            for (byte i = 0; i != CHAR_KIND; ++i)
//                ret += relative_frequency[curr_column][i] * (i == ch ? Pairwise.AffinePenalty.MATCH_SCORE : Pairwise.AffinePenalty.MISMATCH_SCORE);
//            return ret;
//        }
//
//        byte[] realign(byte[] original_without_dash) {
//            int optimal_insert_position = 0, space_number = column - original_without_dash.length;
//            double max_sp = 0;
//            double[][] calculated_partial_sp = new double[original_without_dash.length][2];
//            boolean[][] is_calculated = new boolean[original_without_dash.length][2];
//            for (int insert_position = 0; insert_position <= original_without_dash.length; ++insert_position) {
//                double curr_sp = 0;
//                for (int i = 0; i != original_without_dash.length; ++i) {
//                    int relative_position = i < insert_position ? 0 : 1;
//                    if (!is_calculated[i][relative_position]) {
//                        calculated_partial_sp[i][relative_position] = calculate_sp_of_one_column(original_without_dash[i], relative_position == 0 ? i : i + space_number);
//                        is_calculated[i][relative_position] = true;
//                    }
//                    curr_sp += calculated_partial_sp[i][relative_position];
//                }
//                if (max_sp < curr_sp) {
//                    max_sp = curr_sp;
//                    optimal_insert_position = insert_position;
//                }
//            }
//            var ret = new byte[column];
//            System.arraycopy(original_without_dash, 0, ret, 0, optimal_insert_position);
//            System.arraycopy(original_without_dash, optimal_insert_position, ret, optimal_insert_position + space_number, original_without_dash.length - optimal_insert_position);
//            for (int i = optimal_insert_position; i != optimal_insert_position + space_number; ++i) ret[i] = GAP;
//            return ret;
//        }
//
//    }

    @SuppressWarnings("unused")
    static private class StringComparisonByLength implements Comparator<String>
    {
        @Override
        public int compare(String lhs, String rhs)
        {
            int ret = lhs.length() - rhs.length();
            return ret == 0 ? UtilityFunctions.edict_distance_of(lhs, rhs) : ret;
        }
    }

    private void output_chaotic_part()
    {
        String prefix = "debug\\chaotic_part", fasta_fix = ".fasta", txt_fix = ".txt", aligned = "_aligned";
        for (int i = 0; i != chaotic_areas.size(); ++i)
        {
            try (var bw = new BufferedWriter(new FileWriter(prefix + i + fasta_fix)))
            {
                for (int j = 0; j != row; ++j)
                {
                    bw.write(">" + j + '\n');
                    bw.write(Pseudo.pseudo2string(Pseudo.remove_spaces(pseudo[j], chaotic_areas.get(i).get_first(), chaotic_areas.get(i).get_second())));
                    if (j != row - 1) bw.write('\n');
                }
            }
            catch (IOException e)
            {
                System.err.println("chaotic_part write err");
            }
            try (var bw = new BufferedWriter(new FileWriter(prefix + i + aligned + txt_fix)))
            {
                for (int j = 0; j != row; ++j)
                {
                    bw.write(Pseudo.pseudo2string(substitutes[i][j]));
                    if (j != row - 1) bw.write('\n');
                }
            }
            catch (IOException e)
            {
                System.err.println("chaotic_part write err");
            }
        }
    }

}