package Utilities;

import java.util.ArrayList;

import static Main.GlobalVariables.*;

// @SuppressWarnings("unused")
public class Pseudo
{

    public static String[] pseudo_to_string(byte[][] pseudo)
    {
        var result = new String[pseudo.length];

        for (int i = 0; i != pseudo.length; ++i)
            result[i] = pseudo_to_string(pseudo[i]);
        return result;
    }

    /**
     * 返回序列的字符串版
     */
    public static String pseudo_to_string(byte[] pseudo)
    {
        return pseudo_to_string(pseudo, 0, pseudo.length);
    }

    /**
     * 返回序列中一段的字符串
     */
    public static String pseudo_to_string(byte[] pseudo_sequence, int from, int to)
    {
        var sb = new StringBuilder(pseudo_sequence.length);

        for (int i = from; i != to; ++i) sb.append(decode(pseudo_sequence[i]));
        return sb.toString();
    }

    /**
     * 返回序列的字符串版
     */
    public static byte[] string_to_pseudo(String sequence)
    {
        var result = new byte[sequence.length()];

        for (int i = 0; i != sequence.length(); ++i) result[i] = code(sequence.charAt(i));
        return result;
    }

    public static byte[][] string_to_pseudo(String[] sequences)
    {
        var result = new byte[sequences.length][];

        for (int i = 0; i != sequences.length; ++i)
            result[i] = string_to_pseudo(sequences[i]);
        return result;
    }

    /**
     * 字符编码
     */
    public static byte code(char c)
    {
        switch (c)
        {
            case 'A':
            case 'a':
                return A;
            case 'G':
            case 'g':
                return G;
            case 'C':
            case 'c':
                return C;
            case 'U':
            case 'u':
            case 'T':
            case 't':
                return T;
            default:
                return UNKNOWN;
        }
    }

    /**
     * 字符解码
     */
    public static char decode(byte i)
    {
        switch (i)
        {
            case 0: return '-';
            case 1: return 'a';
            case 2: return 'g';
            case 3: return 'c';
            case 4: return 't';
            default: return 'n';
        }
    }

    /**
     * 返回两个序列的编辑距离
     */
    public static int edict_distance(byte[] lhs, byte[] rhs)
    {
//        int[][] dp = new int[2][lhs.length + 1];
//        for (int i = 0; i <= lhs.length; ++i) {
//            dp[0][i] = i;
//        }
//        for (int i = 0; i < rhs.length; ++i) {
//            int cur_line = (i + 1) % 2;
//            dp[cur_line][0] = i + 1;
//            for (int j = 1; j <= lhs.length; ++j) {
//                dp[cur_line][j] = lhs[j - 1] == rhs[i] ? dp[(cur_line + 1) % 2][j - 1] :
//                        Math.min(dp[(cur_line + 1) % 2][j - 1], Math.min(dp[cur_line][j - 1], dp[(cur_line + 1) % 2][j])) + 1;
//            }
//        }
//        return dp[rhs.length % 2][lhs.length];
        return edict_distance(lhs, 0, lhs.length, rhs, 0, rhs.length);
    }

    /**
     * 返回两个序列各自子序列的编辑距离
     */
    public static int edict_distance(byte[] lhs, int lhs_from, int lhs_to, byte[] rhs, int rhs_from, int rhs_to)
    {
        int[][] dp = new int[2][lhs_to - lhs_from + 1];
        int lhs_length = lhs_to - lhs_from, rhs_length = rhs_to - rhs_from;
        for (int i = 0; i <= lhs_length; ++i) dp[0][i] = i;
        for (int i = 0; i != rhs_length; ++i)
        {
            int cur_line = (i + 1) % 2;
            dp[cur_line][0] = i + 1;
            for (int j = 1; j <= lhs_length; ++j)
                dp[cur_line][j] = lhs[lhs_from + j - 1] == rhs[rhs_from + i] ? dp[(cur_line + 1) % 2][j - 1] : Math.min(dp[(cur_line + 1) % 2][j - 1], Math.min(dp[cur_line][j - 1], dp[(cur_line + 1) % 2][j])) + 1;
        }
        return dp[rhs_length % 2][lhs_length];
    }

    public static byte[] reverse(byte[] src, int from, int to)
    {
        var result = new byte[to - from];

        for (int i = 0, j = to - 1; i != result.length; )
            result[i++] = src[j--];
        return result;
    }

    public static byte[] reverse(byte[] src)
    {
        return reverse(src, 0, src.length);
    }

    /**
     * 从序列中截取一段并返回去空格版
     */
    public static byte[] remove_spaces(byte[] src, int from, int to)
    {
        int len = to - from;
        for (int i = from; i != to; ++i) if (src[i] == GAP) --len;

        var result = new byte[len];
        for (int i = from, j = 0; i != to; ++i) if (src[i] != GAP) result[j++] = src[i];
        return result;
    }

    /**
     * 返回序列的去空格版
     */
    public static byte[] remove_spaces(byte[] src)
    {
        return remove_spaces(src, 0, src.length);
    }

    /**
     * 未计算好长度时返回插入空格后的伪序列
     */
    public static byte[] insert_spaces(byte[] src, int[] spaces)
    {
        int result_length = src.length;
        for (int i = 0; i <= src.length; ++i) result_length += spaces[i];
        return insert_spaces(src, spaces, result_length);
    }

    public static byte[][] insert_spaces(byte[][] src, int[][] spaces)
    {
        var result = new byte[src.length][];
        for (int i = 0; i != src.length; ++i)
            result[i] = insert_spaces(src[i], spaces[i]);
        return result;
    }

    /**
     * 计算好长度时返回插入空格后的伪序列
     */
    public static byte[] insert_spaces(byte[] src, int[] spaces, int result_length)
    {
//        assert src.length + 1 == spaces.length;
//        int tmp = src.length;
//        for (int i = 0; i != spaces.length; ++i) tmp += spaces[i];
//        assert tmp == result_length;
        var ret = new byte[result_length];
        int index = 0;
        for (int i = 0; i != src.length; ++i)
        {
            for (int j = 0; j != spaces[i]; ++j) ret[index++] = GAP;
            ret[index++] = src[i];
        }
        for (int j = 0; j != spaces[src.length]; ++j) ret[index++] = GAP;
        return ret;
    }

    public static ArrayList<Byte> to_al(byte[] src)
    {
        var result = new ArrayList<Byte>();
        result.ensureCapacity(src.length);

        for (int i = 0; i != src.length; ++i) result.add(src[i]);
        return result;
    }

    public static String merge(byte[] pseudo, String origin)
    {
        var sb = new StringBuilder(pseudo.length);
        for (int i = 0, origin_index = 0; i != pseudo.length; ++i)
            sb.append(pseudo[i] == GAP ? '-' : origin.charAt(origin_index++));
        return sb.toString();
    }

}
