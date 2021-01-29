package SuffixTreeAlignment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static Main.GlobalVariables.thread;

public class ConcurrentFinalSpaceBuilder
{
    private final ExecutorService es = Executors.newFixedThreadPool(thread);

    private final byte[][] sequences;
    private final byte[] centre;
    private final int[][] centre_spaces;
    private final int[][] others_spaces;
    private final int[] final_centre_spaces;
    private final int[][] spaces;

    static int[][] build_final_spaces(byte[][] sequences, byte[] centre, int[][]centre_spaces, int[][] others_spaces)
    {
        return new ConcurrentFinalSpaceBuilder(sequences, centre, centre_spaces, others_spaces).build().get_spaces();
    }

    private ConcurrentFinalSpaceBuilder(byte[][] sequences, byte[] centre, int[][] centre_spaces, int[][] others_spaces)
    {
        this.sequences = sequences;
        this.centre = centre;
        this.centre_spaces = centre_spaces;
        this.others_spaces = others_spaces;
        spaces = new int[sequences.length][];
        final_centre_spaces = new int[centre.length + 1];
        for (int i = 0; i != sequences.length; ++i)
            for (int j = 0; j <= centre.length; ++j)
                if (final_centre_spaces[j] < centre_spaces[i][j]) final_centre_spaces[j] = centre_spaces[i][j];
    }

    private ConcurrentFinalSpaceBuilder build()
    {
        for (int i = 0; i != sequences.length; ++i)
            es.submit(new FinalSpacesBuilder(i));
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
        private final int curr_sqc;

        FinalSpacesBuilder(int index)
        {
            curr_sqc = index;
        }

        @Override
        public void run()
        {
            spaces[curr_sqc] = new int[sequences[curr_sqc].length + 1];
            System.arraycopy(others_spaces[curr_sqc], 0,
                    spaces[curr_sqc], 0,
                    sequences[curr_sqc].length + 1); // 先把两两比对结果的空格数量拷贝过来

            int[] diff = new int[centre.length + 1]; // 中心序列插入空格最终和当前数量的差值
            for (int j = 0; j <= centre.length; ++j)
                diff[j] = final_centre_spaces[j] - centre_spaces[curr_sqc][j];

            int centre_idx = 0, curr_idx = 0;
            while (centre_idx <= centre.length)
            {
                if (centre_spaces[curr_sqc][centre_idx] == 0 && others_spaces[curr_sqc][curr_idx] == 0)
                {
                    spaces[curr_sqc][curr_idx] += diff[centre_idx];
                    diff[centre_idx] = 0;
                    ++curr_idx;
                    ++centre_idx;
                }
                else if (centre_spaces[curr_sqc][centre_idx] == 0)
                {
                    while (others_spaces[curr_sqc][curr_idx] > 0)
                    { // 按照当前序列索引处空格数量
                        --others_spaces[curr_sqc][curr_idx];
                        spaces[curr_sqc][curr_idx] += diff[centre_idx]; // 将中心序列当前索引处空格差异数量并入
                        diff[centre_idx] = 0;
                        ++centre_idx;
                    }
                }
                else
                {
                    while (centre_spaces[curr_sqc][centre_idx] > 0)
                    { // 按照中心序列索引处空格数量
                        --centre_spaces[curr_sqc][centre_idx];
                        ++curr_idx; // 当前序列索引向前移动
                    }
                    spaces[curr_sqc][curr_idx] += diff[centre_idx]; // 最后加上差值
                    diff[centre_idx] = 0;
                }
            }
        }
    }

}
