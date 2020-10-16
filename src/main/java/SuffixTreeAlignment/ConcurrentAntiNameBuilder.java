package SuffixTreeAlignment;

import Utilities.UtilityFunctions;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static Main.GlobalVariables.*;

class ConcurrentAntiNameBuilder
{
    private final ExecutorService es = Executors.newFixedThreadPool(thread);
    private final byte[][] sequences;
    private final int[][][] name;
    private final int[][][] anti_name;
    private final int centre_index;

    static int[][][] build_anti_name(int[][][] name, byte[][] sequences, int centre_index)
    {
        return new ConcurrentAntiNameBuilder(name, sequences, centre_index).build().get_anti_name();
    }

    private ConcurrentAntiNameBuilder(int[][][] name, byte[][] sequences, int centre_index)
    {
        this.name = name;
        this.sequences = sequences;
        this.centre_index = centre_index;
        anti_name = new int[sequences.length][][];
    }

    private ConcurrentAntiNameBuilder build()
    {
        for (int i = 0; i != sequences.length; ++i) es.submit(new AntiNameBuilder(i));
        es.shutdown();
        try { es.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return this;
    }

    private int[][][] get_anti_name()
    {
        return anti_name;
    }

    private class AntiNameBuilder implements Runnable
    {
        private final int curr_index;

        AntiNameBuilder(int index)
        {
            curr_index = index;
        }

        @Override
        public void run()
        {
            ArrayList<Integer> centre_endpoints = new ArrayList<>(name[curr_index].length * 2 + 2); // 中心序列各区间端点
            ArrayList<Integer> cur_endpoints = new ArrayList<>(name[curr_index].length * 2 + 2); // 当前序列各区间端点
            centre_endpoints.add(0); // 提前加上第一个字符的索引
            cur_endpoints.add(0);
            for (int j = 0; j != name[curr_index].length; ++j)
            { // 对当前序列每一个子串对
                centre_endpoints.add(name[curr_index][j][0]);                             // 中心序列左端点
                centre_endpoints.add(name[curr_index][j][0] + name[curr_index][j][2]);    // 中心序列右端点
                cur_endpoints.add(name[curr_index][j][1]);                                // 当前序列左端点
                cur_endpoints.add(name[curr_index][j][1] + name[curr_index][j][2]);       // 当前序列右端点
            }
            centre_endpoints.add(sequences[centre_index].length); // 加上序列最后一个字符的索引, 完成分段, 注意这里不需要减一
            cur_endpoints.add(sequences[curr_index].length);
            ArrayList<Integer> al = new ArrayList<>(centre_endpoints.size() * 2); // 暂存anti_name
            for (int j = 0, tmp = centre_endpoints.size() - 1; j <= tmp; j += 2)
            { // 这里减1正好得到分段数
                if (!centre_endpoints.get(j).equals(centre_endpoints.get(j + 1)) || !cur_endpoints.get(j).equals(cur_endpoints.get(j + 1)))
                { // 如果两子串长度不都为零
                    al.add(centre_endpoints.get(j));
                    al.add(centre_endpoints.get(j + 1));
                    al.add(cur_endpoints.get(j));
                    al.add(cur_endpoints.get(j + 1));
                }
            }
            anti_name[curr_index] = UtilityFunctions.to_2d_array(al, 4);
        }
    }

}
