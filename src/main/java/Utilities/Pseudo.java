package Utilities;

import java.util.ArrayList;
import java.util.Collection;

import static Main.GlobalVariables.*;

@SuppressWarnings("unused")
public interface Pseudo
{

    /**
     * byte列表到数组的转换
     */
    static byte[] to_array(Collection<Byte> al)
    {
        var ret = new byte[al.size()];
        for (var i : al) ret[i] = i;
        return ret;
    }

    /**
     * 返回序列的字符串版
     */
    static String pseudo2string(byte[] pseudo)
    {
        return pseudo2string(pseudo, 0, pseudo.length);
    }

    /**
     * 返回序列中一段的字符串
     */
    static String pseudo2string(byte[] pseudo_sequence, int from, int to)
    {
        var sb = new StringBuilder(pseudo_sequence.length);
        for (int i = from; i != to; ++i) sb.append(decode(pseudo_sequence[i]));
        return sb.toString();
    }

    /**
     * 返回序列的字符串版
     */
    static byte[] string2pseudo(String sequence)
    {
        var ret = new byte[sequence.length()];
        for (int i = 0; i != sequence.length(); ++i) ret[i] = code(sequence.charAt(i));
        return ret;
    }

    /**
     * 字符编码
     */
    static byte code(char c)
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
    static char decode(byte i)
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
    static int edict_distance(byte[] lhs, byte[] rhs)
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
    static int edict_distance(byte[] lhs, int lhs_from, int lhs_to, byte[] rhs, int rhs_from, int rhs_to)
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

    static byte[] reverse_range(byte[] src, int from, int to)
    {
        var ret = new byte[to - from];
        for (int left = from, i = 0; i != ret.length; ++left, ++i) ret[i] = src[left];
        return ret;
    }

    /**
     * 从序列中截取一段并返回去空格版
     */
    static byte[] remove_spaces(byte[] src, int from, int to)
    {
        var al = new ArrayList<Byte>(to - from);
        for (int i = from; i != to; ++i) if (src[i] != GAP) al.add(src[i]);
        return Pseudo.to_array(al);
    }

    /**
     * 返回序列的去空格版
     */
    static byte[] remove_spaces(byte[] src)
    {
        return remove_spaces(src, 0, src.length);
    }

    /**
     * 未计算好长度时返回插入空格后的伪序列
     */
    static byte[] insert_spaces(byte[] src, int[] spaces)
    {
        int result_length = src.length;
        for (int i = 0; i <= src.length; ++i) result_length += spaces[i];
        return insert_spaces(src, spaces, result_length);
    }

    /**
     * 计算好长度时返回插入空格后的伪序列
     */
    static byte[] insert_spaces(byte[] src, int[] spaces, int result_length)
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

}