// TODO:
// package Multidimensional;
//
// import Utilities.Combination;
// import MultipleSequenceAlignment.GlobalAligningMethod;
// import static Main.Global.*;
//
// /**
//  * 这个类使用多维动态规划完成多序列比对, 可以认为是能得到最大sp的方法, 但时空复杂度极高
//  */
// public final class MultidimensionalDynamicProgrammingAlignment extends GlobalAligningMethod
// {
//
//     private MultidimensionalArray dp;
//     // {-1, 0, -1}, 回溯起点代表编辑距离
//     // {0, 1, 0}, 代表最大公共子串长度
//     // {-1, 1, -1}, 结果不错
//     private static final int GAP_SCORE = -2, MATCH_SCORE = 1, MISMATCH_SCORE = -1;
//     private boolean[] is_selected; // 各个维度是否需要处理
//     private int[] index; // 当前坐标
//     private int[][][][] combination_collection; // 组合集合
//
//     public MultidimensionalDynamicProgrammingAlignment(String input_file_name)
//     {
//         super(input_file_name);
//     }
//
//     public MultidimensionalDynamicProgrammingAlignment(String[] sequences)
//     {
//         super(sequences);
//     }
//
//     /**
//      * 生成结果
//      */
//     public void align()
//     {
//         initialise();
//         build_dp();
// //        if (row == 2) // 临时, 如果是二维dp数组则输出观察
// //        {
// //            int[] tmp_index = new int[2];
// //            for (int i = 0, tmp_1 = lengths[0]; i <= tmp_1; ++i)
// //            {
// //                for (int j = 0, tmp_2 = lengths[1]; j <= tmp_2; ++j)
// //                {
// //                    tmp_index[0] = i;
// //                    tmp_index[1] = j;
// //                    System.out.printf("%d\t\t", dp.get_element(tmp_index));
// //                }
// //                System.out.println();
// //            }
// //            System.out.println();
// //        }
// //        else // 否则调用print显示所有元素
// //        {
// //            dp.print();
// //        }
//         trace_back();
//         insert_spaces();
//         generate_result();
//     }
//
//     // 该初始化的全部在这里初始化
//     private void initialise()
//     {
//         // dp数组
//         int[] size = new int[row];
//         for (int i = 0; i < row; ++i)
//         {
//             size[i] = lengths[i] + 1; // 注意, 动态规划数组每一维长度总比对应序列长度多1
//         }
//         dp = new MultidimensionalArray(size);
//         dp.fill_with(Integer.MIN_VALUE / 2); // 将数组元素初始化为MIN_VALUE / 2, 方便后面计算得分
//         for (int i = 0; i < row; ++i) // 对每一个维度
//         {
//             int[] tmp_index = new int[row];
//             for (int j = 0, tmp = lengths[i]; j <= tmp; ++j) // 对该维度上每一个元素
//             {
//                 tmp_index[i] = j; // 设定坐标
//                 dp.set_element(tmp_index, GAP_SCORE * j * (row - 1)); // 将该坐标处矩阵初始化为(空格分数 * 空格数量 * (其它序列数量))
//             }
//         }
//         // index数组, 存放当前坐标, 默认所有元素为0, 即原点
//         index = new int[row];
//         // is_selected数组, 全部置为true。即, 初始状态下, 当前所有维度都要处理
//         is_selected = new boolean[row];
//         for (int i = 0; i < row; ++i)
//         {
//             is_selected[i] = true;
//         }
//         // 在按照添加空格多少便利所有回溯可能方向时会用到很多组合数, 统一完成减少重复计算
//         combination_collection = new int[row + 1][row + 1][][]; // 为增加可读性, 稍微浪费点内存
//         for (int i = 1; i <= row; ++i) // 第一维是从m中选（1 <= m <= row）, 从1维中选也是可以的
//         {
//             for (int j = 1; j < i; ++j) // 第二维是选出n个（1 <= n < m）, 没有被选中的会被插入空格
//             // 同一个位置不能全是空格, 所以至少需要有一个被选中；而全部被选中的情况由排除法决定, 所以小于m即可
//             {
//                 // 第三维是第c种选法（combination_number(m, n)）, 第四维是被选中元素的索引
//                 combination_collection[i][j] = new Combination(i, j).get_re();
//             }
//         }
//     }
//
//     // 初始化和生成动态规划矩阵
//     private void build_dp()
//     {
//         int selected = 0; // 一共被选中几维
//         for (boolean i : is_selected) // 得到当前需要处理的维度数量
//         {
//             if (i)
//             {
//                 ++selected;
//             }
//         }
//         if (selected != 1) // 否则, 各个维度坐标轴上的元素已经初始化过了, 结束递归
//         {
//             int[] indexes = new int[selected];
//             for (int i = 0, k = 0; i < row; ++i) // 得到当前需要处理的维度, 存入indexes数组
//             {
//                 if (is_selected[i])
//                 {
//                     indexes[k++] = i;
//                 }
//             }
//             for (int i = 0; i < selected; ++i) // 初始化要, 先分别去掉每一维, 递归完成对应n-1维dp数组的建立
//             {
//                 if (i > 0)
//                 {
//                     is_selected[indexes[i - 1]] = true; // 将前一维放回去
//                 }
//                 is_selected[indexes[i]] = false; // 去掉一维
//                 build_dp(); // 建立n - 1维dp数组
//             }
//             is_selected[row - 1] = true; // 将最后一维放回去
//             fill_dp(0, indexes); // 初始化完成, 进行动态规划, 0为递归深度, indexes为需要处理的维度信息
//         }
//     }
//
//     // 每次递归处理一维, 对每个点调用put_element函数, deep代表递归深度（0开始, indexes.length结束）
//     // 其实该函数只负责遍历当前空间中的所有点, 即indexes代表的当前空间
//     // 该函数默认当前空间已经初始化完成
//     private void fill_dp(int deep, int[] indexes)
//     {
//         if (deep == indexes.length) // 最后一维, 递归结束标志
//         {
//             put_element(indexes);
//         }
//         else // 如果不是最后一维, 继续往下递归
//         {
//             for (int i = 1; i <= lengths[indexes[deep]]; ++i) // 对于该维度上的每一个点
//             {
//                 index[indexes[deep]] = i; // indexes[deep]代表当前处理的维度
//                 fill_dp(deep + 1, indexes); // 继续递归
//             }
//             index[indexes[deep]] = 0; // 还原index数组, 不还原会错, 不知道为啥
//         }
//     }
//
//     // 将一点处的得分填入数组
//     private void put_element(int[] indexes)
//     {
//         int[] tmp_index = new int[row];
//         System.arraycopy(index, 0, tmp_index, 0, row);
// //        util.output_index(tmp_index);
// //        System.out.println(": ");
//         dp.set_element(index, compute_element(tmp_index, indexes, 0)); // 计算并填入
//     }
//
//     // 计算当前坐标的得分, 选出最高的返回, 递归完成, deep为递归深度, 初始为0, 每一次递归代表一个维度
//     // 因为多维数组元素被初始化为Integer.MIN_VALUE, 所以无需排除index本身, index == tmp_index时, 计算结果为MIN_VALUE
//     private int compute_element(int[] tmp_index, int[] indexes, int deep)
//     {
//         if (deep != indexes.length) // 继续递归
//         {
//             int tmp_1 = compute_element(tmp_index, indexes, deep + 1); // 当前维度坐标不变
//             --tmp_index[indexes[deep]]; // 当前维度坐标 - 1
//             int tmp_2 = compute_element(tmp_index, indexes, deep + 1);
//             ++tmp_index[indexes[deep]]; // 恢复当前维度
//             return Math.max(tmp_1, tmp_2); // 返回较大者
//         }
//         else // 不在向下递归, 计算返回结果
//         {
// //            int re = dp.get_element(tmp_index) + compute_score(tmp_index);
// //            System.out.print("\t\t");
// //            util.output_index(tmp_index);
// //            System.out.println(": " + re);
// //            return re;
//             return dp.get_element(tmp_index) + compute_score(tmp_index);
//         }
//     }
//
//     // 得到一个方向的得分
//     private int compute_score(int[] tmp_index)
//     {
//         byte[] cur_char = new byte[row];
//         for (int i = 0; i != row; ++i) // 对每一个维度, 得到对应字符
//         {
//             cur_char[i] = index[i] - tmp_index[i] == 1 ? sequences[i][tmp_index[i]] : GAP;
//         }
//         return score_of_str(cur_char);
//     }
//
//     // 返回字符数组的得分, 为取两个字符组合的得分之和
//     private int score_of_str(byte[] str)
//     {
//         int ret = 0, hyphen_number = 0;
//         for (int i = 0, j = 0; i != row; ++i)
//         {
//             if (str[i] == GAP)
//             {
//                 ++hyphen_number; // 统计hyphen数量
//             }
//             else
//             {
//                 str[j++] = str[i]; // 将有效字符挪到数组前部
//             }
//         }
//         int char_number = row - hyphen_number; // 有效字符数
//         for (int i = 0; i < char_number - 1; ++i)
//         {
//             for (int j = i + 1; j < char_number; ++j)
//             {
//                 if (i >= str.length || j >= str.length)
//                     System.err.println(i + " " + char_number + " " + str.length);
//                 ret += str[i] == str[j] ? MATCH_SCORE : MISMATCH_SCORE; // 有效字符间得分
//             }
//         }
//         ret += hyphen_number * char_number * GAP_SCORE; // 再加上有效字符和空格间的得分
//         return ret;
//     }
//
//     // 返回两个字符的得分, 暂时没用了
//     @SuppressWarnings("unused")
//     private int score_of_two_chars(byte lhs, byte rhs)
//     {
//         return lhs == GAP ? rhs == GAP ? 0 : GAP_SCORE : rhs == GAP ? GAP_SCORE : lhs == rhs ? MATCH_SCORE : MISMATCH_SCORE;
//     }
//
//     // 回溯, 生成空格位置数组
//     private void trace_back()
//     {
//         // 设置当前坐标, 从终点开始回溯
//         for (int i = 0; i != row; ++i)
//         {
//             index[i] = lengths[i];
//         }
// //        util.output_index(index);
// //        System.out.println(": ");
//         int iter = total_length; // 回溯时需要走多少步
//         while (iter > 0) // 获得下一步位置, 完成步数自减、向下一点移动并计数空格
//         {
//             int[] next_index = back_to_where(); // 下一步
// //            util.output_index(next_index);
// //            System.out.println();
//             for (int i = 0; i < row; ++i) // 对每个维度
//             {
//                 if (index[i] - next_index[i] > 0) // 如果在这个维度上往回走了
//                 {
//                     --iter; // 步数自减
//                 }
//                 else // 如果在这个维度上没有往回走
//                 {
//                     ++spaces[i][index[i]]; // 空格自增
//                 }
//             }
//             index = next_index; // 往回走, 注意这里不需要arraycopy也可以
//         }
//     }
//
//     // 返回下一步的坐标, 这里有个问题, 到底先走边缘还是先走内部会影响到比对结果
//     // 猜想：先走边缘会增加比对对间隙的容忍度
//     private int[] back_to_where()
//     {
//         int unfinished = 0; // 尚未跑完的维度数量
//         for (int i : index)
//         {
//             if (i > 0)
//             {
//                 ++unfinished;
//             }
//         }
//         int[] indexes = new int[unfinished]; // 记录尚未跑完的维度信息, 为防止某一坐标自减为负
//         for (int i = 0, j = 0; i < row; ++i)
//         {
//             if (index[i] > 0)
//             {
//                 indexes[j++] = i;
//             }
//         }
//         for (int i = 1; i < unfinished; ++i) // 从unfinished中挑出i个, 不再验证最后一种情况, 即排除其他情况后, 自然选择
//         {
//             int[][] cur_combination = combination_collection[unfinished][i]; // 当前组合
//             for (int[] j : cur_combination) // 每一种组合
//             {
//                 int[] tmp_index = new int[row]; // 都对应一个坐标, 代表从tmp_index到index
//                 System.arraycopy(index, 0, tmp_index, 0, row); // 从index开始
//                 for (int k = 0; k < i; ++k) // 对于每一个被选中的维度
//                 {
//                     --tmp_index[indexes[j[k]]]; // 将该维度上坐标 - 1, 选中对应字符, 即不空格
//                 }
// //                int pre_element = dp.get_element(tmp_index), score = compute_score(tmp_index), cur_element = dp.get_element(index);
// //                System.out.print('\t');
// //                util.output_index(tmp_index);
// //                System.out.println("\n\t\tpre_element: " + pre_element);
// //                System.out.println("\t\tscore      : " + score);
// //                System.out.println("\t\tcur_element: " + cur_element);
// //                if (pre_element + score == cur_element)
// //                {
// //                    System.out.println("\tmatch");
// //                    return tmp_index;
// //                }
//                 if (/*dp.check_index(tmp_index) && */compute_score(tmp_index) + dp.get_element(tmp_index) == dp.get_element(index)) // 如果可以在此方向回溯
//                 {
//                     return tmp_index;
//                 }
//             }
//         }
//         int[] tmp_index = new int[row];
//         for (int i = 0; i < row; ++i)
//         {
//             tmp_index[i] = index[i] > 0 ? index[i] - 1 : 0;
//         }
// //        System.out.println("\tunmatch!");
//         return tmp_index; // 排除其他情况后, 自然选择最后一种, 即在所有不为0维度上都向前走1
//     }
//
// }