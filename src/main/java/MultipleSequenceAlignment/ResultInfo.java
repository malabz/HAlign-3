package MultipleSequenceAlignment;

import static Main.GlobalVariables.GAP;

/**
 * 全部为静态函数, 返回比对结果的某些属性
 */
@SuppressWarnings("unused")
public class ResultInfo
{

    private static final int DEFAULT_MATCH_SCORE = 1, DEFAULT_MISMATCH_SCORE = -1, DEFAULT_GAP_SCORE = -2;

    /**
     * 计算比对结果sp得分, 使用默认计分模式
     * @param pseudo_result 比对完成的伪序列, 必须有相同的长度
     * @return sp得分
     */
    public static long calculate_sp(byte[][] pseudo_result)
    {
        return calculate_sp(pseudo_result, DEFAULT_MATCH_SCORE, DEFAULT_MISMATCH_SCORE, DEFAULT_GAP_SCORE);
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
        int row = pseudo_result.length;
        long ret = 0;
        for (int i = 0; i != row - 1; ++i)
            for (int j = i + 1; j != row; ++j)
                ret += pairwise_sp(pseudo_result[i], pseudo_result[j], match_score, mismatch_score, gap_score);
        return ret;
    }

    public static long pairwise_sp(byte[] lhs, byte[] rhs)
    {
        return pairwise_sp(lhs, rhs, DEFAULT_MATCH_SCORE, DEFAULT_MISMATCH_SCORE, DEFAULT_GAP_SCORE);
    }

    public static long pairwise_sp(byte[] lhs, byte[] rhs, int match_score, int mismatch_score, int gap_score)
    {
        long ret = 0;
        for (int i = 0; i != lhs.length; ++i)
            ret += lhs[i] == GAP ? rhs[i] == GAP ? 0 : gap_score : rhs[i] == GAP ? gap_score : lhs[i] == rhs[i] ? match_score : mismatch_score;
        return ret;
    }

}