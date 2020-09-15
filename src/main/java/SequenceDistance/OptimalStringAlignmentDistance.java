package SequenceDistance;

import Utilities.Pseudo;
import Utilities.UtilityFunctions;

public class OptimalStringAlignmentDistance
{

    public static int get_distance(byte[] lhs, byte[] rhs)
    {
        int[][] dp = new int[lhs.length + 1][rhs.length + 1];

        for (int i = 0; i <= lhs.length; ++i) dp[i][0] = i;
        for (int j = 0; j <= rhs.length; ++j) dp[0][j] = j;

        for (int i = 1; i <= lhs.length; ++i)
            dp[i][1] = UtilityFunctions.min(dp[i - 1][1] + 1, dp[i][0] + 1,
                    dp[i - 1][0] + distance(lhs[i - 1], rhs[0]));

        for (int j = 1; j <= rhs.length; ++j)
            dp[1][j] = UtilityFunctions.min(dp[0][j] + 1, dp[1][j - 1] + 1,
                    dp[0][j - 1] + distance(lhs[0], rhs[j - 1]));

        for (int i = 2; i <= lhs.length; ++i)
            for (int j = 2; j <= rhs.length; ++j)
            {
                dp[i][j] = UtilityFunctions.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1,
                        dp[i - 1][j - 1] + distance(lhs[i - 1], rhs[j - 1]));
                if (lhs[i - 2] == rhs[j - 1] && lhs[i - 1] == rhs[j - 2])
                {
                    int if_transposition = dp[i - 2][j - 2] + 1;
                    if (if_transposition < dp[i][j])
                        dp[i][j] = if_transposition;
                }
            }

        return dp[lhs.length][rhs.length];
    }

    private static int distance(byte lhs, byte rhs)
    {
        return lhs == rhs ? 0 : 1;
    }

    public static void main(String[] args)
    {
        String lhs, rhs;
        System.out.println((lhs = "ca") + ' ' + (rhs = "agc") + ": " +
                OptimalStringAlignmentDistance.get_distance(Pseudo.string2pseudo(lhs), Pseudo.string2pseudo(rhs)));
        System.out.println((lhs = "ga") + ' ' + (rhs = "agc") + ": " +
                OptimalStringAlignmentDistance.get_distance(Pseudo.string2pseudo(lhs), Pseudo.string2pseudo(rhs)));
        System.out.println((lhs = "at") + ' ' + (rhs = "gc") + ": " +
                OptimalStringAlignmentDistance.get_distance(Pseudo.string2pseudo(lhs), Pseudo.string2pseudo(rhs)));
    }

}
