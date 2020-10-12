package Pairwise;

import Utilities.Pair;
import Utilities.Pseudo;
import Utilities.UtilityFunctions;

import static Main.GlobalVariables.UNKNOWN;

public class NeedlemanWunschKBandEnding extends PairwiseAligner
{
    public static final int LEFT_ENDING = 1;
    public static final int RIGHT_ENDING = 1 << 1;

    private static final int MATCH = 7, MISMATCH = -3, MID_OPEN = -41, EXTENSION = -2, END_OPEN = 0;
    private static final int MIN_K = 3, LENGTH_DIVISOR = 4;
    private static final int X = 0, Y = 1, Z = 2;

    private int[][][] mtrx;
    private int[][][] path;
    private int lhs_offset, rhs_offset;

    private int left_open, right_open;

    public static Pair<int[], int[]> align(byte[] lhs, byte[] rhs, int flags)
    {
        return new NeedlemanWunschKBandEnding().do_align(lhs, rhs, flags);
    }

    private Pair<int[], int[]> do_align(byte[] lhs, byte[] rhs, int flags)
    {
        initialise(lhs, rhs, flags);

//        print_dp_matrix(lhs, rhs, mtrx[X]);
//        print_dp_matrix(lhs, rhs, mtrx[Y]);
//        print_dp_matrix(lhs, rhs, mtrx[Z]);
//        System.out.println();

        dp();

//        print_dp_matrix(lhs, rhs, mtrx[X]);
//        print_dp_matrix(lhs, rhs, mtrx[Y]);
//        print_dp_matrix(lhs, rhs, mtrx[Z]);
//        System.out.println();

//        print_dp_matrix(lhs, rhs, path[X]);
//        print_dp_matrix(lhs, rhs, path[Y]);
//        print_dp_matrix(lhs, rhs, path[Z]);
//        System.out.println();

        trace_back();
//        log();

        return new Pair<>(lhs_spaces, rhs_spaces);
    }

    private void initialise(byte[] lhs, byte[] rhs, int flags)
    {
        base_init(lhs, rhs);

        left_open = (flags & LEFT_ENDING) == 0 ? MID_OPEN : -2;
        right_open = (flags & RIGHT_ENDING) == 0 ? MID_OPEN : END_OPEN;

        mtrx = new int[3][lhs.length + 1][rhs.length + 1];
        for (int i = 0; i <= lhs.length; ++i)
        {
            mtrx[X][i][0] = left_open + i * EXTENSION;
            mtrx[Y][i][0] = mtrx[Z][i][0] = MINUS_INFINITY;
        }
        for (int j = 0; j <= rhs.length; ++j)
        {
            mtrx[X][0][j] = mtrx[Z][0][j] = MINUS_INFINITY;
            mtrx[Y][0][j] = left_open + j * EXTENSION;
        }
        mtrx[Z][0][0] = 0;

        path = new int[3][lhs.length + 1][rhs.length + 1];
        for (int i = 1; i <= lhs.length; ++i) path[X][i][0] = X;
        for (int j = 1; j <= rhs.length; ++j) path[Y][0][j] = Y;

        int k = Math.max(MIN_K, Math.min(lhs.length, rhs.length) / LENGTH_DIVISOR);
        lhs_offset = (lhs.length > rhs.length ? lhs.length - rhs.length : 0) + k;
        rhs_offset = (rhs.length > lhs.length ? rhs.length - lhs.length : 0) + k;
        for (int i = 0; i <= lhs.length; ++i)
            for (int j = 0; j <= rhs.length; ++j)
                if (i - j > lhs_offset || j - i > rhs_offset)
                {
                    mtrx[X][i][j] = MINUS_INFINITY;
                    mtrx[Y][i][j] = MINUS_INFINITY;
                    mtrx[Z][i][j] = MINUS_INFINITY;
                }
    }

    private void dp()
    {
//        var lhs_gap_open = new int[lhs.length + 1];
//        var rhs_gap_open = new int[rhs.length + 1];
//        for (int i = 1; i != lhs.length; ++i) lhs_gap_open[i] = MID_OPEN;
//        for (int j = 1; j != rhs.length; ++j) rhs_gap_open[j] = MID_OPEN;
//        lhs_gap_open[0] = lhs_gap_open[lhs.length] = rhs_gap_open[0] = rhs_gap_open[rhs.length] = END_OPEN;

        for (int i = 1; i <= lhs.length; ++i)
            for (int j = 1; j <= rhs.length; ++j)
                if (i - j <= lhs_offset || j - i >= rhs_offset)
                {
//                    int open = i == lhs.length || j == rhs.length ? END_OPEN : MID_OPEN;
                    var arr = new int[3];
                    int index_of_max;

                    arr[X] = mtrx[X][i - 1][j];
                    arr[Y] = mtrx[Y][i - 1][j] + (j == rhs.length ? right_open : MID_OPEN);
                    arr[Z] = mtrx[Z][i - 1][j] + (j == rhs.length ? right_open : MID_OPEN);
                    index_of_max = UtilityFunctions.index_of_max(arr);
                    mtrx[X][i][j] = arr[index_of_max] + EXTENSION;
                    path[X][i][j] = index_of_max;

                    arr[X] = mtrx[X][i][j - 1] + (i == lhs.length ? right_open : MID_OPEN);
                    arr[Y] = mtrx[Y][i][j - 1];
                    arr[Z] = mtrx[Z][i][j - 1] + (i == lhs.length ? right_open : MID_OPEN);
                    index_of_max = UtilityFunctions.index_of_max(arr);
                    mtrx[Y][i][j] = arr[index_of_max] + EXTENSION;
                    path[Y][i][j] = index_of_max;

                    arr[X] = mtrx[X][i - 1][j - 1];
                    arr[Y] = mtrx[Y][i - 1][j - 1];
                    arr[Z] = mtrx[Z][i - 1][j - 1];
                    index_of_max = UtilityFunctions.index_of_max(arr);
                    mtrx[Z][i][j] = arr[index_of_max] + score(lhs[i - 1], rhs[j - 1]);
                    path[Z][i][j] = index_of_max;
                }
    }

    private int score(byte l, byte r)
    {
        if (l == UNKNOWN || r == UNKNOWN) return MATCH;
        else return l == r ? MATCH : MISMATCH;
    }

    private void trace_back()
    {
        int lhs_index = lhs.length, rhs_index = rhs.length;
        var arr = new int[3];
        arr[X] = mtrx[X][lhs_index][rhs_index];
        arr[Y] = mtrx[Y][lhs_index][rhs_index];
        arr[Z] = mtrx[Z][lhs_index][rhs_index];
        int curr_path = UtilityFunctions.index_of_max(arr);
        while (lhs_index > 0 || rhs_index > 0)
        {
            switch (curr_path)
            {
            case X:
                curr_path = path[curr_path][lhs_index--][rhs_index];
                ++rhs_spaces[rhs_index];
                break;
            case Y:
                curr_path = path[curr_path][lhs_index][rhs_index--];
                ++lhs_spaces[lhs_index];
                break;
            case Z:
                curr_path = path[curr_path][lhs_index--][rhs_index--];
                break;
            }
        }
    }

    public static void main(String[] args)
    {
//        byte[] lhs = Pseudo.string_to_pseudo("agcttcttaggagaatgacaataaggtagcgaaattccttgtcaactaattattgacctgcacgaaaggcgcatgcctaacatgcttagaattatggcctcacttgt");
//        byte[] rhs = Pseudo.string_to_pseudo("nnnnnnttaggaaaaaaanaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        byte[] lhs = Pseudo.string_to_pseudo("aaaaaaaa");
        byte[] rhs = Pseudo.string_to_pseudo("aaaa");
        var result = NeedlemanWunschKBandEnding.align(lhs, rhs, 3);
        System.out.println(Pseudo.pseudo_to_string(Pseudo.insert_spaces(lhs, result.get_first())));
        System.out.println(Pseudo.pseudo_to_string(Pseudo.insert_spaces(rhs, result.get_second())));
    }

}
