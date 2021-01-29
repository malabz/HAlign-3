package Pairwise;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import Utilities.Pair;
import Utilities.Pseudo;

/**
 * 用于存放动态规划方法的基本成员和通用方法
 */
public abstract class PairwiseAligner
{

    protected static final int MINUS_INFINITY = Integer.MIN_VALUE / 2;

    protected byte[] lhs, rhs; // 两个序列
    protected int[] lhs_spaces, rhs_spaces; // 插入空格数量

    protected void base_init(byte[] lhs, byte[] rhs)
    {
        this.lhs = lhs;
        this.rhs = rhs;
        lhs_spaces = new int[lhs.length + 1];
        rhs_spaces = new int[rhs.length + 1];
    }

    /**
     * 打印二维动态规划数组
     */
    @SuppressWarnings("unused")
    protected static void print_dp_matrix(byte[] lhs, byte[] rhs, int[][] matrix)
    {
        for (int i = 0; i <= lhs.length; ++i)
        {
            for (int j = 0; j <= rhs.length; ++j) System.out.printf("%13d ", matrix[i][j]);
            System.out.println();
        }
        System.out.println();
    }

    /**
     * 打印二维动态规划数组
     */
    @SuppressWarnings("unused")
    protected static void print_dp_matrix(byte[] lhs, byte[] rhs, long[][] matrix)
    {
        for (int i = 0; i <= lhs.length; ++i)
        {
            for (int j = 0; j <= rhs.length; ++j) System.out.printf("%13d ", matrix[i][j]);
            System.out.println();
        }
        System.out.println();
    }

    /**
     * 打印二维动态规划数组
     */
    @SuppressWarnings("unused")
    protected static void print_dp_matrix(byte[] lhs, byte[] rhs, byte[][] matrix)
    {
        for (int i = 0; i <= lhs.length; ++i)
        {
            for (int j = 0; j <= rhs.length; ++j) System.out.printf("%13d ", matrix[i][j]);
            System.out.println();
        }
        System.out.println();
    }

    /**
     * 进行局部比对, 为MSA服务
     *
     * @param lhs        待比对全局序列
     * @param rhs        待比对全局序列
     * @param lhs_spaces 比对完成后将局部比对结果融入此全局比对结果
     * @param rhs_spaces 比对完成后将局部比对结果融入此全局比对结果
     * @param lhs_begin  待比对局部序列起始位置
     * @param rhs_begin  待比对局部序列起始位置
     * @param lhs_end    待比对局部序列结束位置的后一个位置
     * @param rhs_end    待比对局部序列结束位置的后一个位置
     */
    public static void local_align(byte[] lhs, int[] lhs_spaces, int lhs_begin, int lhs_end,
                                   byte[] rhs, int[] rhs_spaces, int rhs_begin, int rhs_end)
    {
        int end_flags = 0;
        if (lhs_begin == 0) end_flags |= NeedlemanWunschEnding.LEFT_ENDING;
        if (lhs_end == lhs.length) end_flags |= NeedlemanWunschEnding.RIGHT_ENDING;

        Pair<int[], int[]> result = null;
        try
        {
            result = end_flags == 0 ?
                    NeedlemanWunschKBand.align(
                            Arrays.copyOfRange(lhs, lhs_begin, lhs_end),
                            Arrays.copyOfRange(rhs, rhs_begin, rhs_end)) :
                    NeedlemanWunschEnding.align(
                            Arrays.copyOfRange(lhs, lhs_begin, lhs_end),
                            Arrays.copyOfRange(rhs, rhs_begin, rhs_end),
                            end_flags);
        }
        catch (OutOfMemoryError oom)
        {
            System.err.println("fatal error: out of memory");
            System.exit(1);
        }

        assert check_align(lhs_end - lhs_begin, result.get_first(), rhs_end - rhs_begin, result.get_second());

        merge(result.get_first(), lhs_spaces, lhs_begin);
        merge(result.get_second(), rhs_spaces, rhs_begin);
    }

    static private boolean check_align(int lhs_len, int[] lhs_spaces, int rhs_len, int[] rhs_spaces)
    {
        if (lhs_spaces == null || rhs_spaces == null) return false;
        if (lhs_spaces.length != lhs_len + 1 || rhs_spaces.length != rhs_len + 1) return false;

        for (int i = 0; i != lhs_spaces.length; ++i) lhs_len += lhs_spaces[i];
        for (int i = 0; i != rhs_spaces.length; ++i) rhs_len += rhs_spaces[i];
        return lhs_len == rhs_len;
    }

    // 把局部比对结果融入全局比对结果
    private static void merge(int[] src, int[] target, int begin_pos)
    {
        for (int i = 0; i != src.length; ++i) target[begin_pos + i] += src[i];
    }

    @SuppressWarnings("unused")
    protected final void log()
    {
        boolean lhs_space_added = false, rhs_space_added = false;
        for (int i = 0; i != lhs_spaces.length; ++i)
        {
            if (lhs_spaces[i] != 0)
            {
                lhs_space_added = true;
                break;
            }
        }
        for (int i = 0; i != rhs_spaces.length; ++i)
        {
            if (rhs_spaces[i] != 0)
            {
                rhs_space_added = true;
                break;
            }
        }
        if (lhs_space_added && rhs_space_added)
        {
            try (var bw = new BufferedWriter(new FileWriter("debug\\pairwise.txt", true)))
            {
                bw.write("aligned by " + this.getClass().getName() + "\n");
                bw.write(Pseudo.pseudo_to_string(Pseudo.insert_spaces(lhs, lhs_spaces)) + "\n");
                bw.write(Pseudo.pseudo_to_string(Pseudo.insert_spaces(rhs, rhs_spaces)) + "\n\n");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

}
