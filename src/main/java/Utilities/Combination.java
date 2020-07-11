package Utilities;

/**
 * 这个类用来返回从m中选出n个的所有组合
 */
@SuppressWarnings("unused")
public class Combination
{

    private final int m, n; // 从m中取n个
    private int[][] re;
    private int[] cur;
    private int finished;

    public Combination(int m, int n)
    {
        this.m = m;
        this.n = n;
    }

    public int[][] get_re()
    {
        generate_combination();
        return re;
    }

    private void generate_combination()
    {
        re = new int[UtilityFunctions.combination_number(m, n)][n];
        cur = new int[n];
        finished = 0;
        generate_combination(0);
    }

    private void generate_combination(int deep)
    {
        if (deep == n)
        {
            System.arraycopy(cur, 0, re[finished++], 0, n);
        }
        else
        {
            for (int i = deep == 0 ? 0 : cur[deep - 1] + 1, tmp = m - n + deep + 1; i < tmp; ++i)
            {
                cur[deep] = i;
                generate_combination(deep + 1);
            }
        }
    }

    public void print()
    {
        for (int[] i : re)
        {
            for (int j : i) System.out.printf("%d ", j);
            System.out.println();
        }
    }

}
