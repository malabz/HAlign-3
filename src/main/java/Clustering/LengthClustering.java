package Clustering;

import Utilities.Fasta;
import Utilities.UtilityFunctions;

import java.util.ArrayList;
import java.util.TreeSet;

public class LengthClustering
{

    public static int[][] cluster(int[] lengths)
    {
        return new LengthClustering().do_cluster(lengths);
    }

    private int[][] do_cluster(int[] lengths)
    {
        var length_set = new TreeSet<Integer>();
        for (int i : lengths) length_set.add(i);
        var ordered_lengths = new ArrayList<>(length_set);

        var distances = new ArrayList<Integer>(length_set.size() - 1);
        for (int i = 1; i != ordered_lengths.size(); ++i)
            distances.add(ordered_lengths.get(i) - ordered_lengths.get(i - 1));
//        System.out.println(distances.toString());
        double distances_standard_deviation = UtilityFunctions.standard_deviation_of(distances);
        double distances_average = UtilityFunctions.average_of(distances);

        final double THRESHOLD = distances_average + distances_standard_deviation;
        var checkpoints = new ArrayList<Integer>();
        for (int i = 0; i != distances.size(); ++i)
            if (distances.get(i) > THRESHOLD)
//                System.out.println(distances.get(i));
                checkpoints.add(ordered_lengths.get(i));
        checkpoints.add(Integer.MAX_VALUE);

//        System.out.println("threshold: " + THRESHOLD);
//        System.out.println("checkpoints: " + checkpoints.toString());
//        System.out.println();

//        refine(lengths, checkpoints);
//        System.out.println("checkpoints: " + checkpoints.toString());
//        System.out.println();

        var groups = new ArrayList<ArrayList<Integer>>(checkpoints.size());
        for (int i = 0; i != checkpoints.size(); ++i) groups.add(new ArrayList<>());
        for (int i = 0; i != lengths.length; ++i)
        {
            for (int j = 0; j != checkpoints.size(); ++j)
            {
                if (lengths[i] <= checkpoints.get(j))
                {
                    groups.get(j).add(i);
                    break;
                }
            }
        }

        var ret = new int[groups.size()][];
        for (int i = 0; i != groups.size(); ++i) ret[i] = UtilityFunctions.to_array(groups.get(i));

        return ret;
    }

    public static void main(String[] args)
    {
        var sequences = new Fasta("data_set\\SARS-CoV-2_20200417_deoutlier.fasta").get_sequences();
        var lengths = new int[sequences.length];
        for (int i = 0; i != lengths.length; ++i) lengths[i] = sequences[i].length();
        var result = cluster(lengths);
        int count = 0;
        for (int i = 0; i != result.length; ++i)
        {
            System.out.println("group[" + i + "]: ");
            int max_length, min_length;
            max_length = min_length = result[i].length > 0 ? lengths[result[i][0]] : -1;
            for (int j = 0; j < result[i].length; ++j)
            {
                if (lengths[result[i][j]] > max_length) max_length = lengths[result[i][j]];
                else if (lengths[result[i][j]] < min_length) min_length = lengths[result[i][j]];
                ++count;
            }
            System.out.println("max length: " + max_length);
            System.out.println("min length: " + min_length);
            System.out.println();
        }
        assert count == sequences.length;
    }

}
