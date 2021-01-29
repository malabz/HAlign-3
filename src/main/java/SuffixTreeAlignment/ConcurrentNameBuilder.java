package SuffixTreeAlignment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static Main.GlobalVariables.thread;

class ConcurrentNameBuilder
{

    private final ExecutorService es = Executors.newFixedThreadPool(thread);
    private final SuffixTree st;
    private final byte[][] sequences;
    private final byte[] centre;
    private final int[][][] name;

    static int[][][] build_name(byte[][]sequences, byte[] centre, SuffixTree st)
    {
        return new ConcurrentNameBuilder(sequences, centre, st).build().get_name();
    }

    private ConcurrentNameBuilder(byte[][] sequences, byte[] centre, SuffixTree st)
    {
        this.sequences = sequences;
        this.centre = centre;
        this.st = st;
        name = new int[sequences.length][][];
    }

    private ConcurrentNameBuilder build()
    {
        for (int i = 0; i != sequences.length; ++i)
            es.submit(new NameBuilder(i));
        es.shutdown();
        try { es.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return this;
//            System.out.println("build_name: " + i);
//            int max = 0;
//            for (int j = 1; j != name[i].length; ++j)
//                max = Math.max(name[i][j][1] - (name[i][j - 1][1] + name[i][j - 1][2]), max);
//            max = Math.max(name[i][0][1], max);
//            max = Math.max(pseudo[i].length - (name[i][name[i].length - 1][1] + name[i][name[i].length - 1][2]), max);
//            System.out.printf("%9d\n", max);
    }

    private int[][][] get_name()
    {
        return name;
    }

    private class NameBuilder implements Runnable
    {
        private final int curr_sqc;

        NameBuilder(int index)
        {
            curr_sqc = index;
        }

        @Override
        public void run()
        {
//            System.out.println("hello " + curr_index);
            name[curr_sqc] =
                    sequences[curr_sqc] == centre ?
                    new int[][]{ { 0, 0, centre.length } } :
                    st.align_with(sequences[curr_sqc]);
//            System.out.println("bye " + curr_index);
        }
    }

}
