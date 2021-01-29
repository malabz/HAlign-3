package Pairwise;

import Utilities.Pair;
import Utilities.Pseudo;

import java.util.Arrays;

import static Main.GlobalVariables.*;

public class PoolAligner extends PairwiseAligner
{

    private static final int MATCH_SCORE = 3,
                             MISMATCH_SCORE = 0,
                             HORIZONTAL_SCORE = -2,
                             VERTICAL_SCORE = -31;

    private static final byte DIAGONAL = 0,
                              HORIZONTAL = 1,
                              VERTICAL = 2;

    private byte[][] sequences;
    private byte[][] alignments;
    private int[][] pool; // always being the centre, incrementally alignment
    private int curr_depth;
    private int longest_index;

    private PoolAligner() {}

    public static byte[][] align(byte[][] src)
    {
        final int row = src.length, clm = src[0].length;
        int bgn , end;
//        bgn = end = clm / 32;
        for (bgn = 0; bgn != clm; ++bgn)
        {
//            int cnt = 0;
//            for (int i = 0; i != row; ++i)
//                if (src[i][bgn] == GAP) ++cnt;
//            if (cnt < row / 1024) break;
            boolean nongap = true;
            for (int i = 0; i != row; ++i)
                if (src[i][bgn] == GAP)
                {
                    nongap = false;
                    break;
                }
            if (nongap) break;
        }
        for (end = 0; end != clm; ++end)
        {
//            int gap_num = 0;
//            for (int i = 0; i != row; ++i)
//                if (src[i][clm - end - 1] == GAP) ++gap_num;
//            if (gap_num < row / 1024) break;
            boolean nongap = true;
            for (int i = 0; i != row; ++i)
                if (src[i][clm - end - 1] == GAP)
                {
                    nongap = false;
                    break;
                }
            if (nongap) break;
        }
        bgn += clm / 1024;
        end += clm / 1024;

        var bgn_src = new byte[row][];
        var end_src = new byte[row][];
        for (int i = 0; i != row; ++i)
        {
            bgn_src[i] = Pseudo.reverse(Pseudo.remove_spaces(src[i], 0, bgn));
            end_src[i] = Pseudo.remove_spaces(src[i], clm - end, clm);
        }

        var bgn_rst = new PoolAligner().do_align(bgn_src);
        for (int i = 0; i != bgn_rst.length; ++i) bgn_rst[i] = Pseudo.reverse(bgn_rst[i]);
        var end_rst = new PoolAligner().do_align(end_src);

        var result = new byte[row][bgn_rst[0].length + end_rst[0].length + clm - bgn - end];
        for (int i = 0; i != row; ++i)
        {
            System.arraycopy(bgn_rst[i], 0, result[i], 0, bgn_rst[i].length);
            System.arraycopy(src[i], bgn, result[i], bgn_rst[0].length, clm - bgn - end);
            System.arraycopy(end_rst[i], 0, result[i], result[i].length - end_rst[i].length, end_rst[i].length);
        }

//        assert_equal(src, result);
        return result;
    }

    private byte[][] do_align(byte[][] src)
    {
        sequences = src;
        init();

        for (int i = 0; i != src.length; ++i)
//        {
            if (src[i] != sequences[longest_index]) align_next(src[i]);
//            if ((i & 0xf) == 0xf) System.out.print('.');
//            if ((i & 0x7f) == 0x7f) System.out.println(" " + i);
//        }
//        System.out.println();

        uninit();
        return alignments;
    }

    private void init()
    {
        longest_index = 0;
        for (int i = 1; i != sequences.length; ++i)
            if (sequences[i].length > sequences[longest_index].length)
                longest_index = i;

        curr_depth = 1;
        pool = new int[sequences[longest_index].length][CHAR_KIND];
        for (int i = 0; i != sequences[longest_index].length; ++i) pool[i][sequences[longest_index][i]] = 1;

        alignments = new byte[sequences.length][];
        alignments[0] = sequences[longest_index];
    }

    private void uninit()
    {
        var longest = alignments[0];
        for (int i = 0; i != longest_index; ++i)
            alignments[i] = alignments[i + 1];
        alignments[longest_index] = longest;
    }

    private void align_next(byte[] curr_sequence)
    {
        var path = dp(curr_sequence);
        var spaces = trace_back(path);

        var last_alignment = Pseudo.insert_spaces(curr_sequence, spaces.get_first());

        var pool_spaces = spaces.get_second();
        int pool_insert = 0;
        for (int i = 0; i != pool_spaces.length; ++i) pool_insert += pool_spaces[i];

        if (pool_insert != 0)
        {
            var new_pool = new int[pool.length + pool_insert][CHAR_KIND];
            for (int i = 0, j = 0; i != pool.length; ++i)
            {
                for (int k = 0; k != pool_spaces[i]; ++k)
                    new_pool[j++] = new int[]{ curr_depth, 0, 0, 0, 0, 0 };
                new_pool[j++] = pool[i];
            }
            for (int k = 0, index = new_pool.length - 1; k != pool_spaces[pool.length]; ++k)
                new_pool[index--] = new int[]{ curr_depth, 0, 0, 0, 0, 0 };
            pool = new_pool;

            var new_alignments = new byte[sequences.length][];
            for (int i = 0; i != curr_depth; ++i)
            {
                var curr_alignment = new byte[pool.length];
                for (int j = 0, k = 0; j != alignments[i].length; ++j)
                {
                    Arrays.fill(curr_alignment, k, k += pool_spaces[j], GAP);
                    curr_alignment[k++] = alignments[i][j];
                }
                Arrays.fill(curr_alignment, curr_alignment.length - pool_spaces[pool_spaces.length - 1], curr_alignment.length, GAP);
                new_alignments[i] = curr_alignment;
            }
            alignments = new_alignments;
        }
        for (int i = 0; i != pool.length; ++i) ++pool[i][last_alignment[i]];
        alignments[curr_depth++] = last_alignment;

//        if (pool_insert != 0) output_alignments();
    }

    private byte[][] dp(byte[] curr_sequence)
    {
        var mtrx = new long[curr_sequence.length + 1][pool.length + 1];
        final int vertical_step_score = VERTICAL_SCORE * curr_depth;

        for (int i = 1; i <= curr_sequence.length; ++i) mtrx[i][0] = mtrx[i - 1][0] + vertical_step_score;
        for (int j = 1; j <= pool.length; ++j) mtrx[0][j] = mtrx[0][j - 1] + horizontal_step(j - 1);

        var path = new byte[mtrx.length][mtrx[0].length];
        for (int i = 1; i <= curr_sequence.length; ++i) path[i][0] = VERTICAL;
        for (int j = 1; j <= pool.length; ++j) path[0][j] = HORIZONTAL;

        for (int i = 1; i <= curr_sequence.length; ++i)
            for (int j = 1; j <= pool.length; ++j)
            {
                long diagonal = mtrx[i - 1][j - 1] + diagonal_step(j - 1, curr_sequence[i - 1]),
                     horizontal = mtrx[i][j - 1] + (i == curr_sequence.length ? 0 : horizontal_step(j - 1)),
                     vertical = mtrx[i - 1][j] + vertical_step_score;

                mtrx[i][j] = Math.max(Math.max(diagonal, horizontal), vertical);
//                if (Math.abs(mtrx[i][j]) > Long.MAX_VALUE >> 2) System.out.print('y');
                path[i][j] = mtrx[i][j] == diagonal ? DIAGONAL : mtrx[i][j] == horizontal ? HORIZONTAL : VERTICAL;
                path[i][j] = mtrx[i][j] == diagonal ? DIAGONAL : mtrx[i][j] == horizontal ? HORIZONTAL : VERTICAL;
            }

//        print_dp_matrix(curr_sequence, alignments[0], mtrx);
//        print_dp_matrix(curr_sequence, alignments[0], path);

        return path;
    }

    private Pair<int[], int[]> trace_back(byte[][] path)
    {
        var csqc_spaces = new int[path.length];
        var pool_spaces = new int[path[0].length];

        for (int csqc_index = csqc_spaces.length - 1, pool_index = pool_spaces.length - 1; csqc_index > 0 || pool_index > 0; )
            switch (path[csqc_index][pool_index])
            {
            case DIAGONAL:
                --pool_index;
                --csqc_index;
                break;
            case VERTICAL:
                ++pool_spaces[pool_index];
                --csqc_index;
                break;
            case HORIZONTAL:
                ++csqc_spaces[csqc_index];
                --pool_index;
                break;
            default:
                break;
            }

        return new Pair<>(csqc_spaces, pool_spaces);
    }

    private int diagonal_step(int pool_index, byte curr_char)
    {
        int match = 0, mismatch = 0;
        for (int i = A; i != UNKNOWN; ++i)
            if (curr_char == UNKNOWN || curr_char == i) match += pool[pool_index][i];
            else mismatch += pool[pool_index][i];
        match += pool[pool_index][UNKNOWN];
        return match * MATCH_SCORE + mismatch * MISMATCH_SCORE + pool[pool_index][GAP] * HORIZONTAL_SCORE;
    }

    private int horizontal_step(int pool_index)
    {
        int cnt = 0;

        for (int i = A; i != CHAR_KIND; ++i)
            cnt += pool[pool_index][i];
        return cnt * HORIZONTAL_SCORE;
    }

    public static void main(String[] args)
    {
        var fasta = new Utilities.Fasta("D:\\poolalign\\new\\tmp.txt.begin");

        byte[][] src = Pseudo.string_to_pseudo(fasta.get_sequences());
//        byte[][] src = { Pseudo.string_to_pseudo("agctagct"),
//                         Pseudo.string_to_pseudo("agcta"),
//                         Pseudo.string_to_pseudo("gctagc"),
//                         Pseudo.string_to_pseudo("atttt") };
        var result = new PoolAligner().do_align(src);

        var output = new Utilities.Fasta(Pseudo.pseudo_to_string(result), null);
        output.output("D:\\poolalign\\new\\beginning-reversed-poolalign.txt", false);
    }

    private void print_alignments()
    {
        for (int i = 0; i != alignments.length; ++i)
            if (alignments[i] != null) System.out.println(Pseudo.pseudo_to_string(alignments[i]));
            else break;
        System.out.println();
    }

    private void output_alignments()
    {
        int len = 0;
        for (int i = 0; i != alignments.length; ++i)
            if (alignments[i] != null) ++len;

        String[] output = new String[len];
        for (int i = 0; i != alignments.length; ++i)
            if (alignments[i] != null) output[i] = Pseudo.pseudo_to_string(alignments[i]);

        new Utilities.Fasta(output, null).output("D:\\poolalign\\new\\bgn\\" + len + ".txt", false);
    }

    static private void assert_equal(byte[][] src, byte[][] des)
    {
        assert src.length == des.length;

        for (int i = 0; i != src.length; ++i)
            assert_equal(Pseudo.remove_spaces(src[i]), Pseudo.remove_spaces(des[i]));
    }

    static private void assert_equal(byte[] lhs, byte[] rhs)
    {
        assert lhs.length == rhs.length;
        for (int i = 0; i != lhs.length; ++i) assert lhs[i] == rhs[i];
    }

}
