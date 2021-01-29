package SuffixTreeAlignment;

import Pairwise.PairwiseAligner;
import Utilities.Pair;
import Utilities.Pseudo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static Main.GlobalVariables.thread;

public class ConcurrentSequencePairwiseAligner
{
    private final ExecutorService es = Executors.newFixedThreadPool(thread);

    private final byte[][] sequences;
    private final byte[] centre;
    private final int[][][] anti_name;
    private final int[][] centre_spaces;
    private final int[][] others_spaces;

    static Pair<int[][], int[][]> pairwise_align(byte[][] sequences, byte[] centre, int[][][] anti_name)
    {
        return new ConcurrentSequencePairwiseAligner(sequences, centre, anti_name).align().get_spaces();
    }

    private ConcurrentSequencePairwiseAligner(byte[][] sequences, byte[] centre, int[][][] anti_name)
    {
        this.sequences = sequences;
        this.anti_name = anti_name;
        this.centre = centre;
        centre_spaces = new int[sequences.length][centre.length + 1];
        others_spaces = new int[sequences.length][];
    }

    private ConcurrentSequencePairwiseAligner align()
    {
        for (int i = 0; i != sequences.length; ++i)
            es.submit(new SequencePairwiseAligner(i));
        es.shutdown();
        try { es.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return this;
    }

    private Pair<int[][], int[][]> get_spaces()
    {
        return new Pair<>(centre_spaces, others_spaces);
    }

    private class SequencePairwiseAligner implements Runnable
    {
        private final int curr_sqc;

        SequencePairwiseAligner(int index)
        {
            curr_sqc = index;
        }

        @Override
        public void run()
        {
            others_spaces[curr_sqc] = new int[sequences[curr_sqc].length + 1];
            for (int j = 0; j != anti_name[curr_sqc].length; ++j)
                PairwiseAligner.local_align(centre, centre_spaces[curr_sqc], anti_name[curr_sqc][j][0], anti_name[curr_sqc][j][1],
                        sequences[curr_sqc], others_spaces[curr_sqc], anti_name[curr_sqc][j][2], anti_name[curr_sqc][j][3]);
        }
    }

}

