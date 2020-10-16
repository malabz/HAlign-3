package SuffixTreeAlignment;

import Pairwise.PairwiseAligner;
import Utilities.Pair;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static Main.GlobalVariables.thread;

public class ConcurrentSequencePairwiseAligner
{
    private final ExecutorService es = Executors.newFixedThreadPool(thread);

    private final byte[][] sequences;
    private final int[][][] anti_name;
    private final int[][] centre_spaces;
    private final int[][] others_spaces;
    private final int centre_index;

    static Pair<int[][], int[][]> pairwise_align(byte[][] sequences, int centre_index, int[][][] anti_name)
    {
        return new ConcurrentSequencePairwiseAligner(sequences, centre_index, anti_name).align().get_spaces();
    }

    private ConcurrentSequencePairwiseAligner(byte[][] sequences, int centre_index, int[][][] anti_name)
    {
        this.sequences = sequences;
        this.anti_name = anti_name;
        this.centre_index = centre_index;
        centre_spaces = new int[sequences.length][sequences[centre_index].length + 1];
        others_spaces = new int[sequences.length][];
    }

    private ConcurrentSequencePairwiseAligner align()
    {
        for (int i = 0; i != sequences.length; ++i) es.submit(new SequencePairwiseAligner(i));
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
        private final int curr_index;

        SequencePairwiseAligner(int index)
        {
            curr_index = index;
        }

        @Override
        public void run()
        {
            others_spaces[curr_index] = new int[sequences[curr_index].length + 1];
            for (int j = 0; j != anti_name[curr_index].length; ++j)
                PairwiseAligner.local_align(sequences[centre_index], centre_spaces[curr_index], anti_name[curr_index][j][0], anti_name[curr_index][j][1],
                        sequences[curr_index], others_spaces[curr_index], anti_name[curr_index][j][2], anti_name[curr_index][j][3]);
        }
    }

}

