package SuffixTreeAlignment;

import Pairwise.PairwiseAligner;
import Utilities.Pseudo;
import Utilities.UtilityFunctions;

import static Main.GlobalVariables.*;

import java.util.ArrayList;
import java.util.Arrays;

public class SuffixTreeAligner
{

    private final byte[][] sequences;
    private final int row;
    private byte[] centre;
    private int[][][] name;           // 同质子序列对
    private int[][][] anti_name;      // 非同质子序列对
    private int[][] spaces;
    private int[][] centre_spaces;    // 两两比对结果中, 中心序列空格位置和数量
    private int[][] others_spaces;    // 两两比对结果中, 当前序列空格位置和数量

    SuffixTreeAligner(byte[][] sequences)
    {
        this.sequences = sequences;
        row = sequences.length;
    }

    public static byte[][] align(byte[][] sequences, int centre)
    {
        if (centre == -1)
        {
            centre = 0;
            for (int i = 1; i != sequences.length; ++i)
                if (sequences[i].length > sequences[centre].length)
                    centre = i;
        }
        return new SuffixTreeAligner(sequences).align(sequences[centre]).get_result();
    }

    public static byte[][] align(byte[][] sequences, byte[] centre)
    {
        return new SuffixTreeAligner(sequences).align(centre).get_result();
    }

    private SuffixTreeAligner align(byte[] centre)
    {
        this.centre = centre;

        var time_stop = System.currentTimeMillis();
        var launch_time = time_stop;

        SuffixTree st = new SuffixTree(centre);
        System.out.printf("%16s: %d ms\n", "suffix tree", System.currentTimeMillis() - time_stop);
        time_stop = System.currentTimeMillis();

        if (thread > 1)
            name = ConcurrentNameBuilder.build_name(sequences, centre, st);
        else
            build_name(st);
        System.out.printf("%16s: %d ms\n", "name", System.currentTimeMillis() - time_stop);
        time_stop = System.currentTimeMillis();
        check_name();

        if (thread > 1)
            anti_name = ConcurrentAntiNameBuilder.build_anti_name(name, sequences, centre);
        else
            build_anti_name();
        System.out.printf("%16s: %d ms\n", "anti-name", System.currentTimeMillis() - time_stop);
        time_stop = System.currentTimeMillis();
        check_anti_name();

        if (thread > 1)
        {
            var tmp_spc = ConcurrentSequencePairwiseAligner.pairwise_align(sequences, centre, anti_name);
            centre_spaces = tmp_spc.get_first();
            others_spaces = tmp_spc.get_second();
        }
        else
        {
            if (first_iteration) pairwise_align();
            else pairwise_align(0);
        }
        System.out.printf("%16s: %d ms\n", "n-w", System.currentTimeMillis() - time_stop);
        time_stop = System.currentTimeMillis();
        check_pairwise_align();

        if (thread > 1)
            spaces = ConcurrentFinalSpaceBuilder.build_final_spaces(sequences, centre, centre_spaces, others_spaces);
        else
            generate_final_spaces();
        System.out.printf("%16s: %d ms\n", "spaces", System.currentTimeMillis() - time_stop);
        System.out.printf("%16s: %d ms\n", "total", System.currentTimeMillis() - launch_time);

        return this;
    }

    private void build_name(SuffixTree st)
    {
        name = new int[row][][];
        for (int i = 0; i != row; ++i)
            if (sequences[i] == centre) name[i] = new int[][]{{0, 0, centre.length}};
//            else name[i] = st.align_with(sequences[i]);
            else name[i] = IdenticalSubsequencePairsOptimizer.optimize(st.get_identical_subsequence_pairs(sequences[i]));
    }

    // 测试name数组是否有效: 即检查所有同质子串对是否相等
    @SuppressWarnings("unused")
    private void check_name()
    {
        for (int i = 0; i != row; ++i)
        {
            for (int j = 0; j != name[i].length; ++j)
            {
                assert name[i][j][0] >= 0;
                assert name[i][j][1] >= 0;
                assert name[i][j][0] + name[i][j][2] <= centre.length;
                assert name[i][j][1] + name[i][j][2] <= sequences[i].length;
                assert j == 0 || name[i][j][0] >= name[i][j - 1][0] + name[i][j - 1][2] && name[i][j][1] >= name[i][j - 1][1] + name[i][j - 1][2];
                for (int k = name[i][j][0], l = name[i][j][1]; k != name[i][j][0] + name[i][j][2]; ++k, ++l)
                    assert centre[k] == sequences[i][l] : "\n" +
                            Pseudo.pseudo_to_string(Arrays.copyOfRange(centre      , name[i][j][0], name[i][j][0] + name[i][j][2])) + "\n" +
                            Pseudo.pseudo_to_string(Arrays.copyOfRange(sequences[i], name[i][j][1], name[i][j][1] + name[i][j][2]));
            }
        }
    }

    private void build_anti_name()
    {
        anti_name = new int[row][][];
        for (int i = 0; i != row; ++i)
        { // 对每一个序列
            ArrayList<Integer> centre_endpoints = new ArrayList<>(name[i].length * 2 + 2); // 中心序列各区间端点
            ArrayList<Integer> cur_endpoints = new ArrayList<>(name[i].length * 2 + 2); // 当前序列各区间端点
            centre_endpoints.add(0); // 提前加上第一个字符的索引
            cur_endpoints.add(0);
            for (int j = 0; j != name[i].length; ++j)
            { // 对当前序列每一个子串对
                centre_endpoints.add(name[i][j][0]);                    // 中心序列左端点
                centre_endpoints.add(name[i][j][0] + name[i][j][2]);    // 中心序列右端点
                cur_endpoints.add(name[i][j][1]);                       // 当前序列左端点
                cur_endpoints.add(name[i][j][1] + name[i][j][2]);       // 当前序列右端点
            }
            centre_endpoints.add(centre.length); // 加上序列最后一个字符的索引, 完成分段, 注意这里不需要减一
            cur_endpoints.add(sequences[i].length);
            ArrayList<Integer> al = new ArrayList<>(centre_endpoints.size() * 2); // 暂存anti_name
            for (int j = 0; j < centre_endpoints.size(); j += 2)
            { // 这里减1正好得到分段数
                if (!centre_endpoints.get(j).equals(centre_endpoints.get(j + 1)) || !cur_endpoints.get(j).equals(cur_endpoints.get(j + 1)))
                { // 如果两子串长度不都为零
                    al.add(centre_endpoints.get(j));
                    al.add(centre_endpoints.get(j + 1));
                    al.add(cur_endpoints.get(j));
                    al.add(cur_endpoints.get(j + 1));
                }
            }
            anti_name[i] = UtilityFunctions.to_2d_array(al, 4);
        }
    }

    @SuppressWarnings("unused")
    private void check_anti_name()
    {
        String err_msg_duplication = "name and anti_name have a duplicated cover";
        String err_msg_missing = "name and anti_name haven't covered all the characters";
        for (int i = 0; i != row; ++i)
        {
            boolean[] centre_bool = new boolean[centre.length], // 中心序列是否被完全覆盖
                    current_bool = new boolean[sequences[i].length]; // 当前序列

            for (int j = 0; j != name[i].length; ++j) // 对name的每一段
                for (int k = name[i][j][0], l = name[i][j][1]; k != name[i][j][0] + name[i][j][2]; ++k, ++l)
                {
                    centre_bool[k] = true;
                    current_bool[l] = true;
                }

            for (int j = 0; j != anti_name[i].length; ++j)
            { // 对anti_name的每一段
                for (int k = anti_name[i][j][0]; k != anti_name[i][j][1]; ++k)
                {
                    assert !centre_bool[k] : err_msg_duplication;
                    centre_bool[k] = true;
                }
                for (int k = anti_name[i][j][2]; k != anti_name[i][j][3]; ++k)
                {
                    assert !current_bool[k] : err_msg_duplication;
                    current_bool[k] = true;
                }
            }
            for (int j = 0; j != centre.length; ++j)
                assert centre_bool[j] : err_msg_missing; // 检查中心序列是否被完全覆盖
            for (int j = 0; j != sequences[i].length; ++j)
                assert current_bool[j] : err_msg_missing; // 检查当前序列
        }
    }

    private void pairwise_align()
    {
        centre_spaces = new int[row][centre.length + 1]; // 存放中心序列增加空格的位置
        others_spaces = new int[row][];
        for (int i = 0; i != row; ++i)
        {
//            System.out.println("pairwise_align(): " + i);
            others_spaces[i] = new int[sequences[i].length + 1];
            for (int j = 0; j != anti_name[i].length; ++j)
            {
//                System.out.println(j + " " + (anti_name[i][j][1] - anti_name[i][j][0]) + " " + (anti_name[i][j][3] - anti_name[i][j][2]));
                PairwiseAligner.local_align(centre, centre_spaces[i], anti_name[i][j][0], anti_name[i][j][1],
                        sequences[i], others_spaces[i], anti_name[i][j][2], anti_name[i][j][3]);
            }
        }
    }

    private void pairwise_align(int nll)
    {
        centre_spaces = new int[row][centre.length + 1]; // 存放中心序列增加空格的位置
        others_spaces = new int[row][];

        var aliner = new Pairwise.NeedlemanWunschNoInsertInLhs(centre_pool);
        for (int i = 0; i != row; ++i)
        {
            others_spaces[i] = new int[sequences[i].length + 1];
            for (int j = 0; j != anti_name[i].length; ++j)
            {
                var curr = aliner.align(anti_name[i][j][0], anti_name[i][j][1], sequences[i], anti_name[i][j][2], anti_name[i][j][3]);
                put_in(curr, others_spaces[i], anti_name[i][j][2]);
            }
        }
    }

    private void put_in(int[] src, int[] des, int des_bgn)
    {
        for (int i = 0; i != src.length; ++i) des[des_bgn + i] += src[i];
    }

    @SuppressWarnings("unused")
    private void check_pairwise_align()
    {
        for (int i = 0; i != row; ++i)
        {
            int a_result_length = centre.length, b_result_length = sequences[i].length;
            for (int j = 0; j <= centre.length; ++j) a_result_length += centre_spaces[i][j];
            for (int j = 0; j <= sequences[i].length; ++j) b_result_length += others_spaces[i][j];
            assert a_result_length == b_result_length : "error found in pairwise alignment";
        }
    }

    // 生成spaces, 最终版, 用来生成比对结果
    private void generate_final_spaces()
    {
        var final_centre_spaces = new int[centre.length + 1]; // 记录中心序列中空格的添加位置和数量
        for (int i = 0; i != row; ++i)
            for (int j = 0; j <= centre.length; ++j)
                if (final_centre_spaces[j] < centre_spaces[i][j]) final_centre_spaces[j] = centre_spaces[i][j];

        spaces = new int[row][];
        for (int i = 0; i != row; ++i)
        {
            spaces[i] = new int[sequences[i].length + 1];
            System.arraycopy(others_spaces[i], 0, spaces[i], 0, sequences[i].length + 1); // 先把两两比对结果的空格数量拷贝过来
            int[] dis = new int[centre.length + 1]; // 中心序列插入空格最终和当前数量的差值
            for (int j = 0; j <= centre.length; ++j) dis[j] = final_centre_spaces[j] - centre_spaces[i][j];
            int centre_pointer = 0, counterpart_pointer = 0; // 遍历位置
            while (centre_pointer <= centre.length/* && counterpart_pointer <= lengths[i]*/)
            {
//                    if (counterpart_pointer > lengths[i])
//                    {
//                        System.out.println("out of range");
//                        break;
//                    }
                if (centre_spaces[i][centre_pointer] == 0 && others_spaces[i][counterpart_pointer] == 0)
                { // 如果当前两个位置上空格数量均为0
                    spaces[i][counterpart_pointer] += dis[centre_pointer];
                    dis[centre_pointer] = 0;
//                        if (added[centre_pointer] > 0)
//                            System.out.println("duplicated add 1 " + added[centre_pointer]);
//                        else
//                            added[centre_pointer] = 1;
                    ++counterpart_pointer;
                    ++centre_pointer;
                }
                else if (centre_spaces[i][centre_pointer] == 0)
                { // 如果只有当前序列索引处空格数量不为0
                    while (others_spaces[i][counterpart_pointer] > 0)
                    { // 按照当前序列索引处空格数量
                        --others_spaces[i][counterpart_pointer];
                        spaces[i][counterpart_pointer] += dis[centre_pointer]; // 将中心序列当前索引处空格差异数量并入
                        dis[centre_pointer] = 0;
//                            if (added[centre_pointer] > 0)
//                                System.out.println("duplicated add 2 " + added[centre_pointer]);
//                            else
//                                added[centre_pointer] = 2;
                        ++centre_pointer; // 序列索引向前移动
                    }
                }
                else
                { // 如果只有中心序列索引处空格数量不为0
                    while (centre_spaces[i][centre_pointer] > 0)
                    { // 按照中心序列索引处空格数量
                        --centre_spaces[i][centre_pointer];
                        ++counterpart_pointer; // 当前序列索引向前移动
                    }
                    spaces[i][counterpart_pointer] += dis[centre_pointer]; // 最后加上差值
                    dis[centre_pointer] = 0;
//                        if (added[centre_pointer] > 0)
//                            System.out.println("duplicated add 3 " + added[centre_pointer]);
//                        else
//                            added[centre_pointer] = 3;
                }
            }
//                int result_length_tmp = lengths[i];
//                for (int j = 0, tmp = lengths[i]; j <= tmp; ++j)
//                    result_length_tmp += spaces[i][j];
//                System.out.println(i + " " + result_length_tmp);
//                for (int j = 0; j <= centre_length; ++j)
//                    if (dis[j] > 0 && added[j] == 0)
//                        System.out.println("omit an add " + centre_length + " " + j);
        }
    }

    @SuppressWarnings("unused")
    private void check_result(byte[][] result)
    {
        int column = result[0].length;
        for (int i = 1; i != row; ++i) assert result[i].length == column;
        for (int j = 0; j != column; ++j)
        {
            boolean all_spaces = true;
            for (int i = 0; i != row; ++i)
            {
                if (result[i][j] != GAP)
                {
                    all_spaces = false;
                    break;
                }
            }
            assert !all_spaces;
        }
    }

//    private void post_process()
//    {
//        long start = System.currentTimeMillis();
//        pseudo = new PostProcessor().post_process(pseudo);
//        System.out.println("time consumed to post_process:" + (System.currentTimeMillis() - start) / 1000);
//    }

    // 插入空格得到比对完成的伪序列
    private byte[][] get_result()
    {
        int result_length = sequences[0].length;
        var result = new byte[row][];
        for (int i = 0; i <= sequences[0].length; ++i) result_length += spaces[0][i];
        for (int i = 0; i != row; ++i) result[i] = Pseudo.insert_spaces(sequences[i], spaces[i], result_length);
//        check_result(result);
        return result;
    }

    private int[][] get_spaces()
    {
        return spaces;
    }

}
