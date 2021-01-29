package SuffixTreeAlignment;

import Utilities.UtilityFunctions;
import static Main.GlobalVariables.*;

import java.util.*;

// todo: 0长度会报错
class SuffixTree
{

    private static final int  THRESHOLD = 15;
    private static final int  GLOBAL_THRESHOLD = 15;
    private static final byte WIDTH = CHAR_KIND;    // 树宽

    private final byte[] word;                      // 包含的字
    private final Node   root;                      // 根结点

    // 活动点
    private Node active_node;                       // 活动结点
    private Byte active_edge;                       // 用字符代表的活动边
    private int  active_length;                     // 活动长度, 只有在活动边非null时有效

    // 每趟循环临时变量
    private int  curr_end;                          // 待显式插入后缀的区间, 注意这里所代表的区间形式为(begin_index, end_index)
    private Node pre;                               // 最后创建的节点, 后缀链接时使用

    SuffixTree(byte[] input)
    {
        int remainder;  // 原本为类成员, 待插入后缀树数量
        byte curr_char; // 原本为类成员, 当前待插入后缀的最后一个字符
        word = new byte[input.length + 1];
        System.arraycopy(input, 0, word, 0, input.length);
        word[input.length] = GAP; // 相当于 '$'
        root = new Node(-1, 0, 0, false);
        active_node = root;
        active_edge = null;
        active_length = remainder = 0;
        for (curr_end = 1; curr_end != word.length + 1; ++curr_end)
        { // 每次向后处理一个字符
            pre = null;
            ++remainder;
            curr_char = word[curr_end - 1];
            for (int i = remainder; i != 0; --i)
            {
                if (active_length == 0)
                { // 活动长度为0
                    if (active_node.children[curr_char] != null)
                    { // 可隐式插入
                        active_edge = curr_char;
                        active_length = 1;
                        link(active_node);
                        try_to_walk_down_by_one_step();
                        break;
                    }
                    else
                    { // 不可隐式插入
                        int new_edge_length = word.length - curr_end + 1;
                        active_node.children[curr_char] = new Node(curr_end - 1, new_edge_length,
                                active_node.length_from_root + new_edge_length, true);
                        if (active_node != root)
                        { // 活动结点为root时无需擦屁股
                            link(active_node);
                            active_node = active_node.suffix == null ? root : active_node.suffix;
                        }
                        --remainder;
                    }
                }
                else if (word[active_node.children[active_edge].begin + active_length] == curr_char)
                { // 活动长度非0且可隐式插入
                    ++active_length;
                    try_to_walk_down_by_one_step();
                    break;
                }
                else
                { // 活动长度非0且不可隐式插入
                    active_node.split();
                    int new_edge_length = word.length - curr_end + 1;
                    active_node.children[active_edge].children[curr_char] = new Node(curr_end - 1, new_edge_length,
                            active_node.children[active_edge].length_from_root + new_edge_length, true);
                    link(active_node.children[active_edge]);
                    if (active_node == root) // 活动结点为根结点
                        active_edge = --active_length == 0 ? null : word[curr_end - remainder + 1];
                    else // 活动结点非根结点
                        active_node = active_node.suffix == null ? root : active_node.suffix;
                    try_to_walk_down();
                    --remainder;
                }
            }
        }
    }

    // 要求活动边非null
    private void try_to_walk_down_by_one_step()
    {
        if (active_length == active_node.children[active_edge].length)
        {
            active_node = active_node.children[active_edge];
            active_edge = null;
            active_length = 0;
        }
    }

    private void try_to_walk_down()
    {
        while (active_edge != null && active_length >= active_node.children[active_edge].length)
        {
            active_length -= active_node.children[active_edge].length;
            active_node = active_node.children[active_edge];
            active_edge = active_length == 0 ? null : word[curr_end - 1 - active_length];
        }
    }

    private void link(Node next)
    {
        if (pre != null) pre.suffix = next;
        pre = next;
    }

//    private int node_count; // DEBUG

    private class Node
    {

        Node[] children;
        Node suffix;
        int begin, length, length_from_root;
//        int number; // DEBUG

        Node(int begin, int length, int length_from_root, boolean is_leaf)
        {
//            number = node_count++; // DEBUG
            this.begin = begin;
            this.length = length;
            this.length_from_root = length_from_root;
            children = is_leaf ? null : new Node[WIDTH];
        }

        // 在活动点所指向位置的后面分裂活动边
        void split()
        {
            var next = children[active_edge];
            var new_node = new Node(next.begin, active_length, length_from_root + active_length, false);
            new_node.children[word[next.begin + active_length]] = next;
            next.begin += active_length;
            next.length -= active_length;
            children[active_edge] = new_node;
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
//            sb.append("number: ").append(number).append('\n');
            for (int i = begin; i != begin + length; ++i) sb.append(word[i]);
            sb.append('\n').append("children: ");
//            if (children == null) sb.append("null");
//            else for (byte i = 0; i != CHAR_KIND; ++i) if (children[i] != null) sb.append(i).append(": ").append(children[i].number).append("; ");
            return sb.append('\n').toString();
        }

    }

    public static void main(String[] args)
    {
//        new SuffixTree(new byte[]{1, 1, 1});
//        new SuffixTree(new byte[]{1, 4, 3, 2, 4, 4, 1, 4, 3}).print_all_suffix();
//        new SuffixTree(new byte[]{1, 3, 3, 1, 3, 3, 3}).print_all_suffix();
//        var st = new SuffixTree(new byte[]{1, 2, 1, 4, 3, 1, 3, 1, 2, 2, 4, 3, 4, 1, 4, 3, 1, 3, 3, 3, 4, 1, 4, 4, 4, 1, 1, 3, 3, 1, 3, 4, 3, 1, 3, 2, 2, 2, 1, 2, 3, 4, 3, 4, 3, 3, 1, 4, 2, 3, 1, 4, 4, 4, 2, 2, 4, 1, 4, 4, 4, 4, 3, 2, 4, 3, 4, 2, 2, 2, 2, 2, 2, 4, 2, 4, 2, 3, 1, 3, 2, 3, 2, 1, 4, 1, 2, 3, 1, 4, 4, 2, 3, 2, 1, 2, 1, 3, 2, 3, 4, 2, 2, 1, 2, 3, 3, 2, 2, 1, 2, 3, 1, 3, 3, 3, 4, 1, 4, 2, 4, 3, 2, 3, 1, 2, 4, 1, 4, 3, 4, 2, 4, 3, 4, 4, 4, 2, 1, 4, 4, 3, 3, 4, 2, 3, 3, 4, 3, 1, 4, 3, 3, 4, 1, 4, 4, 1, 4, 4, 4, 1, 4, 3, 2, 3, 1, 3, 3, 4, 1, 3, 2, 4, 4, 3, 1, 1, 4, 1, 4, 4, 1, 3, 1, 2, 2, 3, 2, 1, 1, 3, 1, 1, 4, 1, 4, 4, 4, 1, 3, 3, 1, 1, 1, 2, 4, 2, 4, 1, 4, 4, 1, 1, 4, 4, 1, 1, 4, 4, 1, 1, 4, 2, 3, 4, 4, 2, 4, 1, 2, 2, 1, 3, 1, 4, 1, 1, 4, 1, 1, 4, 1, 1, 3, 1, 1, 4, 4, 2, 1, 1, 4, 2, 4, 3, 4, 2, 3, 1, 3, 1, 2, 3, 3, 2, 3, 4, 4, 4, 3, 3, 1, 3, 1, 3, 1, 2, 1, 3, 1, 4, 3, 1, 4, 1, 1, 3, 1, 1, 1, 1, 1, 1, 4, 4, 4, 3, 3, 1, 3, 3, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 4, 3, 3, 3, 3, 3, 3, 2, 3, 4, 4, 3, 4, 2, 2, 3, 3, 1, 3, 1, 2, 3, 1, 3, 4, 4, 1, 1, 1, 3, 1, 3, 1, 4, 3, 4, 3, 4, 2, 3, 3, 1, 1, 1, 3, 3, 3, 3, 1, 1, 1, 1, 1, 3, 1, 1, 1, 2, 1, 1, 3, 3, 3, 4, 1, 1, 3, 1, 3, 3, 1, 2, 3, 3, 4, 1, 1, 3, 3, 1, 2, 1, 4, 4, 4, 3, 1, 1, 1, 4, 4, 4, 4, 1, 4, 3, 4, 4, 4, 4, 2, 2, 3, 2, 2, 4, 1, 4, 2, 3, 1, 3, 4, 4, 4, 4, 1, 1, 3, 1, 2, 4, 3, 1, 3, 3, 3, 3, 3, 3, 1, 1, 3, 4, 1, 1, 3, 1, 3, 1, 4, 4, 1, 4, 4, 4, 4, 3, 3, 3, 3, 4, 3, 3, 3, 1, 3, 4, 3, 3, 3, 1, 4, 1, 3, 4, 1, 3, 4, 1, 1, 4, 3, 4, 3, 1, 4, 3, 1, 1, 3, 1, 3, 1, 1, 3, 3, 3, 3, 3, 2, 3, 3, 3, 1, 4, 3, 3, 4, 1, 3, 3, 3, 1, 2, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 3, 2, 3, 4, 2, 3, 4, 1, 1, 3, 3, 3, 3, 1, 4, 1, 3, 3, 3, 3, 2, 1, 1, 3, 3, 1, 1, 3, 3, 1, 1, 1, 3, 3, 3, 3, 1, 1, 1, 2, 1, 3, 1, 3, 3, 3, 3, 3, 3, 1, 3, 1, 2, 4, 4, 4, 1, 4, 2, 4, 1, 2, 3, 4, 4, 1, 3, 3, 4, 3, 3, 4, 3, 1, 1, 1, 2, 3, 1, 1, 4, 1, 3, 1, 3, 4, 2, 1, 1, 1, 1, 4, 2, 4, 4, 4, 1, 2, 1, 3, 2, 2, 2, 3, 4, 3, 1, 3, 1, 4, 3, 1, 3, 3, 3, 3, 1, 4, 1, 1, 1, 3, 1, 1, 1, 4, 1, 2, 2, 4, 4, 4, 2, 2, 4, 3, 3, 4, 1, 2, 3, 3, 4, 4, 4, 3, 4, 1, 4, 4, 1, 2, 3, 4, 3, 4, 4, 1, 2, 4, 1, 1, 2, 1, 4, 4, 1, 3, 1, 3, 1, 4, 2, 3, 1, 1, 2, 3, 1, 4, 3, 3, 3, 3, 2, 4, 4, 3, 3, 1, 2, 4, 2, 1, 2, 4, 4, 3, 1, 3, 3, 3, 4, 3, 4, 1, 1, 1, 4, 4, 3, 1, 3, 3, 1, 3, 2, 1, 4, 3, 1, 1, 1, 1, 2, 2, 2, 1, 3, 1, 1, 2, 3, 1, 4, 3, 1, 1, 2, 3, 1, 3, 2, 3, 1, 2, 3, 1, 1, 4, 2, 3, 1, 2, 3, 4, 3, 1, 1, 1, 1, 3, 2, 3, 4, 4, 1, 2, 3, 3, 4, 1, 2, 3, 3, 1, 3, 1, 3, 3, 3, 3, 3, 1, 3, 2, 2, 2, 1, 1, 1, 3, 1, 2, 3, 1, 2, 4, 2, 1, 4, 4, 1, 1, 3, 3, 4, 4, 4, 1, 2, 3, 1, 1, 4, 1, 1, 1, 3, 2, 1, 1, 1, 2, 4, 4, 4, 1, 1, 3, 4, 1, 1, 2, 3, 4, 1, 4, 1, 3, 4, 1, 1, 3, 3, 3, 3, 1, 2, 2, 2, 4, 4, 2, 2, 4, 3, 1, 1, 4, 4, 4, 3, 2, 4, 2, 3, 3, 1, 2, 3, 3, 1, 3, 3, 2, 3, 2, 2, 4, 3, 1, 3, 1, 3, 2, 1, 4, 4, 1, 1, 3, 3, 3, 1, 1, 2, 4, 3, 1, 1, 4, 1, 2, 1, 1, 2, 3, 3, 2, 2, 3, 2, 4, 1, 1, 1, 2, 1, 2, 4, 2, 4, 4, 4, 4, 1, 2, 1, 4, 3, 1, 3, 3, 3, 3, 3, 4, 3, 3, 3, 3, 1, 1, 4, 1, 1, 1, 2, 3, 4, 1, 1, 1, 1, 3, 4, 3, 1, 3, 3, 4, 2, 1, 2, 4, 4, 2, 4, 1, 1, 1, 1, 1, 1, 3, 4, 3, 3, 1, 2, 4, 4, 2, 1, 3, 1, 3, 1, 1, 1, 1, 4, 1, 2, 1, 3, 4});
//        st.align_with(new byte[]{1, 2, 1, 4, 3, 1, 3, 1, 2, 2, 4, 3, 1, 4, 3, 1, 3, 3, 3, 4, 1, 4, 4, 4, 1, 1, 3, 3, 1, 3, 4, 3, 1, 3, 2, 2, 2, 1, 2, 3, 4, 3, 4, 3, 3, 1, 4, 2, 3, 2, 4, 4, 4, 2, 2, 4, 1, 4, 4, 4, 4, 3, 2, 4, 3, 4, 2, 2, 2, 2, 2, 2, 4, 2, 4, 2, 3, 1, 3, 2, 3, 2, 1, 4, 1, 2, 3, 1, 4, 4, 2, 3, 2, 1, 2, 1, 3, 1, 2, 3, 4, 2, 2, 1, 2, 3, 3, 2, 2, 1, 2, 3, 1, 3, 3, 3, 4, 1, 4, 2, 4, 3, 2, 3, 1, 2, 4, 1, 4, 3, 4, 2, 4, 3, 4, 4, 4, 2, 1, 4, 4, 3, 3, 4, 2, 3, 3, 4, 3, 1, 4, 3, 3, 4, 1, 4, 4, 1, 4, 4, 4, 1, 4, 3, 2, 3, 1, 3, 3, 4, 1, 3, 2, 4, 4, 1, 1, 4, 1, 4, 4, 1, 3, 1, 2, 2, 3, 2, 1, 1, 3, 1, 1, 4, 1, 4, 4, 4, 1, 3, 3, 1, 1, 1, 2, 4, 2, 4, 1, 4, 4, 1, 1, 4, 4, 1, 1, 4, 4, 1, 1, 4, 2, 3, 4, 4, 2, 4, 1, 2, 2, 1, 3, 1, 4, 1, 1, 4, 1, 1, 4, 1, 1, 3, 1, 1, 4, 4, 2, 1, 1, 4, 2, 4, 3, 4, 2, 3, 1, 3, 1, 3, 3, 3, 2, 3, 4, 4, 4, 3, 3, 1, 3, 1, 3, 1, 2, 1, 3, 1, 4, 3, 1, 4, 1, 1, 3, 1, 1, 1, 1, 1, 1, 4, 4, 4, 3, 3, 1, 3, 3, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 4, 3, 3, 3, 3, 3, 3, 2, 3, 4, 4, 3, 4, 2, 2, 3, 3, 1, 3, 1, 2, 3, 1, 3, 4, 4, 1, 1, 1, 3, 1, 3, 1, 4, 3, 4, 3, 4, 2, 3, 3, 1, 1, 1, 3, 3, 3, 3, 1, 1, 1, 1, 1, 3, 1, 1, 1, 2, 1, 1, 3, 3, 3, 4, 1, 1, 3, 1, 3, 3, 1, 2, 3, 3, 4, 1, 1, 3, 3, 1, 2, 1, 4, 4, 4, 3, 1, 1, 1, 4, 4, 4, 4, 1, 4, 3, 4, 4, 4, 4, 2, 2, 3, 2, 2, 4, 1, 4, 2, 3, 1, 3, 4, 4, 4, 4, 1, 1, 3, 1, 2, 4, 3, 1, 3, 3, 3, 3, 3, 3, 1, 1, 3, 4, 1, 1, 3, 1, 3, 1, 4, 4, 1, 4, 4, 4, 4, 3, 3, 3, 3, 4, 3, 3, 3, 1, 3, 4, 3, 3, 3, 1, 4, 1, 3, 1, 3, 4, 1, 1, 4, 3, 4, 3, 1, 4, 3, 1, 1, 3, 1, 3, 1, 1, 3, 3, 3, 3, 3, 2, 3, 3, 3, 1, 4, 3, 3, 4, 1, 3, 3, 3, 1, 2, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 3, 2, 3, 4, 2, 3, 4, 1, 1, 3, 3, 3, 3, 1, 4, 1, 3, 3, 3, 3, 2, 1, 1, 3, 3, 1, 1, 3, 3, 1, 1, 1, 3, 3, 3, 3, 1, 1, 1, 2, 1, 3, 1, 3, 3, 3, 3, 3, 3, 1, 3, 1, 2, 4, 4, 4, 1, 4, 2, 4, 1, 2, 3, 4, 4, 1, 3, 3, 4, 3, 3, 4, 3, 1, 1, 1, 2, 3, 1, 1, 4, 1, 3, 1, 3, 4, 2, 1, 1, 1, 1, 4, 2, 4, 4, 4, 1, 2, 1, 3, 2, 2, 2, 3, 4, 3, 1, 3, 1, 4, 3, 1, 3, 3, 3, 3, 1, 4, 1, 1, 1, 3, 1, 1, 1, 4, 1, 2, 2, 4, 4, 4, 2, 2, 4, 3, 3, 4, 1, 2, 3, 3, 4, 4, 4, 3, 4, 1, 4, 4, 1, 2, 3, 4, 3, 1, 4, 4, 1, 2, 4, 1, 1, 2, 1, 4, 4, 1, 3, 1, 3, 1, 4, 2, 3, 1, 1, 2, 3, 1, 4, 3, 3, 3, 3, 2, 4, 4, 3, 3, 1, 2, 4, 2, 1, 2, 4, 4, 3, 1, 3, 3, 3, 4, 3, 4, 1, 1, 1, 4, 4, 3, 1, 3, 3, 1, 3, 2, 1, 4, 3, 1, 1, 1, 1, 2, 2, 2, 1, 3, 1, 1, 2, 3, 1, 4, 3, 1, 1, 2, 3, 1, 3, 2, 3, 1, 2, 3, 1, 1, 4, 2, 3, 1, 2, 3, 4, 3, 1, 1, 1, 1, 3, 2, 3, 4, 4, 1, 2, 3, 3, 4, 1, 2, 3, 3, 1, 3, 1, 3, 3, 3, 3, 1, 3, 2, 2, 2, 1, 1, 1, 3, 1, 2, 3, 1, 2, 4, 2, 1, 4, 4, 1, 1, 3, 3, 4, 4, 4, 1, 2, 3, 1, 1, 4, 1, 1, 1, 3, 2, 1, 1, 1, 2, 4, 4, 4, 1, 1, 3, 4, 1, 1, 2, 3, 4, 1, 4, 1, 3, 4, 1, 1, 3, 3, 3, 3, 1, 2, 2, 2, 4, 4, 2, 2, 4, 3, 1, 1, 4, 4, 4, 3, 2, 4, 2, 3, 3, 1, 2, 3, 3, 1, 3, 3, 2, 3, 2, 2, 1, 3, 1, 3, 1, 3, 2, 1, 4, 4, 1, 1, 3, 3, 3, 1, 1, 2, 4, 3, 1, 1, 4, 1, 2, 1, 1, 2, 3, 3, 2, 2, 3, 2, 4, 1, 1, 1, 2, 1, 2, 4, 2, 4, 4, 4, 4, 1, 2, 1, 4, 3, 1, 3, 3, 3, 3, 3, 4, 3, 3, 3, 3, 1, 1, 4, 1, 1, 1, 2, 3, 4, 1, 1, 1, 1, 3, 4, 3, 1, 3, 3, 4, 2, 1, 2, 4, 4, 2, 4, 1, 1, 1, 1, 1, 1, 3, 4, 3, 3, 2, 2, 4, 4, 2, 1, 3, 1, 3, 1, 1, 1, 1, 4, 1, 2, 1, 3, 1});
        final int bgn = 1 << 4, end = 1 << 16;
        double mx = 0, mn = 1, tt = 0;
        for (int i = bgn; i < end; i += 8)
        {
            var curr_rsl = check_suffix_tree(generate(i));
            double curr = (double)curr_rsl[0] / curr_rsl[1];
            tt += curr;
            if (mx < curr) mx = curr;
            else if (mn > curr) mn = curr;
        }
        System.out.println("avr = " + (tt / (end - bgn) * 8));
        System.out.println("max = " + mx);
        System.out.println("min = " + mn);
    }

    int[][] align_with(byte[] rhs)
    {
        // int last_dis = - 1;
        var al = new ArrayList<Integer>();
        int lhs_index = 0, rhs_index = 0;
        int rhs_last_end = 0; // lhs_last_end数值上等于lhs_index
        while (rhs_index < rhs.length)
        {
            var curr_result = search_prefix(rhs, rhs_index, THRESHOLD);
            if (curr_result == null)
            {
                ++rhs_index; // 没找到, 从下个字符开始找
            }
            else // 找到了
            {
                int min_dis = Integer.MAX_VALUE;
                for (int i = 1; i != curr_result.size(); ++i)
                    if (curr_result.get(i) > lhs_index &&               // 在上一次匹配的右边
                            curr_result.get(i) - lhs_index < min_dis)   // 离得更近
                        min_dis = curr_result.get(i) - lhs_index;

                if (min_dis == Integer.MAX_VALUE)
                { // 都不符合要求, 继续找
                    ++rhs_index;
                }
                else // 找到了, 需要检查该匹配是否错位
                {
                    if (curr_result.get(0) - Math.abs(min_dis - (rhs_index - rhs_last_end)) / 3 > THRESHOLD) // 错位检查通过
                    {
                        lhs_index += min_dis;
                        assert Arrays.equals(word, lhs_index, lhs_index + curr_result.get(0), rhs, rhs_index, rhs_index + curr_result.get(0));
                        al.add(lhs_index);
                        al.add(rhs_index);
                        al.add(curr_result.get(0));
                        lhs_index += curr_result.get(0);
                        rhs_index += curr_result.get(0);
                        rhs_last_end = rhs_index;
                    }
                    else
                    {
                        ++rhs_index;
                    }
                }
            }
        }

//        if (al.get(al.size() - 1) < THRESHOLD * 4) // 去掉结尾位置的同质区段对
//        {
//            al.remove(al.size() - 1);
//            al.remove(al.size() - 1);
//            al.remove(al.size() - 1);
//        }

        return UtilityFunctions.to_2d_array(al, 3);
    }

    int[][] get_identical_subsequence_pairs(byte[] rhs)
    {
        var al = new ArrayList<Integer>();
        al.add(-1);
        al.add(-1);
        al.add(0);

        int rhs_index = 0;
//        var hsal = new ArrayList<HashSet<Integer>>(word.length + 1);
        var hsal = new ArrayList<HashSet<Integer>>(Collections.nCopies(word.length + 1, null));
        while (rhs_index < rhs.length)
        {
            var curr_result = search_prefix(rhs, rhs_index, GLOBAL_THRESHOLD);

            if (curr_result == null)
            {
                ++rhs_index; // 没找到, 从下个字符继续找
            }
            else // 找到了
            {
                final int curr_len = curr_result.get(0);
                final int rhs_bgn = rhs_index;
                for (int i = 1; i != curr_result.size(); ++i)
                {
                    final int lhs_bgn = curr_result.get(i);
                    final int lhs_end = lhs_bgn + curr_len;
                    final int rhs_end = rhs_bgn + curr_len;
                    if (hsal.get(lhs_end) == null || !hsal.get(lhs_end).contains(rhs_end))
                    {
                        al.add(curr_result.get(i));
                        al.add(rhs_index);
                        al.add(curr_result.get(0));

                        if (hsal.get(lhs_end) == null) hsal.set(lhs_end, new HashSet<>());
                        hsal.get(lhs_end).add(rhs_end);
                    }
                }
                rhs_index += curr_result.get(0) - GLOBAL_THRESHOLD + 1;
//                rhs_index += GLOBAL_THRESHOLD;
//                rhs_index += 1;
            }
        }

        al.add(word.length - 1);
        al.add(rhs.length);
        al.add(1);
        return UtilityFunctions.to_2d_array(al, 3);
    }

    // 返回word中所有和rhs.substr(rhs_index)具有相同前缀的子串长度和起始位置
    private ArrayList<Integer> search_prefix(byte[] rhs, int rhs_index, int threshold)
    {
        int common_prefix_length = 0;
        for (Node last_node = root, curr_node = last_node.children[rhs[rhs_index]]; ; last_node = curr_node, curr_node = curr_node.children[rhs[rhs_index]])
        {
            if (curr_node == null)
                return common_prefix_length < threshold ? null : get_all_beginning_with(last_node, common_prefix_length);
            for (int lhs_index = curr_node.begin, lhs_end = curr_node.begin + curr_node.length; lhs_index != lhs_end && rhs_index != rhs.length; ++lhs_index, ++rhs_index, ++common_prefix_length)
                if (word[lhs_index] != rhs[rhs_index])
                    return common_prefix_length < threshold ? null : get_all_beginning_with(curr_node, common_prefix_length);
            if (curr_node.children == null || rhs_index == rhs.length)
                return common_prefix_length < threshold ? null : get_all_beginning_with(curr_node, common_prefix_length);
        }
    }

    // 首元素为前缀长度，然后把root下所有叶结点的起始位置提取出来
    private ArrayList<Integer> get_all_beginning_with(Node curr_root, int to_add)
    {
        var ret = new ArrayList<Integer>();
        ret.add(to_add);
        get_all_beginning_with(curr_root, ret);
        return ret;
    }

    private void get_all_beginning_with(Node curr_root, List<Integer> lst)
    {
        if (curr_root.children == null)
            lst.add(word.length - curr_root.length_from_root);
        else
            for (int i = 0; i != WIDTH; ++i)
                if (curr_root.children[i] != null)
                    get_all_beginning_with(curr_root.children[i], lst);
    }

    private boolean contains(Node start_root, byte[] rhs, int start_index)
    {
        if (start_root == null) return start_index == rhs.length;
        var curr_path = start_root.children[rhs[start_index]];
        int rhs_length = rhs.length - start_index;
        return curr_path != null && rhs_length >= curr_path.length &&
                Arrays.equals(word, curr_path.begin, curr_path.begin + curr_path.length, rhs, start_index, start_index + curr_path.length) &&
                contains(curr_path, rhs, start_index + curr_path.length);
    }

    private void check()
    {
        for (int i = 0; i != word.length; ++i) assert contains(root, word, i);
    }

    private SuffixTree print_all_suffix()
    {
        var stack = new LinkedList<Node>();
        var suffixes = new TreeSet<>(Comparator.comparingInt(String::length));
        get_all_suffixes(root, stack, suffixes);

        var check = new boolean[word.length];
        suffixes.forEach(i -> check[i.length() - 1] = true);
        for (var i : check) assert i;

        for (int i = 0; i != word.length; ++i)
        {
            var sb = new StringBuilder();
            for (int j = i; j != word.length; ++j) sb.append(word[j]);
            assert suffixes.contains(sb.toString());
        }

        suffixes.forEach(System.out::println);

        return this;
    }

    private void get_all_suffixes(Node root, LinkedList<Node> stack, TreeSet<String> suffixes)
    {
        if (root.children == null)
        {
            assert !suffixes.contains(get_stack_content(stack));
            suffixes.add(get_stack_content(stack));
        }
        else
        {
            for (var i : root.children)
                if (i != null)
                {
                    stack.add(i);
                    get_all_suffixes(i, stack, suffixes);
                    stack.remove(stack.size() - 1);
                }
        }
    }

    private String get_stack_content(List<Node> lst)
    {
        if (lst == null) return "";
        var sb = new StringBuilder();
        for (var i : lst) for (int j = i.begin; j != i.begin + i.length; ++j) sb.append(word[j]);
        return sb.toString();
    }

    private SuffixTree level_print()
    {
        var queue = new LinkedList<Node>();
        queue.add(root);
        while (queue.size() != 0)
        {
            var curr = queue.removeFirst();
            System.out.println(curr.toString());
            if (curr.children != null) for (var i : curr.children) if (i != null) queue.add(i);
        }
        return this;
    }

    static private SuffixTree generate(int len)
    {
        var word = new byte[len];
        new Random(System.currentTimeMillis()).nextBytes(word);
        for (int i = 0; i != len; ++i) word[i] = (byte)(Math.abs(word[i] % 5) + 1);
        return new SuffixTree(word);
    }

    static private int[] check_suffix_tree(SuffixTree st)
    {
        return check_suffix_tree(st.root);
//        System.out.printf("%7d%7d%7d\n", statistics[0], statistics[1], statistics[2]);
    }

    static private int[] check_suffix_tree(Node root)
    {
        var result = new int[3];

        if (root.children == null)
            ++result[2];
        else
            for (int i = 0; i != root.children.length; ++i)
                if (root.children[i] == null)
                {
                    ++result[1];
                }
                else
                {
                    ++result[0];
                    var curr = check_suffix_tree(root.children[i]);
                    for (int j = 0; j != curr.length; ++j) result[j] += curr[j];
                }

        return result;
    }

}
