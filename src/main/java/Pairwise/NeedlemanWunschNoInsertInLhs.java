package Pairwise;

import static Main.GlobalVariables.*;

public class NeedlemanWunschNoInsertInLhs
{

    private static final int MATCH = 7, MISMATCH = -3, MID_OPEN = -9, MID_EXTENSION = -2, END_OPEN = 0, END_EXTENSION = -1;
    private static final int MINUS_INFINITY = Integer.MIN_VALUE / 2;
    private static final int Z = 0, X = 1;

    private final int[][] pool;

    private int lhs_bgn, lhs_len;
    private int rhs_bgn, rhs_len;
    private byte[] rhs;

    private int[][][] mtrx;
    private int[][][] path;

    private int[] rhs_spaces;

    public NeedlemanWunschNoInsertInLhs(int[][] statistics)
    {
        this.pool = statistics;
    }

    public int[] align(int lhs_bgn, int lhs_end, byte[] rhs, int rhs_bgn, int rhs_end)
    {
        initialise(lhs_bgn, lhs_end, rhs, rhs_bgn, rhs_end);

//        print_dp(lhs, rhs, matrix[X]);
//        print_dp(lhs, rhs, matrix[Z]);
//        System.out.println();

        dp();

//        print_dp_matrix(lhs, rhs, mtrx[X]);
//        print_dp_matrix(lhs, rhs, mtrx[Z]);
//        System.out.println();

//        print_dp_matrix(lhs, rhs, path[X]);
//        print_dp_matrix(lhs, rhs, path[Z]);
//        System.out.println();

        trace_back();
//        log();

        return rhs_spaces;
    }

    private void initialise(int lhs_bgn, int lhs_end, byte[] rhs, int rhs_bgn, int rhs_end)
    {
        final int open = rhs_bgn == 0 ? END_OPEN : MID_OPEN;
        final int extension = rhs_bgn == 0 ? END_EXTENSION : MID_EXTENSION;

        this.lhs_bgn = lhs_bgn;
        this.lhs_len = lhs_end - lhs_bgn;
        this.rhs = rhs;
        this.rhs_bgn = rhs_bgn;
        this.rhs_len = rhs_end - rhs_bgn;
        this.rhs_spaces = new int[rhs_len + 1];

        mtrx = new int[2][lhs_len + 1][rhs_len + 1];
        for (int i = 0; i <= lhs_len; ++i)
        {
            mtrx[X][i][0] = open + i * extension;
            mtrx[Z][i][0] = MINUS_INFINITY;
        }
        for (int j = 0; j <= rhs_len; ++j)
            mtrx[X][0][j] = mtrx[Z][0][j] = MINUS_INFINITY;
        mtrx[X][0][0] = open;
        mtrx[Z][0][0] = 0;

        path = new int[2][lhs_len + 1][rhs_len + 1];
        for (int i = 1; i <= lhs_len; ++i) path[X][i][0] = X;
    }

    private void dp()
    {
        for (int i = 1; i <= lhs_len; ++i)
            for (int j = 1; j <= rhs_len; ++j)
            {
                int fx, fz;

                fx = mtrx[X][i - 1][j];
                fz = mtrx[Z][i - 1][j] + (rhs_bgn + j == rhs.length ? END_OPEN : MID_OPEN);
                mtrx[X][i][j] = Math.max(fx, fz) + (rhs_bgn + j == rhs.length ? END_EXTENSION : MID_EXTENSION);
                path[X][i][j] = fx > fz ? X : Z;

                fx = mtrx[X][i - 1][j - 1];
                fz = mtrx[Z][i - 1][j - 1];
                mtrx[Z][i][j] = Math.max(fx, fz) + score_of(lhs_bgn + i - 1, rhs_bgn + j - 1);
                path[Z][i][j] = fx > fz ? X : Z;
            }
    }

    private int score_of(int lhs_idx, int rhs_idx)
    {
        if (rhs[rhs_idx] == UNKNOWN) return MATCH;

        int mismatch = 0, match = pool[lhs_idx][UNKNOWN];
//        int most = GAP;
        for (int i = A; i != UNKNOWN; ++i)
        {
            if (i != rhs[rhs_idx]) mismatch += pool[lhs_idx][i];
            else match += pool[lhs_idx][i];
//            if (pool[lhs_idx][i] > pool[lhs_idx][most]) most = i;
        }
//        if (pool[lhs_idx][UNKNOWN] > pool[lhs_idx][most]) most = UNKNOWN;

        int result = (mismatch * MISMATCH + match * MATCH + pool[lhs_idx][GAP] * MID_EXTENSION) / (mismatch + match + pool[lhs_idx][GAP]);
//        if (most == GAP) result += MID_EXTENSION;
        return result;
    }

    private void trace_back()
    {
        int lhs_idx = lhs_len, rhs_idx = rhs_len;
        int fx = mtrx[X][lhs_idx][rhs_idx];
        int fz = mtrx[Z][lhs_idx][rhs_idx];
        int curr_path = fx > fz ? X : Z;
        while (lhs_idx > 0 || rhs_idx > 0)
            switch (curr_path)
            {
                case X:
                    curr_path = path[curr_path][lhs_idx--][rhs_idx];
                    ++rhs_spaces[rhs_idx];
                    break;
                case Z:
                    curr_path = path[curr_path][lhs_idx--][rhs_idx--];
                    break;
            }
    }

}
