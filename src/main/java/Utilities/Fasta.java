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

    private Fasta() {}

    // TODO: 输出进度
    /**
     * 输入要求:
     * 每个序列名称必须由以>为开头的完整一行组成
     * 从第一个>行开始, 每一个序列都由夹在两个>行中间的部分或最后一个>行之后的部分组成
     **/
    public Fasta(String infile)
    {
        System.out.printf("reading %s... ", infile);
        System.out.flush();
        var start_time = System.currentTimeMillis();
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
                    sequences.add(remove_white_spaces(sb.toString()));
                    sb = new StringBuilder();
                }
                else
                {
                    sb.append(line);
                }
            }
            sequences.add(remove_white_spaces(sb.toString()));
            this.sequences = sequences.toArray(new String[0]);
            this.sequence_identifiers = sequence_identifiers.toArray(new String[0]);
        }
        catch (FileNotFoundException e)
        {
            System.err.println("fatal error: cannot open file " + infile);
            System.exit(1);
        }
        catch (IOException io_exception)
        {
            System.err.println("fatal error: cannot access file " + infile);
            System.exit(1);
        }
        System.out.printf("finished in %d ms\n", System.currentTimeMillis() - start_time);
    }

    public Fasta(String[] sequences, String[] sequence_identifiers)
    {
        for (int i = 0; i != sequences.length; ++i) sequences[i] = remove_white_spaces(sequences[i]);
        this.sequences = sequences;
        this.sequence_identifiers = sequence_identifiers;
    }

    private static String remove_white_spaces(String str)
    {
        return str.replaceAll("\\s+", "");
    }

    public String get_sequence_identifier(int index) { return sequence_identifiers[index].substring(1); }

    public String[] get_sequence_identifier() { return sequence_identifiers; }

    public String get_sequence(int index) { return sequences[index]; }

    public String[] get_sequences() { return sequences; }

    public int get_sequence_number()
    {
        return sequences.length;
    }

    /**
     * 输出文件
     * @param outfile 输出文件名
     * @param output_identifiers 是否输出序列标识
     */
    public void output(String outfile, boolean output_identifiers)
    {
        System.out.printf("writing to %s... ", outfile);
        System.out.flush();
        var start_time = System.currentTimeMillis();
        try (var bw = new BufferedWriter(new FileWriter(outfile)))
        {
            for (int i = 0; i != sequences.length; ++i)
            {
                if (output_identifiers)
                {
                    bw.write(">");

                    if (sequence_identifiers != null && sequence_identifiers[i] != null)
                        bw.write(sequence_identifiers[i]);
                    else
                        bw.write(i);

                    bw.newLine();
                }

                bw.write(sequences[i]);
                if (i != sequences.length - 1) bw.newLine();
                bw.flush();
            }
        }
        catch (IOException e)
        {
            System.err.println("fatal error: cannot write file " + outfile);
            System.exit(1);
        }
        System.out.printf("finished in %d ms\n", System.currentTimeMillis() - start_time);
    }

    /**
     * 在标准输出打印所有序列
     * @param output_identifiers 是否输出序列标识
     */
    public void print(boolean output_identifiers)
    {
        for (int i = 0; i != sequences.length; ++i)
        {
            if (output_identifiers) System.out.println(sequence_identifiers[i]);
            System.out.println(sequences[i]);
        }
    }

}