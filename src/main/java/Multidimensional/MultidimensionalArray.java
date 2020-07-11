package Multidimensional;

import java.util.Arrays;

@SuppressWarnings("unused")
public class MultidimensionalArray
{

    private final int dimension; // 维度
    private final int[] size; // 每个维度的长度
    private int[] array;
    private int[] capacity; // 每个维度容纳下游维度的点数
    private int total_capacity;

    // 初始化数组, 所有元素被赋值为value
    public MultidimensionalArray(int[] size, int value)
    {
        this(size);
        fill_with(value);
    }

    public MultidimensionalArray(int[] size)
    {
        check_size();
        dimension = size.length;
        this.size = size;
        total_capacity = 1;
        build_array();
        build_capacity();
    }

    private void check_size()
    {
        for (int i = 0; i != size.length; ++i)
        {
            if (size[i] < 0)
            {
                throw new NegativeArraySizeException();
            }
        }
    }

    private void build_array()
    {
        for (int i : size)
        {
            total_capacity *= i;
        }
        array = new int[total_capacity];
    }

    private void build_capacity()
    {
        capacity = new int[dimension];
        capacity[dimension - 1] = 1;
        for (int i = dimension - 1; i > 0; --i)
        {
            capacity[i - 1] = capacity[i] * size[i];
        }
    }

    public void set_element(int[] index, int val)
    {
        check_index(index);
        array[from(index)] = val;
    }

    public int get_element(int[] index)
    {
        check_index(index);
        return array[from(index)];
    }

    public int get_dimension()
    {
        return dimension;
    }

    @Override
    public String toString()
    {
        var sb = new StringBuilder();
        for (int i = 0; i != total_capacity; ++i)
        {
            int[] index = to(i);
            sb.append(to_string(index));
            sb.append(" = ").append(array[i]).append('\n');
        }
        return sb.toString();
    }

    public void check_index(int[] index)
    {
        if (index.length != dimension)
            throw new IllegalArgumentException("coordinate dimension error");
        for (int i = 0; i < dimension; ++i)
            if (index[i] >= size[i] || index[i] < 0)
                throw new ArrayIndexOutOfBoundsException("index " + to_string(index) + " out of bounds for " + to_string(size));
    }

    private int from(int[] logic_index)
    {
        int ret = 0;
        for (int i = 0; i < dimension; ++i)
        {
            ret += logic_index[i] * capacity[i];
        }
        return ret;
    }

    private int[] to(int real_index)
    {
        int[] ret = new int[dimension];
        for (int i = 0; i < dimension; ++i)
        {
            ret[i] = real_index / capacity[i];
            real_index %= capacity[i];
        }
        return ret;
    }

    public void fill_with(int val)
    {
        Arrays.fill(array, val);
    }

    /**
     * 打印多维坐标, {a, b, c, d}会打印出[a][b][c][d]
     * @param index 坐标
     */
    private static String to_string(int[] index)
    {
        var sb = new StringBuilder();
        for (int i : index)
        {
            sb.append('[').append(i).append(']');
        }
        return sb.toString();
    }

}
