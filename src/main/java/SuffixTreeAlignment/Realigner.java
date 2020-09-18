package SuffixTreeAlignment;

import Utilities.Pseudo;
import Utilities.UtilityFunctions;

import static Main.GlobalVariables.*;

import java.util.*;

class Realigner
{

    final static double TOLERANCE = .9; // 当当前串和串池等长或当前串长度小于串池但尝试简单插入时, 能够容忍多大的差异度

    byte[][] pending_alignment; // 待比对序列
    int row; // 待比对行数
    int finished; // 已经比对完成的行数, 在update_pool中更新
    int[][] pool; // pool[i][j]表示索引为i的位置上字符j出现的频数, 第二维长度永远为CHAR_KIND
    double[][] relative_pool; // (double)pool / finished
    TreeMap<byte[], byte[]> result_map; // key为待比对序列, value为key对应的比对结果

    // 输入为已经去空格的待比对序列
    Realigner(byte[][] input)
    {
        pending_alignment = input;
        row = input.length;
    }

    // 比对后输出结果
    byte[][] align()
    {
        Comparator<byte[]> byte_array_comparator = (byte[] lhs, byte[] rhs) ->
        { // 长度大的排在前, 长度相同时按反字典序
            if (lhs.length != rhs.length)
                return rhs.length - lhs.length;
            else
                for (int i = 0; i != lhs.length; ++i)
                    if (lhs[i] != rhs[i])
                        return rhs[i] - lhs[i];
            return 0;
        };
        result_map = new TreeMap<>(byte_array_comparator); // 初始化result_map
        var frequency_map = new TreeMap<byte[], Integer>(byte_array_comparator); // 记录频数
        for (int i = 0; i != row; ++i)
            frequency_map.merge(pending_alignment[i], 1, Integer::sum); // 统计频数
        Comparator<byte[]> new_byte_array_comparator = (byte[] lhs, byte[] rhs) ->
        {
            if (lhs.length != rhs.length)
                return rhs.length - lhs.length;
            else
            {
                int frequency_diff = frequency_map.get(rhs) - frequency_map.get(lhs);
                if (frequency_diff != 0)
                    return frequency_diff;
                else
                    for (int i = 0; i != lhs.length; ++i)
                        if (lhs[i] != rhs[i])
                            return rhs[i] - lhs[i];
            }
            return 0;
        };
        var order = new TreeSet<>(new_byte_array_comparator); // 用来记录比对顺序
        order.addAll(frequency_map.keySet());
        // order.forEach(element -> System.out.println(frequency_map.get(element) + " " + Pseudo.pseudo2string(element)));
        order.forEach(element -> align(element, frequency_map.get(element)));
        // order.forEach(element -> align(element, 1));
        var ret = new byte[row][];
        for (int i = 0; i != row; ++i)
            ret[i] = result_map.get(pending_alignment[i]);
        return ret;
    }

    // 完成当前串和串池的比对, 更新串池, 并将比对结果加入result_map
    private void align(byte[] curr_str, int frequency)
    {
        if (pool == null)
        { // 第一次加入
            result_map.put(curr_str, curr_str);
            pool = new int[curr_str.length][CHAR_KIND];
            relative_pool = new double[curr_str.length][CHAR_KIND];
            for (int i = 0; i != curr_str.length; ++i)
            {
                pool[i][curr_str[i]] = frequency;
                relative_pool[i][curr_str[i]] = 1;
            }
            return;
        }
        if (curr_str.length == pool.length)
        { // 和当前串池长度相等时
            int match_time = 0; // pool和curr_str对应位置相同的字符数量
            for (int i = 0; i != pool.length; ++i)
                match_time += pool[i][curr_str[i]]; // 此时finished还有没更新
            if ((double) match_time / finished / pool.length > TOLERANCE)
            {
                result_map.put(curr_str, curr_str);
                update_pool(curr_str, frequency, null);
                return;
            }
        }
        if (curr_str.length < pool.length)
        { // 如果当前串长度小于串池, 则尝试在当前串某一位置插入连续空格
            var insertion_position = try_direct_insertion(curr_str);
            if (insertion_position != -1)
            { // 若仅在当前串某一位置插入连续空格的效果不错
                var spaces = new int[curr_str.length + 1];
                spaces[insertion_position] = pool.length - curr_str.length;
                var aligned = Pseudo.insert_spaces(curr_str, spaces, pool.length);
                result_map.put(curr_str, aligned);
                update_pool(aligned, frequency, null);
                return;
            }
        }
        var spaces = dynamic_programming(curr_str);
        var aligned = Pseudo.insert_spaces(curr_str, spaces[0]);
        result_map.put(curr_str, aligned);
        update_pool(aligned, frequency, spaces[1]);
        // System.out.println(Pseudo.pseudo2string(curr_str));
        // System.out.println(Pseudo.pseudo2string(aligned));
    }

    // 对动态规划略作修改以适应pool的机制, 返回需插入空格的位置和数量, 如果需要则修改result_map中的所有结果(不添加)
    private int[][] dynamic_programming(byte[] curr_str)
    {
        final int CURR_GAP_SCORE = -7, POOL_GAP_SCORE = -63, /*MISMATCH_SCORE = 0, */MATCH_SCORE = 10;
        var dp = new double[curr_str.length + 1][pool.length + 1];
        for (int i = 1; i <= curr_str.length; ++i)
            dp[i][0] = i * POOL_GAP_SCORE;
        for (int i = 1; i <= pool.length; ++i)
            dp[0][i] = i * CURR_GAP_SCORE;
        for (int i = 1; i <= curr_str.length; ++i)
            for (int j = 1; j <= pool.length; ++j)
            {
                double /*gap = relative_pool[j - 1][GAP], */match = relative_pool[j - 1][curr_str[i - 1]]/*, mismatch = 1 - gap - match*/;
                dp[i][j] = UtilityFunctions.max(
                        dp[i - 1][j - 1] + match * MATCH_SCORE /*+ gap * POOL_GAP_SCORE + mismatch * MISMATCH_SCORE*//* 不插入空格 */,
                        dp[i - 1][j] + POOL_GAP_SCORE/* 在pool中插入空格 */, dp[i][j - 1] + CURR_GAP_SCORE)/* 在curr_str中插入空格 */;
            }
        var lhs_spaces = new int[curr_str.length + 1];
        var rhs_spaces = new int[pool.length + 1];
        int lhs_index = curr_str.length, rhs_index = pool.length;
        while (lhs_index > 0 && rhs_index > 0)
        { // 回溯
            double match = relative_pool[rhs_index - 1][curr_str[lhs_index - 1]]/*, gap = relative_pool[rhs_index - 1][GAP],
                    mismatch = 1 - relative_pool[rhs_index - 1][curr_str[lhs_index - 1]] - relative_pool[rhs_index - 1][GAP]*/;
            if (dp[lhs_index][rhs_index] == dp[lhs_index - 1][rhs_index - 1] + match * MATCH_SCORE/*+ gap * POOL_GAP_SCORE + mismatch * MISMATCH_SCORE*/)
            { // 能向左上走则向左上走
                --lhs_index;
                --rhs_index;
            }
            else if (dp[lhs_index][rhs_index] == dp[lhs_index][rhs_index - 1] + CURR_GAP_SCORE)
            {
                --rhs_index;
                ++lhs_spaces[lhs_index];
            }
            else
            {
                --lhs_index;
                ++rhs_spaces[rhs_index];
            }
        }
        while (lhs_index > 0)
        {
            --lhs_index;
            ++rhs_spaces[rhs_index];
        }
        while (rhs_index > 0)
        {
            --rhs_index;
            ++lhs_spaces[lhs_index];
        }
        boolean is_spaces_inserted_to_rhs = false;
        for (int i = 0; i != rhs_spaces.length; ++i)
            if (rhs_spaces[i] != 0)
            {
                is_spaces_inserted_to_rhs = true;
                break;
            }
        if (is_spaces_inserted_to_rhs)
        {
            int tmp = 0;
            for (int i = 0; i <= pool.length; ++i)
                tmp += rhs_spaces[i];
            int new_length = tmp + pool.length;
            result_map.forEach((k, v) ->
            {
                var new_v = new byte[new_length];
                for (int i = 0, begin = 0; i != v.length; ++i)
                {
                    int end = begin + rhs_spaces[i];
                    for (int j = begin; j != end; ++j)
                        new_v[j] = GAP;
                    new_v[end] = v[i];
                    begin = end + 1;
                }
                for (int i = 0; i != rhs_spaces[pool.length]; ++i)
                    new_v[new_v.length - i - 1] = GAP;
                result_map.put(k, new_v);
            });
        }
        var ret = new int[2][];
        ret[0] = lhs_spaces; // curr_str
        ret[1] = rhs_spaces; // pool
        return ret;
    }

    // 调用者须保证参数长度小于pool
    // 尝试一次性在当前串某一位置插入足够的空格, 若在某一种情况下能够完全
    private int try_direct_insertion(byte[] curr_str)
    {
        double max_score = 0;
        int optimal_insert_position = -1, dis = pool.length - curr_str.length;
        for (int insert_position = 0; insert_position <= curr_str.length; ++insert_position)
        {
            double curr_score = 0;
            for (int curr_str_index = 0; curr_str_index != curr_str.length; ++curr_str_index) // 计算得分
                curr_score += relative_pool[curr_str_index < insert_position ? curr_str_index : curr_str_index + dis][curr_str[curr_str_index]];
            if (max_score < curr_score)
            {
                max_score = curr_score;
                optimal_insert_position = insert_position;
            }
        }
        return max_score / curr_str.length > TOLERANCE ? optimal_insert_position : -1;
    }

    // 根据上一趟的结果(spaces)更新两个pool, new_here为上一趟加入串的比对结果
    // pool为null时初始化, spaces为null时不需要改变pool的长度
    private void update_pool(byte[] new_here, int frequency, int[] spaces)
    {
        finished += frequency;
        if (spaces == null)
        { // 不需要在pool中插入空格时, 此时pool的长度不变
            for (int i = 0; i != new_here.length; ++i)
            {
                pool[i][new_here[i]] += frequency;
                update_relative_pool();
            }
        }
        else
        { // 需要在pool中插入空格时
            int new_pool_length = pool.length;
            for (int i = 0; i <= pool.length; ++i) new_pool_length += spaces[i];
            var new_pool = new int[new_pool_length][CHAR_KIND];
            for (int i = 0, index = 0; i != pool.length; ++i)
            { // 形成新pool
                int new_index = index + spaces[i]; // [index, new_index)这一段为空格段
                for (int j = index; j != new_index; ++j) // pool默认为0, 不需要从0到CHAR_KIND遍历
                    new_pool[j][GAP] = finished; // 此时finished已经更新过了, 表示包括当前插入串内已完成串的数量
                for (int j = 0; j != CHAR_KIND; ++j)
                    new_pool[new_index][j] = pool[i][j];
                new_pool[new_index][new_here[new_index]] += frequency;
                index = new_index;
            }
            for (int j = 0; j != spaces[pool.length]; ++j) // 最后一个位置的空格
                new_pool[new_pool_length - j - 1][GAP] = finished;
            pool = new_pool;
            update_relative_pool();
        }
    }

    // pool更改完成后更新relative_pool
    private void update_relative_pool()
    {
        if (relative_pool.length != pool.length) relative_pool = new double[pool.length][CHAR_KIND];
        for (int i = 0; i != pool.length; ++i)
            for (int j = 0; j != CHAR_KIND; ++j) relative_pool[i][j] = (double) pool[i][j] / finished;
    }

}
