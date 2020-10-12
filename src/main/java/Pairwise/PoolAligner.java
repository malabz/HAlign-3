package Pairwise;

import Utilities.Pair;
import Utilities.Pseudo;

import java.util.Arrays;

import static Main.GlobalVariables.*;

@SuppressWarnings("unused")
public class PoolAligner extends PairwiseAligner
{
    private static final int MATCH_SCORE = 2, MISMATCH_SCORE = 0, HORIZONTAL_SCORE = -7, VERTICAL_SCORE = -31;
    private static final byte DIAGONAL = 0, HORIZONTAL = 1, VERTICAL = 2;

    private byte[][] sequences;
    private byte[][] alignments;
    private byte[] longest;
    private int[][] pool; // always being the centre, incrementally alignment
    private int curr_depth;

    private PoolAligner() {}

    public static byte[][] align(byte[][] src)
    {
        return new PoolAligner().do_align(src);
    }

    private byte[][] do_align(byte[][] src)
    {
        sequences = src;
        initialise();

        for (int i = 0; i != src.length; ++i)
        {
            if (src[i] != longest) align_next(src[i]);
//            if ((i & 0xf) == 0xf) System.out.print('.');
            if ((i & 0x7f) == 0x7f) System.out.println(" " + i);
        }
        System.out.println();

        return alignments;
    }

    private void initialise()
    {
        longest = sequences[0];
        for (int i = 1; i != sequences.length; ++i) if (sequences[i].length > longest.length) longest = sequences[i];

        curr_depth = 1;
        pool = new int[longest.length][CHAR_KIND];
        for (int i = 0; i != longest.length; ++i) pool[i][longest[i]] = 1;

        alignments = new byte[sequences.length][];
        alignments[0] = longest;
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

        System.out.print(pool_insert == 0 ? ' ' : '-');
        if (pool_insert != 0) output_alignments();
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
            case VERTICAL:
                ++pool_spaces[pool_index];
                --csqc_index;
                break;
            case HORIZONTAL:
                ++csqc_spaces[csqc_index];
                --pool_index;
                break;
            case DIAGONAL:
                --pool_index;
                --csqc_index;
                break;
            default:
                break;
            }

        return new Pair<>(csqc_spaces, pool_spaces);
    }

    private int diagonal_step(int pool_index, byte curr_char)
    {
        int score = pool[pool_index][GAP] * HORIZONTAL_SCORE;
        int match = 0, mismatch = 0;
        for (int i = A; i != UNKNOWN; ++i)
            if (curr_char == UNKNOWN || curr_char == i) match += pool[pool_index][i];
            else mismatch += pool[pool_index][i];
        match += pool[pool_index][UNKNOWN];
        score += match * MATCH_SCORE + mismatch * MISMATCH_SCORE;
        return score;
    }

    private int horizontal_step(int pool_index)
    {
        int not_gap = 0;

        for (int i = A; i != CHAR_KIND; ++i)
            not_gap += pool[pool_index][i];
        return not_gap * HORIZONTAL_SCORE;
    }

    public static void main(String[] args)
    {
        var fasta = new Utilities.Fasta("D:\\new-folder\\beginning-origin-reversed.fasta");

        byte[][] src = Pseudo.string_to_pseudo(fasta.get_sequences());
//        byte[][] src = { Pseudo.string_to_pseudo("agctagct"),
//                         Pseudo.string_to_pseudo("agcta"),
//                         Pseudo.string_to_pseudo("gctagc"),
//                         Pseudo.string_to_pseudo("atttt") };
        var result = PoolAligner.align(src);

        var output = new Utilities.Fasta(Pseudo.pseudo_to_string(result), null);
        output.output("D:\\new-folder\\beginning-reversed-poolalign.txt", false);
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

        new Utilities.Fasta(output, null).output(len + ".txt", false);
    }

}
