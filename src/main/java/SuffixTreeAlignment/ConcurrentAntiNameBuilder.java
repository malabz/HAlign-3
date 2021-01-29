package SuffixTreeAlignment;

import Utilities.UtilityFunctions;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static Main.GlobalVariables.thread;

class ConcurrentAntiNameBuilder
{
    private final ExecutorService es = Executors.newFixedThreadPool(thread);
    private final byte[][] sequences;
    private final byte[] centre;
    private final int[][][] name;
    private final int[][][] anti_name;

    static int[][][] build_anti_name(int[][][] name, byte[][] sequences, byte[] centre)
    {
        return new ConcurrentAntiNameBuilder(name, sequences, centre).build().get_anti_name();
    }

    private ConcurrentAntiNameBuilder(int[][][] name, byte[][] sequences, byte[] centre)
    {
        this.name = name;
        this.sequences = sequences;
        this.centre = centre;
        anti_name = new int[sequences.length][][];
    }

    private ConcurrentAntiNameBuilder build()
    {
        for (int i = 0; i != sequences.length; ++i)
            es.submit(new AntiNameBuilder(i));
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
        private final int curr_sqc;

        AntiNameBuilder(int index)
        {
            curr_sqc = index;
        }

        @Override
        public void run()
        {
            ArrayList<Integer> centre_endpoints = new ArrayList<>(name[curr_sqc].length * 2 + 2); // 中心序列各区间端点
            ArrayList<Integer> currnt_endpoints = new ArrayList<>(name[curr_sqc].length * 2 + 2); // 当前序列各区间端点
            centre_endpoints.add(0); // 提前加上第一个字符的索引
            currnt_endpoints.add(0);
            for (int j = 0; j != name[curr_sqc].length; ++j)
            { // 对当前序列每一个子串对
                centre_endpoints.add(name[curr_sqc][j][0]);                        // 中心序列左端点
                centre_endpoints.add(name[curr_sqc][j][0] + name[curr_sqc][j][2]); // 中心序列右端点
                currnt_endpoints.add(name[curr_sqc][j][1]);                        // 当前序列左端点
                currnt_endpoints.add(name[curr_sqc][j][1] + name[curr_sqc][j][2]); // 当前序列右端点
            }
            centre_endpoints.add(centre.length); // 加上序列最后一个字符的索引, 完成分段, 注意这里不需要减一
            currnt_endpoints.add(sequences[curr_sqc].length);

            ArrayList<Integer> al = new ArrayList<>(centre_endpoints.size() * 2);  // 暂存anti_name
            for (int j = 0; j < centre_endpoints.size(); j += 2)
                if (!centre_endpoints.get(j).equals(centre_endpoints.get(j + 1)) || !currnt_endpoints.get(j).equals(currnt_endpoints.get(j + 1)))
                { // 如果两子串长度不都为零
                    al.add(centre_endpoints.get(j));
                    al.add(centre_endpoints.get(j + 1));
                    al.add(currnt_endpoints.get(j));
                    al.add(currnt_endpoints.get(j + 1));
                }

            anti_name[curr_sqc] = UtilityFunctions.to_2d_array(al, 4);
        }
    }

}
