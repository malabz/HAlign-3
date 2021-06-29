package MultipleSequenceAlignment;

import static Main.GlobalVariables.*;

/**
 * 全部为静态函数, 返回比对结果的某些属性
 */
// TODO: flaws
public class ResultInfo
{

    private static final int MATCH_SCORE = 1, MISMATCH_SCORE = -1, GAP_SCORE = -2;

    /**
     * 计算比对结果sp得分, 使用默认计分模式
     * @param pseudo_result 比对完成的伪序列, 必须有相同的长度
     * @return sp得分
     */
    public static long calculate_sp(byte[][] pseudo_result)
    {
        return calculate_sp(pseudo_result, MATCH_SCORE, MISMATCH_SCORE, GAP_SCORE);
    }

    /**
     * 计算比对结果sp得分, 使用自定义计分模式
     * @param pseudo_result 比对完成的伪序列, 必须有相同的长度
     * @param match_score 字符匹配时得分
     * @param mismatch_score 字符未匹配时得分
     * @param gap_score 一个字符和一个空格时得分
     * @return sp得分
     */
    public static long calculate_sp(byte[][] pseudo_result, int match_score, int mismatch_score, int gap_score)
    {
        long score = 0;
        for (int j = 0; j != pseudo_result[0].length; ++j)
        {
            int[] statistics = new int[CHAR_KIND];
            for (int i = 0; i != pseudo_result.length; ++i)
                ++statistics[pseudo_result[i][j]];
            score += score_of(statistics[A], statistics[G], statistics[C], statistics[T], statistics[UNKNOWN], statistics[GAP]);
        }
        return score;
    }

    private static long score_of(int a, int g, int c, int t, int n, int gap)
    {
        return ((a + c) * (g + t) + a * c + g * t) * MISMATCH_SCORE +
                (a * (a - 1) + c * (c - 1) + g * (g - 1) + t * (t - 1)) / 2 * MATCH_SCORE +
                (a + c + g + t + n) * gap * GAP_SCORE;
    }

    public static long pairwise_sp(byte[] lhs, byte[] rhs)
    {
        return pairwise_sp(lhs, rhs, MATCH_SCORE, MISMATCH_SCORE, GAP_SCORE);
    }

    public static long pairwise_sp(byte[] lhs, byte[] rhs, int match_score, int mismatch_score, int gap_score)
    {
        long ret = 0;
        for (int i = 0; i != lhs.length; ++i)
            ret += lhs[i] == GAP ? rhs[i] == GAP ? 0 : gap_score : rhs[i] == GAP ? gap_score : lhs[i] == rhs[i] ? match_score : mismatch_score;
        return ret;
    }

}