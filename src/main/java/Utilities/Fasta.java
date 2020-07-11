package Utilities;

import java.io.*;
import java.util.ArrayList;

/**
 * 读取和存储和输出fasta格式的文件
 * 该类将删除序列中的空白字符\s
 */
@SuppressWarnings("unused")
public final class Fasta
{

    private String[] sequences;
    private String[] sequence_identifiers; // 序列名中包含'>'

    /**
     * 输入要求:
     * 每个序列名称必须由以>为开头的完整一行组成
     * 从第一个>行开始, 每一个序列都由夹在两个>行中间的部分或最后一个>行之后的部分组成
     **/
    public Fasta(String infile)
    {
        var sequences = new ArrayList<String>();
        var sequence_identifiers = new ArrayList<String>();
        try (var br = new BufferedReader(new FileReader(infile)))
        {
            var sb = new StringBuilder();
            String line;
            while (br.ready())
            { // 找到第一个序列名的位置
                line = br.readLine();
                if (line.length() > 0 && line.charAt(0) == '>')
                { // 找到了
                    sequence_identifiers.add(line);
                    break;
                }
            }
            while (br.ready())
            { // 读入每个序列
                line = br.readLine();
                if (line.length() > 0 && line.charAt(0) == '>')
                {
                    sequence_identifiers.add(line);
                    sequences.add(sb.toString().replaceAll("\\s+", ""));
                    sb = new StringBuilder();
                }
                else
                {
                    sb.append(line);
                }
            }
            sequences.add(sb.toString().replaceAll("\\s+", ""));
            this.sequences = sequences.toArray(new String[0]);
            this.sequence_identifiers = sequence_identifiers.toArray(new String[0]);
        }
        catch (FileNotFoundException e)
        {
            System.err.println("cannot open file " + infile);
            System.exit(1);
        }
        catch (IOException io_exception)
        {
            System.err.println("cannot access file " + infile);
            System.exit(1);
        }
    }

    public Fasta(String[] sequences, String[] sequence_identifiers)
    {
        if (sequences == null) throw new IllegalArgumentException();
//        for (int i = 0; i != sequences.length; ++i) sequences[i] = sequences[i].replaceAll("\\s+", "");
        for (int i = 0; i != sequences.length; ++i) // TODO
            if (sequences[i] != null)
                sequences[i] = sequences[i].replaceAll("\\s+", "");
        this.sequences = sequences;
        this.sequence_identifiers = sequence_identifiers;
    }

    public final String get_sequence_identifiers(int index) { return sequence_identifiers[index]; }

    public final String[] get_sequence_identifiers() { return sequence_identifiers; }

    public final String get_sequence(int index) { return sequences[index]; }

    public final String[] get_sequences() { return sequences; }

    /**
     * 输出文件
     * @param outfile 输出文件名
     * @param identifiers_retained 是否输出序列标识
     */
    public void output(String outfile, boolean identifiers_retained)
    {
        try (var bw = new BufferedWriter(new FileWriter(outfile)))
        {
            for (int i = 0; i != sequences.length; ++i)
            {
                if (sequences[i] != null) // TODO 这里待改
                {
                    if (identifiers_retained && sequence_identifiers != null)
                    {
                        bw.write(sequence_identifiers[i]);
                        bw.newLine();
                    }
                    bw.write(sequences[i]);
                    if (i != sequences.length - 1) bw.newLine();
                    bw.flush();
                }
            }
        }
        catch (IOException e)
        {
            System.err.println("cannot write file " + outfile);
            System.exit(1);
        }
    }

    // TODO
    public void print(boolean identifiers_retained) {}

    // TODO
    public void print_sequence_statistics_info() {}

}