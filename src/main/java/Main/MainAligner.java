package Main;

import static Main.GlobalVariables.*;

public class MainAligner
{
    static byte[][] align(byte[][] sequences)
    {
        return new MainAligner().do_align(sequences);
    }

    private MainAligner() {}

    private byte[][] do_align(byte[][] sequences)
    {
        first_iteration = true;
        var result = SuffixTreeAlignment.SuffixTreeAligner.align(sequences, idx_centre);

//        first_iteration = false;
//        var next_centre = extract(result);
//        result = SuffixTreeAlignment.SuffixTreeAligner.align(sequences, next_centre);

//        if (realign_ending) result = Pairwise.PoolAligner.align(result);

        return result;
    }

    private static byte[] extract(byte[][] alignments)
    {
        centre_pool = new int[alignments[0].length][CHAR_KIND];
        var centre = new byte[alignments[0].length];
        for (int j = 0; j != alignments[0].length; ++j)
        {
            var statistics = new int[CHAR_KIND];
            for (int i = 0; i != alignments.length; ++i) ++statistics[alignments[i][j]];
            byte curr = GAP;
            for (byte i = A; i != CHAR_KIND; ++i)
                if (statistics[i] > statistics[curr])
                    curr = i;
//            final int non_space = statistics[A] + statistics[G] + statistics[C] + statistics[T] + statistics[UNKNOWN];
//            if (statistics[curr] < non_space / 16 * 15) curr = UNKNOWN;
            centre[j] = curr == GAP ? UNKNOWN : curr;
            centre_pool[j] = statistics;
        }
        return centre;
    }

}
