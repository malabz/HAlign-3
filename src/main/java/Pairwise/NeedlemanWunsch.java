package Pairwise;

import static Main.GlobalVariables.UNKNOWN;
import Utilities.Pair;
import Utilities.Pseudo;
import Utilities.UtilityFunctions;

public class NeedlemanWunsch extends PairwiseAligner
{

    private static final int MATCH = 7, MISMATCH = -3, MID_OPEN = -11, EXTENSION = -2, END_OPEN = -3;
    private static final int MINUS_INFINITY = Integer.MIN_VALUE / 2;
    private static final int X = 0, Y = 1, Z = 2;

    private int[][][] mtrx;
    private int[][][] path;

    public static Pair<int[], int[]> align(byte[] lhs, byte[] rhs)
    {
        return new NeedlemanWunsch().do_align(lhs, rhs);
    }

    private Pair<int[], int[]> do_align(byte[] lhs, byte[] rhs)
    {
        initialise(lhs, rhs);

//        print_dp(lhs, rhs, matrix[X]);
//        print_dp(lhs, rhs, matrix[Y]);
//        print_dp(lhs, rhs, matrix[Z]);
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

    private void initialise(byte[] lhs, byte[] rhs)
    {
        base_init(lhs, rhs);

        mtrx = new int[3][lhs.length + 1][rhs.length + 1];
        for (int i = 0; i <= lhs.length; ++i)
        {
            mtrx[X][i][0] = END_OPEN + i * EXTENSION;
            mtrx[Y][i][0] = mtrx[Z][i][0] = MINUS_INFINITY;
        }
        for (int j = 0; j <= rhs.length; ++j)
        {
            mtrx[X][0][j] = mtrx[Z][0][j] = MINUS_INFINITY;
            mtrx[Y][0][j] = END_OPEN + j * EXTENSION;
        }
        mtrx[Z][0][0] = 0;

        path = new int[3][lhs.length + 1][rhs.length + 1];
        for (int i = 1; i <= lhs.length; ++i) path[X][i][0] = X;
        for (int j = 1; j <= rhs.length; ++j) path[Y][0][j] = Y;
    }

    private void dp()
    {
//        var lhs_gap_open = new int[lhs.length + 1];
//        var rhs_gap_open = new int[rhs.length + 1];
//        for (int i = 1; i != lhs.length; ++i) lhs_gap_open[i] = MID_OPEN;
//        for (int j = 1; j != rhs.length; ++j) rhs_gap_open[j] = MID_OPEN;
//        lhs_gap_open[0] = lhs_gap_open[lhs.length] = rhs_gap_open[0] = rhs_gap_open[rhs.length] = END_OPEN;

        for (int i = 1; i <= lhs.length; ++i)
        {
            for (int j = 1; j <= rhs.length; ++j)
            {
                int open = i == lhs.length || j == rhs.length ? END_OPEN : MID_OPEN;
                var arr = new int[3];
                int index_of_max;

                arr[X] = mtrx[X][i - 1][j];
                arr[Y] = mtrx[Y][i - 1][j] + open;
                arr[Z] = mtrx[Z][i - 1][j] + open;
                index_of_max = UtilityFunctions.index_of_max(arr);
                mtrx[X][i][j] = arr[index_of_max] + EXTENSION;
                path[X][i][j] = index_of_max;

                arr[X] = mtrx[X][i][j - 1] + open;
                arr[Y] = mtrx[Y][i][j - 1];
                arr[Z] = mtrx[Z][i][j - 1] + open;
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
    }

    private int score(byte l, byte r)
    {
        if (l == UNKNOWN || r == UNKNOWN) return 0;
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
        byte[] lhs = Pseudo.string2pseudo("agcttcttaggagaatgacaataaggtagcgaaattccttgtcaactaattattgacctgcacgaaaggcgcatgcctaacatgcttagaattatggcctcacttgt");
        byte[] rhs = Pseudo.string2pseudo("nnnnnnttaggaaaaaaanaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//        byte[] lhs = Pseudo.string2pseudo("agct");
//        byte[] rhs = Pseudo.string2pseudo("agct");
        var result = NeedlemanWunsch.align(lhs, rhs);
        System.out.println(Pseudo.pseudo2string(Pseudo.insert_spaces(lhs, result.get_first())));
        System.out.println(Pseudo.pseudo2string(Pseudo.insert_spaces(rhs, result.get_second())));
    }

}
