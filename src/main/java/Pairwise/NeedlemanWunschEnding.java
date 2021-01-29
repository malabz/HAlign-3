package Pairwise;

import Utilities.Pair;
import Utilities.Pseudo;
import Utilities.UtilityFunctions;

import static Main.GlobalVariables.UNKNOWN;

public class NeedlemanWunschEnding extends PairwiseAligner
{

    public static final int LEFT_ENDING = 1;
    public static final int RIGHT_ENDING = 2;

    private static final int MATCH = 7, MISMATCH = -3, MID_OPEN = -31, MID_EXTENSION = -2, END_OPEN = 0, END_EXTENSION = -1, UNKNOWN_MATCH = 5;
    private static final int X = 0, Y = 1, Z = 2;

    private int[][][] mtrx;
    private int[][][] path;

    private int left_open, right_open;
    private int left_extension, right_extension;

    private static int cnt = 0;

    public static Pair<int[], int[]> align(byte[] lhs, byte[] rhs, int flags)
    {
        return new NeedlemanWunschEnding().do_align(lhs, rhs, flags);
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

//        if ((flags & RIGHT_ENDING) != 0 && rhs.length != 0)
//        {
//            ++cnt;
//            System.out.println(cnt);
//            System.out.println(Pseudo.pseudo_to_string(Pseudo.insert_spaces(lhs, lhs_spaces)));
//            System.out.println(Pseudo.pseudo_to_string(Pseudo.insert_spaces(rhs, rhs_spaces)));
//            System.out.println();
//        }

        return new Pair<>(lhs_spaces, rhs_spaces);
    }

    private void initialise(byte[] lhs, byte[] rhs, int flags)
    {
        base_init(lhs, rhs);

        left_open = (flags & LEFT_ENDING) == 0 ? MID_OPEN : END_OPEN;
        left_extension = (flags & LEFT_ENDING) == 0 ? MID_EXTENSION : END_EXTENSION;
        right_open = (flags & RIGHT_ENDING) == 0 ? MID_OPEN : END_OPEN;
        right_extension = (flags & RIGHT_ENDING) == 0 ? MID_EXTENSION : END_EXTENSION;

//        if ((flags & LEFT_ENDING) == 0) System.out.println("nl");
//        if ((flags & RIGHT_ENDING) == 0) System.out.println("nr");
//        System.out.println("left_open = " + left_open);
//        System.out.println("right_open = " + right_open);
//        System.out.println("left_extension = " + left_extension);
//        System.out.println("right_extension = " + right_extension);

        mtrx = new int[3][lhs.length + 1][rhs.length + 1];
        for (int i = 0; i <= lhs.length; ++i)
        {
//            mtrx[X][i][0] = left_open + i * EXTENSION;
            mtrx[X][i][0] = left_open + i * left_extension;
            mtrx[Y][i][0] = mtrx[Z][i][0] = MINUS_INFINITY;
        }
        for (int j = 0; j <= rhs.length; ++j)
        {
            mtrx[X][0][j] = mtrx[Z][0][j] = MINUS_INFINITY;
            mtrx[Y][0][j] = left_open + j * left_extension;
        }
        mtrx[X][0][0] = mtrx[Y][0][0] = left_open;
        mtrx[Z][0][0] = 0;

        path = new int[3][lhs.length + 1][rhs.length + 1];
        for (int i = 1; i <= lhs.length; ++i) path[X][i][0] = X;
        for (int j = 1; j <= rhs.length; ++j) path[Y][0][j] = Y;
    }

    private void dp()
    {
        for (int i = 1; i <= lhs.length; ++i)
            for (int j = 1; j <= rhs.length; ++j)
            {
                var arr = new int[3];
                int index_of_max;

                arr[X] = mtrx[X][i - 1][j];
                arr[Y] = mtrx[Y][i - 1][j] + (j == rhs.length ? right_open : MID_OPEN);
                arr[Z] = mtrx[Z][i - 1][j] + (j == rhs.length ? right_open : MID_OPEN);
                index_of_max = UtilityFunctions.index_of_max(arr);
                mtrx[X][i][j] = arr[index_of_max] + (j == rhs.length ? right_extension : MID_EXTENSION);
                path[X][i][j] = index_of_max;

                arr[X] = mtrx[X][i][j - 1] + (i == lhs.length ? right_open : MID_OPEN);
                arr[Y] = mtrx[Y][i][j - 1];
                arr[Z] = mtrx[Z][i][j - 1] + (i == lhs.length ? right_open : MID_OPEN);
                index_of_max = UtilityFunctions.index_of_max(arr);
                mtrx[Y][i][j] = arr[index_of_max] + (i == lhs.length ? right_extension : MID_EXTENSION);
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
        if (l == UNKNOWN || r == UNKNOWN) return UNKNOWN_MATCH;
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
//        byte[] lhs = Pseudo.string_to_pseudo("TGCTAGGTAGAGCTGCCTATATGGAAGAGCCCTAATGTGTAAAATTAATTTTAGTAGTGCTATCCCCATGTGATTTTAAAAGCTTTTTAGGAAAATGACCAAAAAAAAAAAAAAAA");
//        byte[] rhs = Pseudo.string_to_pseudo("TGCTAGGTAGAGCTGCCTATATGGAAGAGCCCTAATGTGTAAAATTAATTTTAGTAGTGCTATCCCCATGTGATTTTAATAGCTTCTTAGGAGAATGACAAAAAAAAAAAAAAAAAAAAA");
        byte[] lhs = Pseudo.string_to_pseudo("TTAATTTTAGTAGTGCTATCCCCATGTGATTTTAATAGCTTCTTAGGAGAATCTGCCACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        byte[] rhs = Pseudo.string_to_pseudo("TTAATTTTAGTAGTGCTATCCCCATGTGATTTTAATAGCTTCTTAGGAGAATGACAAAAAAAAAAAAAAAAAAAAA");
        var result = NeedlemanWunschEnding.align(lhs, rhs, RIGHT_ENDING);
        System.out.println(Pseudo.pseudo_to_string(Pseudo.insert_spaces(lhs, result.get_first())));
        System.out.println(Pseudo.pseudo_to_string(Pseudo.insert_spaces(rhs, result.get_second())));
    }

}
