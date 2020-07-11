package Clustering;

import Utilities.UtilityFunctions;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class Temporary
{

    private static final int THRESHOLD = 10000;

    private final int[] lengths;

    public Temporary(byte[][] pseudo)
    {
        lengths = new int[pseudo.length];
        for (int i = 0; i != pseudo.length; ++i)
            lengths[i] = pseudo[i].length;
    }

    public int[][] cluster()
    {
        var al = new ArrayList<ArrayList<Integer>>(2);
        al.add(new ArrayList<>());
        al.add(new ArrayList<>());

        for (int i = 0; i != lengths.length; ++i)
            al.get(lengths[i] > THRESHOLD ? 0 : 1).add(i);

        int[][] ret = new int[2][];
        ret[0] = UtilityFunctions.to_array(al.get(0));
        ret[1] = UtilityFunctions.to_array(al.get(1));

        return ret;
    }

}
