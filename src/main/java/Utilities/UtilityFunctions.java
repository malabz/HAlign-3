package Utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.text.SimpleDateFormat;

@SuppressWarnings("unused")
public class UtilityFunctions
{

    public static int count(boolean... arr)
    {
        int cnt = 0;
        for (var i : arr) if (i) ++cnt;
        return cnt;
    }

    public static void print_time(String msg, long start)
    {
        System.out.println(msg + (System.currentTimeMillis() - start));
    }

    /**
     * 返回int数组之和
     */
    public static int sum_of(int[] arr)
    {
        int sum = 0;
        for (int i : arr) sum += i;
        return sum;
    }

    public static int edict_distance_of(String lhs, String rhs)
    {
        int ll = lhs.length(), rl = rhs.length();
        int[][] dp = new int[2][ll + 1];
        for (int i = 0; i <= ll; ++i) dp[0][i] = i;

        for (int i = 0; i < rl; ++i)
        {
            int cur_line = (i + 1) % 2;
            dp[cur_line][0] = i + 1;
            for (int j = 1; j <= ll; ++j)
                dp[cur_line][j] = lhs.charAt(j - 1) == rhs.charAt(i) ? dp[(cur_line + 1) % 2][j - 1] :
                        Math.min(dp[(cur_line + 1) % 2][j - 1], Math.min(dp[cur_line][j - 1], dp[(cur_line + 1) % 2][j])) + 1;
        }
        return dp[rl % 2][ll];
    }

    /**
     * 返回字符串形式的当前日期, yyyy-MM-dd HH:mm:ss
     */
    public static String now()
    {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     * @param arr 数组
     * @param k 排位, 从0计数
     * @return 第k小的元素索引
     */
    public static int index_of_kth_smallest(int[] arr, int k)
    {
        int l = 0, r = arr.length;
        int[] arr_copy = new int[r];
        System.arraycopy(arr, 0, arr_copy, 0, r);
        int index_axis = UtilityFunctions.partition(arr_copy, l, r);
        while (index_axis != k)
        {
            if (index_axis > k) r = index_axis;
            else l = index_axis + 1;
            index_axis = partition(arr_copy, l, r);
        }
        return arr_copy[index_axis];
    }

    /**
     * 快排partition部分
     * @param l 待处理部分最左边的元素的索引
     * @param r 待处理部分之后一个元素的索引
     * @return 轴元素索引
     */
    private static int partition(int[] array, int l, int r)
    {
        int axis = array[l], j = l + 1;
        for (int i = l; i != r; ++i)
        {
            if (array[i] < axis)
            {
                int tmp = array[j];
                array[j++] = array[i];
                array[i] = tmp;
            }
        }
        array[l] = array[--j];
        array[j] = axis;
        return j;
    }

    public static double standard_deviation_of(Collection<? extends Number> arr)
    {
        double sum = 0, square_sum = 0;
        for (Number i : arr)
        {
            sum += i.doubleValue();
            square_sum += Math.pow(i.doubleValue(), 2);
        }
        return Math.sqrt(square_sum / arr.size() - Math.pow(sum / arr.size(), 2));
    }

    public static double standard_deviation_of(int... arr)
    {
        long sum = 0, square_sum = 0;
        for (var i : arr)
        {
            sum += i;
            square_sum += Math.pow(i, 2);
        }
        return Math.sqrt((double)square_sum / arr.length - Math.pow((double)sum / arr.length, 2));
    }

    public static double average_of(Collection<? extends Number> arr)
    {
        double sum = 0;
        for (Number integer : arr) sum += integer.doubleValue();
        return sum / arr.size();
    }

    public static double average_of(int... arr)
    {
        long sum = 0;
        for (var i : arr) sum += i;
        return (double)sum / arr.length;
    }

    private static long[] to_long_array(int[] int_array)
    {
        var ret = new long[int_array.length];
        for (int i = 0; i != int_array.length; ++i) ret[i] = int_array[i];
        return ret;
    }

    /**
     * 从m中选n个的组合数
     */
    public static int combination_number(int m, int n)
    {
        int re = 1;
        if (n > m / 2) n = m - n;
        for (int i = 0; i < n; ++i) re = re * (m - i) / (i + 1);
        return re;
    }

    /**
     * 将二维数组填充为val
     */
    public static void set_val(int[][] target, int val)
    {
        for (int i = 0; i != target.length; ++i) Arrays.fill(target[i], val);
    }

    /**
     * 返回多个整数的最大值
     */
    public static int max(int... args)
    {
        int max = Integer.MIN_VALUE;
        for (int i : args)
            if (i > max)
                max = i;
        return max;
    }

    /**
     * 返回多个整数的最小值
     */
    public static int min(int... args)
    {
        int min = Integer.MAX_VALUE;
        for (int i : args)
            if (i < min)
                min = i;
        return min;
    }

    /**
     * 返回多个双精度浮点数的最大值
     */
    public static double max(double... args)
    {
        var max = Double.MIN_VALUE;
        for (double i : args)
            if (i > max)
                max = i;
        return max;
    }

    public static int index_of_max(int[] array)
    {
        if (array.length == 0) return -1;

        int ret = 0;
        for (int i = 1; i != array.length; ++i)
            if (array[ret] < array[i]) ret = i;

        return ret;
    }

    /**
     * Iterable到int[][num_per_pair]
     * 不检查参数合法性
     */
    public static int[][] to_2d_array(List<Integer> al, int num_per_pair)
    {
        int pair_num = al.size() / num_per_pair;
        int[][] arr = new int[pair_num][num_per_pair];
        for (int j = 0, k = 0; j != pair_num; ++j)
            for (int l = 0; l != num_per_pair; ++l)
                arr[j][l] = al.get(k++);
        return arr;
    }

    /**
     * 将List<Integer>转换为int[]
     */
    public static int[] to_array(List<Integer> al)
    {
        var ret = new int[al.size()];
        for (int i = 0; i != al.size(); ++i) ret[i] = al.get(i);
        return ret;
    }

    public static int[] reverse(int[] arr)
    {
        for (int left = 0; left != arr.length / 2; ++left)
        {
            var tmp = arr[left];
            int right = arr.length - left - 1;
            arr[left] = arr[right];
            arr[right] = tmp;
        }
        return arr;
    }

    public static void output_pseudo_sequences(byte[][] sequences, String outfile)
    {
        try (var bw = new BufferedWriter(new FileWriter(outfile)))
        {
            for (int j = 0; j != sequences.length; ++j)
            {
                bw.write(Pseudo.pseudo_to_string(sequences[j]));
                if (j != sequences.length - 1) bw.newLine();
                bw.flush();
            }
        }
        catch (IOException e)
        {
            System.err.println("cannot write file " + outfile);
            System.exit(1);
        }
    }

}