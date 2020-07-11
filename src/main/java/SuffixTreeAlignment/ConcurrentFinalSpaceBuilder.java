package SuffixTreeAlignment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static Main.GlobalVariables.THREAD;

public class ConcurrentFinalSpaceBuilder
{
    private final ExecutorService es = Executors.newFixedThreadPool(THREAD);

    private final byte[][] sequences;
    private final int centre_index;
    private final int[][] centre_spaces;
    private final int[][] others_spaces;
    private final int[] final_centre_spaces;
    private final int[][] spaces;

    static int[][] build_final_spaces(byte[][] sequences, int centre_index, int[][]centre_spaces, int[][] others_spaces)
    {
        return new ConcurrentFinalSpaceBuilder(sequences, centre_index, centre_spaces, others_spaces).build().get_spaces();
    }

    private ConcurrentFinalSpaceBuilder(byte[][] sequences, int centre_index, int[][] centre_spaces, int[][] others_spaces)
    {
        this.sequences = sequences;
        this.centre_index = centre_index;
        this.centre_spaces = centre_spaces;
        this.others_spaces = others_spaces;
        spaces = new int[sequences.length][];
        final_centre_spaces = new int[sequences[centre_index].length + 1];
        for (int i = 0; i != sequences.length; ++i)
            for (int j = 0; j <= sequences[centre_index].length; ++j)
                if (final_centre_spaces[j] < centre_spaces[i][j]) final_centre_spaces[j] = centre_spaces[i][j];
    }

    private ConcurrentFinalSpaceBuilder build()
    {
        for (int i = 0; i != sequences.length; ++i) es.submit(new FinalSpacesBuilder(i));
        es.shutdown();
        try { es.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return this;
    }

    private int[][] get_spaces()
    {
        return spaces;
    }

    private class FinalSpacesBuilder implements Runnable
    {
        private final int curr_index;

        FinalSpacesBuilder(int index)
        {
            curr_index = index;
        }

        @Override
        public void run()
        {
            spaces[curr_index] = new int[sequences[curr_index].length + 1];
            System.arraycopy(others_spaces[curr_index], 0, spaces[curr_index], 0, sequences[curr_index].length + 1); // 先把两两比对结果的空格数量拷贝过来
            int[] dis = new int[sequences[centre_index].length + 1]; // 中心序列插入空格最终和当前数量的差值
            for (int j = 0; j <= sequences[centre_index].length; ++j) dis[j] = final_centre_spaces[j] - centre_spaces[curr_index][j];
            int center_pointer = 0, counterpart_pointer = 0; // 遍历位置
            while (center_pointer <= sequences[centre_index].length)
            {
                if (centre_spaces[curr_index][center_pointer] == 0 && others_spaces[curr_index][counterpart_pointer] == 0)
                { // 如果当前两个位置上空格数量均为0
                    spaces[curr_index][counterpart_pointer] += dis[center_pointer];
                    dis[center_pointer] = 0;
                    ++counterpart_pointer;
                    ++center_pointer;
                }
                else if (centre_spaces[curr_index][center_pointer] == 0)
                { // 如果只有当前序列索引处空格数量不为0
                    while (others_spaces[curr_index][counterpart_pointer] > 0)
                    { // 按照当前序列索引处空格数量
                        --others_spaces[curr_index][counterpart_pointer];
                        spaces[curr_index][counterpart_pointer] += dis[center_pointer]; // 将中心序列当前索引处空格差异数量并入
                        dis[center_pointer] = 0;
                        ++center_pointer; // 序列索引向前移动
                    }
                }
                else
                { // 如果只有中心序列索引处空格数量不为0
                    while (centre_spaces[curr_index][center_pointer] > 0)
                    { // 按照中心序列索引处空格数量
                        --centre_spaces[curr_index][center_pointer];
                        ++counterpart_pointer; // 当前序列索引向前移动
                    }
                    spaces[curr_index][counterpart_pointer] += dis[center_pointer]; // 最后加上差值
                    dis[center_pointer] = 0;
                }
            }
        }
    }

}
