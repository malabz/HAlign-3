# Real datasets 

## Human mitochondrial genomes

<a href="http://lab.malab.cn/%7Etfr/HAlign3_testdata/mt_genome.tar.xz" download="mt_genome.tar.xz">mt_genome.tar.xz</a> is composed of 672 sequences with the longest length of 16,578 bp and the shortest length of 16,556 bp, which is highly similar to each other  (>99%). 
```bash
#download
wget http://lab.malab.cn/%7Etfr/HAlign3_testdata/mt_genome.tar.xz

#uncompress
tar -Jxf mt_genome.tar.xz
```

## Respiratory syndrome coronavirus 2 genomes

Respiratory syndrome coronavirus 2 (SARS‑CoV‑2) is an RNA virus, which causes coronavirus disease 2019  and is responsible for the COVID-19 pandemic. Its sequences are all recorded in the form of DNA for the convenience of sequencing and recording. SARS-CoV-2 genomes in high quality (without ambiguous nucleotides) were download from GISAID website (https://www.gisaid.org, updated on November 11, 2021). 

<a href="http://lab.malab.cn/%7Etfr/HAlign3_testdata/sars_cov_2_1Mseq.tar.xz" download="sars_cov_2_1Mseq.tar.xz">sars_cov_2_1Mseq.tar.xz</a> is 1 million SARS-CoV-2 sequences (21111 to 29891bp), which have >99% similarity to the reference genome (GISAID accession ID: EPI_ISL_402124), were selected by CD-HIT (threshold=0.99). The uncompressed file is nearly 29GB, which needs ～500GB RAM to be aligned by HAlign 3. 
```bash
#download
wget http://lab.malab.cn/%7Etfr/HAlign3_testdata/sars_cov_2_1Mseq.tar.xz

#uncompress
tar -Jxf sars_cov_2_1Mseq.tar.xz

#run halign (the index of reference genome is 0)
halign -Xmx512g -c 0 -t 1 -o sars_cov_2_1Mseq.fasta.aln sars_cov_2_1Mseq.fasta   #conda version
java -Xmx512g -jar HAlign-3.0.0_rc1.jar -c 0 -t 1 -o sars_cov_2_1Mseq.fasta.aln sars_cov_2_1Mseq.fasta   #released package
```


<a href="http://lab.malab.cn/%7Etfr/HAlign3_testdata/sars_cov_2_500seq.tar.xz" download="sars_cov_2_500seq.tar.xz">sars_cov_2_500seq.tar.xz</a> is the first 500 sequences (29283 to 29891bp) from the dataset above. 500 SARS-CoV-2 genomes are less similar to each other (>97%).



## Mycobacterium 23S rRNA sequences

<a href="http://lab.malab.cn/%7Etfr/HAlign3_testdata/23s_rRNA.tar.xz" download="23s_rRNA.tar.xz">23s_rRNA.tar.xz</a> is composed of 641 Mycobacterium 23S rRNA sequences in the range from 1909 to 3485 bp, downloaded from the SLIVA rRNA database (http://www.arb-silva.de/) of bacteria, archaea and eukarya. Sequences from this dataset are more diverse to each other (>30%).



# Hierarchical-tree simulated datasets


Two sets of SARS-CoV-2-like and 2 sets of mitochondrial-like genome datasets were simulated by INDELible v1.03 (Fletcher and Yang 2009). The substitution models in the simulation were based on the estimated models of 500 SARS‑CoV‑2 and 672 mitochondrial genome alignments, respectively, by IQ-TREE v2.2.0-beta (Minh et al. 2020). One hundred randomly picked SARS‑CoV‑2 and mitochondrial genomes from the datasets above were aligned to build the tree for simulation. 

For the simulations of datasets with different mean similarities, every branch in the tree was a random length between 0 and 1 (NON-ULTRAMETRIC); then tree length (sum of branch lengths) was tuned to meet the mean similarities of 99%, 98%, 97%, 96%, 95%, 94%, 93%, 92%, 91%, 90%, 85%, 80%, 75% and 70%. The mean similarity of a dataset is the mean of the similarities between any two sequences in the dataset (length_similarity_distribution.py), which is the percentage of matched characters in their pairwise alignment performed by aligner MAFFT. 

For the simulations of datasets with different scales of tree length, every branch in the tree is the same as the tree of 100 SARS‑CoV‑2 and mitochondrial genomes; then, different scales of tree length (0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0, 7.5, 10.0, 12.5 and 15.0) were set for simulation. The simulation sequence length is 30kb and 16kb for the SARS-CoV-2-like and mitochondrial-like genome. The indel model parameter is LAV 5 50 with the rates of 0.01 and 0.1 for insertion and deletion. 



Download the datasets: 

<a href="http://lab.malab.cn/%7Etfr/HAlign3_testdata/sars_cov_2_like_diff_similarity.tar.xz" download="sars_cov_2_like_diff_similarity.tar.xz">sars_cov_2_like_diff_similarity.tar.xz</a>

<a href="http://lab.malab.cn/%7Etfr/HAlign3_testdata/sars_cov_2_like_diff_treelength.tar.xz" download="sars_cov_2_like_diff_treelength.tar.xz">sars_cov_2_like_diff_treelength.tar.xz</a>

<a href="http://lab.malab.cn/%7Etfr/HAlign3_testdata/mt_like_diff_similarity.tar.xz" download="mt_like_diff_similarity.tar.xz">mt_like_diff_similarity.tar.xz</a>

<a href="http://lab.malab.cn/%7Etfr/HAlign3_testdata/mt_like_diff_treelength.tar.xz" download="mt_like_diff_treelength.tar.xz">mt_like_diff_treelength.tar.xz</a>



Each dataset contains 9 replicates with 100 simulated sequences each. Mean length difference is the mean difference between the reference alignment length and the average length of simulated sequences. The tree length, the difference between alignment length and the average of sequence length and the mean similarity of the 100 randomly picked SARS‑CoV‑2 and mitochondrial genomes are 0.028, 111, 0.996 and 0.048, 55, 0.998. The detailed information of those 4 sets of 14 datasets is summarized in the following table. 

<html xmlns:v="urn:schemas-microsoft-com:vml" xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40">
 <head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <meta name="ProgId" content="Excel.Sheet"/>
  <meta name="Generator" content="WPS Office ET"/>
  <!--[if gte mso 9]>
   <xml>
    <o:DocumentProperties>
     <o:Author>furongtang</o:Author>
     <o:Created>2022-06-12T16:41:44</o:Created>
     <o:LastSaved>2022-06-12T16:48:41</o:LastSaved>
    </o:DocumentProperties>
    <o:CustomDocumentProperties>
     <o:KSOProductBuildVer dt:dt="string">2052-3.9.1.6204</o:KSOProductBuildVer>
    </o:CustomDocumentProperties>
   </xml>
  <![endif]-->
  <style>
<!-- @page
	{margin:0.98in 0.75in 0.98in 0.75in;
	mso-header-margin:0.51in;
	mso-footer-margin:0.51in;}
tr
	{mso-height-source:auto;
	mso-ruby-visibility:none;}
col
	{mso-width-source:auto;
	mso-ruby-visibility:none;}
br
	{mso-data-placement:same-cell;}
.font0
	{color:#000000;
	font-size:12.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:"宋体";
	mso-generic-font-family:auto;
	mso-font-charset:134;}
.font1
	{color:#000000;
	font-size:9.0pt;
	font-weight:700;
	font-style:normal;
	text-decoration:none;
	font-family:"宋体";
	mso-generic-font-family:auto;
	mso-font-charset:134;}
.font2
	{color:#000000;
	font-size:8.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:"宋体";
	mso-generic-font-family:auto;
	mso-font-charset:134;}
.font3
	{color:#000000;
	font-size:7.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:"宋体";
	mso-generic-font-family:auto;
	mso-font-charset:134;}
.font4
	{color:#000000;
	font-size:7.0pt;
	font-weight:700;
	font-style:normal;
	text-decoration:none;
	font-family:"宋体";
	mso-generic-font-family:auto;
	mso-font-charset:134;}
.font5
	{color:#000000;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:"宋体";
	mso-generic-font-family:auto;
	mso-font-charset:0;}
.font6
	{color:#FFFFFF;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:"宋体";
	mso-generic-font-family:auto;
	mso-font-charset:0;}
.font7
	{color:#44546A;
	font-size:13.0pt;
	font-weight:700;
	font-style:normal;
	text-decoration:none;
	font-family:"宋体";
	mso-generic-font-family:auto;
	mso-font-charset:134;}
.font8
	{color:#FF0000;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:"宋体";
	mso-generic-font-family:auto;
	mso-font-charset:0;}
.font9
	{color:#0000FF;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:underline;
	text-underline-style:single;
	font-family:"宋体";
	mso-generic-font-family:auto;
	mso-font-charset:0;}
.font10
	{color:#FA7D00;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:"宋体";
	mso-generic-font-family:auto;
	mso-font-charset:0;}
.font11
	{color:#44546A;
	font-size:11.0pt;
	font-weight:700;
	font-style:normal;
	text-decoration:none;
	font-family:"宋体";
	mso-generic-font-family:auto;
	mso-font-charset:134;}
.font12
	{color:#44546A;
	font-size:15.0pt;
	font-weight:700;
	font-style:normal;
	text-decoration:none;
	font-family:"宋体";
	mso-generic-font-family:auto;
	mso-font-charset:134;}
.font13
	{color:#3F3F3F;
	font-size:11.0pt;
	font-weight:700;
	font-style:normal;
	text-decoration:none;
	font-family:"宋体";
	mso-generic-font-family:auto;
	mso-font-charset:0;}
.font14
	{color:#FFFFFF;
	font-size:11.0pt;
	font-weight:700;
	font-style:normal;
	text-decoration:none;
	font-family:"宋体";
	mso-generic-font-family:auto;
	mso-font-charset:0;}
.font15
	{color:#9C0006;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:"宋体";
	mso-generic-font-family:auto;
	mso-font-charset:0;}
.font16
	{color:#9C6500;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:"宋体";
	mso-generic-font-family:auto;
	mso-font-charset:0;}
.font17
	{color:#FA7D00;
	font-size:11.0pt;
	font-weight:700;
	font-style:normal;
	text-decoration:none;
	font-family:"宋体";
	mso-generic-font-family:auto;
	mso-font-charset:0;}
.font18
	{color:#000000;
	font-size:11.0pt;
	font-weight:700;
	font-style:normal;
	text-decoration:none;
	font-family:"宋体";
	mso-generic-font-family:auto;
	mso-font-charset:0;}
.font19
	{color:#44546A;
	font-size:18.0pt;
	font-weight:700;
	font-style:normal;
	text-decoration:none;
	font-family:"宋体";
	mso-generic-font-family:auto;
	mso-font-charset:134;}
.font20
	{color:#800080;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:underline;
	text-underline-style:single;
	font-family:"宋体";
	mso-generic-font-family:auto;
	mso-font-charset:0;}
.font21
	{color:#006100;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:"宋体";
	mso-generic-font-family:auto;
	mso-font-charset:0;}
.font22
	{color:#7F7F7F;
	font-size:11.0pt;
	font-weight:400;
	font-style:italic;
	text-decoration:none;
	font-family:"宋体";
	mso-generic-font-family:auto;
	mso-font-charset:0;}
.font23
	{color:#3F3F76;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:"宋体";
	mso-generic-font-family:auto;
	mso-font-charset:0;}
.style0
	{mso-number-format:"General";
	text-align:general;
	vertical-align:middle;
	white-space:nowrap;
	mso-rotate:0;
	mso-pattern:auto;
	mso-background-source:auto;
	color:#000000;
	font-size:12.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:134;
	border:none;
	mso-protection:locked visible;
	mso-style-name:"常规";
	mso-style-id:0;}
.style16
	{mso-pattern:auto none;
	background:#A9D08E;
	color:#FFFFFF;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"60% - 强调文字颜色 6";}
.style17
	{mso-pattern:auto none;
	background:#FFF2CC;
	color:#000000;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"20% - 强调文字颜色 4";}
.style18
	{mso-pattern:auto none;
	background:#FFC000;
	color:#FFFFFF;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"强调文字颜色 4";}
.style19
	{mso-pattern:auto none;
	background:#FFCC99;
	color:#3F3F76;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	border:.5pt solid #7F7F7F;
	mso-style-name:"输入";}
.style20
	{mso-pattern:auto none;
	background:#DBDBDB;
	color:#000000;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"40% - 强调文字颜色 3";}
.style21
	{mso-pattern:auto none;
	background:#EDEDED;
	color:#000000;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"20% - 强调文字颜色 3";}
.style22
	{mso-number-format:"_ \0022\00A5\0022* \#\,\#\#0\.00_ \;_ \0022\00A5\0022* \\-\#\,\#\#0\.00_ \;_ \0022\00A5\0022* \0022-\0022??_ \;_ \@_ ";
	mso-style-name:"货币";
	mso-style-id:4;}
.style23
	{mso-pattern:auto none;
	background:#A5A5A5;
	color:#FFFFFF;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"强调文字颜色 3";}
.style24
	{mso-number-format:"0%";
	mso-style-name:"百分比";
	mso-style-id:5;}
.style25
	{mso-pattern:auto none;
	background:#F4B084;
	color:#FFFFFF;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"60% - 强调文字颜色 2";}
.style26
	{mso-pattern:auto none;
	background:#8EA9DB;
	color:#FFFFFF;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"60% - 强调文字颜色 5";}
.style27
	{mso-pattern:auto none;
	background:#ED7D31;
	color:#FFFFFF;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"强调文字颜色 2";}
.style28
	{mso-pattern:auto none;
	background:#9BC2E6;
	color:#FFFFFF;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"60% - 强调文字颜色 1";}
.style29
	{mso-pattern:auto none;
	background:#FFD966;
	color:#FFFFFF;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"60% - 强调文字颜色 4";}
.style30
	{mso-pattern:auto none;
	background:#F2F2F2;
	color:#FA7D00;
	font-size:11.0pt;
	font-weight:700;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	border:.5pt solid #7F7F7F;
	mso-style-name:"计算";}
.style31
	{mso-pattern:auto none;
	background:#5B9BD5;
	color:#FFFFFF;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"强调文字颜色 1";}
.style32
	{mso-pattern:auto none;
	background:#FFEB9C;
	color:#9C6500;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"适中";}
.style33
	{mso-pattern:auto none;
	background:#D9E1F2;
	color:#000000;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"20% - 强调文字颜色 5";}
.style34
	{mso-pattern:auto none;
	background:#C6EFCE;
	color:#006100;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"好";}
.style35
	{mso-pattern:auto none;
	background:#DDEBF7;
	color:#000000;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"20% - 强调文字颜色 1";}
.style36
	{color:#000000;
	font-size:11.0pt;
	font-weight:700;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	border-top:.5pt solid #5B9BD5;
	border-bottom:2.0pt double #5B9BD5;
	mso-style-name:"汇总";}
.style37
	{mso-pattern:auto none;
	background:#FFC7CE;
	color:#9C0006;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"差";}
.style38
	{mso-pattern:auto none;
	background:#A5A5A5;
	color:#FFFFFF;
	font-size:11.0pt;
	font-weight:700;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	border:2.0pt double #3F3F3F;
	mso-style-name:"检查单元格";}
.style39
	{mso-pattern:auto none;
	background:#F2F2F2;
	color:#3F3F3F;
	font-size:11.0pt;
	font-weight:700;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	border:.5pt solid #3F3F3F;
	mso-style-name:"输出";}
.style40
	{color:#44546A;
	font-size:15.0pt;
	font-weight:700;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:134;
	border-bottom:1.0pt solid #5B9BD5;
	mso-style-name:"标题 1";}
.style41
	{color:#7F7F7F;
	font-size:11.0pt;
	font-weight:400;
	font-style:italic;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"解释性文本";}
.style42
	{mso-pattern:auto none;
	background:#FCE4D6;
	color:#000000;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"20% - 强调文字颜色 2";}
.style43
	{color:#44546A;
	font-size:11.0pt;
	font-weight:700;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:134;
	mso-style-name:"标题 4";}
.style44
	{mso-number-format:"_ \0022\00A5\0022* \#\,\#\#0_ \;_ \0022\00A5\0022* \\-\#\,\#\#0_ \;_ \0022\00A5\0022* \0022-\0022_ \;_ \@_ ";
	mso-style-name:"货币[0]";
	mso-style-id:7;}
.style45
	{mso-pattern:auto none;
	background:#FFE699;
	color:#000000;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"40% - 强调文字颜色 4";}
.style46
	{mso-number-format:"_ * \#\,\#\#0\.00_ \;_ * \\-\#\,\#\#0\.00_ \;_ * \0022-\0022??_ \;_ \@_ ";
	mso-style-name:"千位分隔";
	mso-style-id:3;}
.style47
	{color:#800080;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:underline;
	text-underline-style:single;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"已访问的超链接";
	mso-style-id:9;}
.style48
	{color:#44546A;
	font-size:18.0pt;
	font-weight:700;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:134;
	mso-style-name:"标题";}
.style49
	{mso-pattern:auto none;
	background:#F8CBAD;
	color:#000000;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"40% - 强调文字颜色 2";}
.style50
	{color:#FF0000;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"警告文本";}
.style51
	{mso-pattern:auto none;
	background:#C9C9C9;
	color:#FFFFFF;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"60% - 强调文字颜色 3";}
.style52
	{mso-pattern:auto none;
	background:#FFFFCC;
	border:.5pt solid #B2B2B2;
	mso-style-name:"注释";}
.style53
	{mso-pattern:auto none;
	background:#E2EFDA;
	color:#000000;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"20% - 强调文字颜色 6";}
.style54
	{mso-pattern:auto none;
	background:#4472C4;
	color:#FFFFFF;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"强调文字颜色 5";}
.style55
	{mso-pattern:auto none;
	background:#C6E0B4;
	color:#000000;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"40% - 强调文字颜色 6";}
.style56
	{color:#0000FF;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:underline;
	text-underline-style:single;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"超链接";
	mso-style-id:8;}
.style57
	{mso-number-format:"_ * \#\,\#\#0_ \;_ * \\-\#\,\#\#0_ \;_ * \0022-\0022_ \;_ \@_ ";
	mso-style-name:"千位分隔[0]";
	mso-style-id:6;}
.style58
	{color:#44546A;
	font-size:13.0pt;
	font-weight:700;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:134;
	border-bottom:1.0pt solid #5B9BD5;
	mso-style-name:"标题 2";}
.style59
	{mso-pattern:auto none;
	background:#B4C6E7;
	color:#000000;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"40% - 强调文字颜色 5";}
.style60
	{color:#44546A;
	font-size:11.0pt;
	font-weight:700;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:134;
	border-bottom:1.0pt solid #ACCCEA;
	mso-style-name:"标题 3";}
.style61
	{mso-pattern:auto none;
	background:#70AD47;
	color:#FFFFFF;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"强调文字颜色 6";}
.style62
	{mso-pattern:auto none;
	background:#BDD7EE;
	color:#000000;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	mso-style-name:"40% - 强调文字颜色 1";}
.style63
	{color:#FA7D00;
	font-size:11.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:0;
	border-bottom:2.0pt double #FF8001;
	mso-style-name:"链接单元格";}
td
	{mso-style-parent:style0;
	padding-top:1px;
	padding-right:1px;
	padding-left:1px;
	mso-ignore:padding;
	mso-number-format:"General";
	text-align:general;
	vertical-align:middle;
	white-space:nowrap;
	mso-rotate:0;
	mso-pattern:auto;
	mso-background-source:auto;
	color:#000000;
	font-size:12.0pt;
	font-weight:400;
	font-style:normal;
	text-decoration:none;
	font-family:宋体;
	mso-generic-font-family:auto;
	mso-font-charset:134;
	border:none;
	mso-protection:locked visible;}
.xl65
	{mso-style-parent:style0;
	text-align:center;
	white-space:normal;
	font-size:9.0pt;
	font-weight:700;
	mso-font-charset:134;
	border-top:1.0pt solid #000000;}
.xl66
	{mso-style-parent:style0;
	text-align:center;
	white-space:normal;
	font-size:9.0pt;
	font-weight:700;
	mso-font-charset:134;
	border-top:1.0pt solid #000000;}
.xl67
	{mso-style-parent:style0;
	text-align:center;
	white-space:normal;
	font-size:8.0pt;
	mso-font-charset:134;}
.xl68
	{mso-style-parent:style0;
	text-align:center;
	white-space:normal;
	font-size:7.0pt;
	mso-font-charset:134;}
.xl69
	{mso-style-parent:style0;
	text-align:center;
	white-space:normal;
	font-size:7.0pt;
	font-weight:700;
	mso-font-charset:134;
	border-bottom:.5pt solid #000000;}
.xl70
	{mso-style-parent:style0;
	text-align:center;
	white-space:normal;
	font-size:7.0pt;
	mso-font-charset:134;
	border-bottom:.5pt solid #000000;}
.xl71
	{mso-style-parent:style0;
	text-align:center;
	white-space:normal;
	font-size:7.0pt;
	font-weight:700;
	mso-font-charset:134;}
.xl72
	{mso-style-parent:style0;
	text-align:center;
	white-space:normal;
	font-size:7.0pt;
	font-weight:700;
	mso-font-charset:134;
	border-bottom:1.0pt solid #000000;}
.xl73
	{mso-style-parent:style0;
	text-align:center;
	white-space:normal;
	font-size:7.0pt;
	mso-font-charset:134;
	border-bottom:1.0pt solid #000000;}
 -->  </style>
  <!--[if gte mso 9]>
   <xml>
    <x:ExcelWorkbook>
     <x:ExcelWorksheets>
      <x:ExcelWorksheet>
       <x:Name>Sheet1</x:Name>
       <x:WorksheetOptions>
        <x:DefaultRowHeight>352</x:DefaultRowHeight>
        <x:StandardWidth>2340</x:StandardWidth>
        <x:Selected/>
        <x:Panes>
         <x:Pane>
          <x:Number>3</x:Number>
          <x:ActiveCol>10</x:ActiveCol>
          <x:ActiveRow>10</x:ActiveRow>
          <x:RangeSelection>K11</x:RangeSelection>
         </x:Pane>
        </x:Panes>
        <x:ProtectContents>False</x:ProtectContents>
        <x:ProtectObjects>False</x:ProtectObjects>
        <x:ProtectScenarios>False</x:ProtectScenarios>
        <x:PageBreakZoom>100</x:PageBreakZoom>
        <x:Zoom>121</x:Zoom>
        <x:Print>
         <x:PaperSizeIndex>9</x:PaperSizeIndex>
        </x:Print>
       </x:WorksheetOptions>
      </x:ExcelWorksheet>
     </x:ExcelWorksheets>
     <x:ProtectStructure>False</x:ProtectStructure>
     <x:ProtectWindows>False</x:ProtectWindows>
     <x:WindowHeight>14620</x:WindowHeight>
     <x:WindowWidth>26700</x:WindowWidth>
    </x:ExcelWorkbook>
    <x:SupBook>
     <x:Path>/private/var/folders/nt/mk_5djwj3jn50l47f5681wym0000gn/T/com.kingsoft.wpsoffice.mac/</x:Path>
    </x:SupBook>
   </xml>
  <![endif]-->
 </head>
 <body link="blue" vlink="purple">
  <table width="681.60" border="0" cellpadding="0" cellspacing="0" style='width:681.60pt;border-collapse:collapse;table-layout:fixed;'>
   <col width="56.80" span="12" style='mso-width-source:userset;mso-width-alt:2423;'/>
   <tr height="19.10" style='height:19.10pt;mso-height-source:userset;mso-height-alt:382;'>
    <td class="xl65" height="19.10" width="340.80" colspan="6" style='height:19.10pt;width:340.80pt;border-right:none;border-bottom:none;' x:str>Datasets with different mean similarities</td>
    <td class="xl65" width="340.80" colspan="6" style='width:340.80pt;border-right:none;border-bottom:none;' x:str>Datasets with different scales of tree length</td>
   </tr>
   <tr height="10" style='height:10.00pt;mso-height-source:userset;mso-height-alt:200;'>
    <td class="xl67" height="10" colspan="6" style='height:10.00pt;border-right:none;border-bottom:none;' x:str>(random branch lengths)</td>
    <td class="xl67" colspan="6" style='border-right:none;border-bottom:none;' x:str><span style='mso-spacerun:yes;'>&nbsp;</span><font class="font2">(preset branch lengths based on real cases)</font></td>
   </tr>
   <tr height="17.60" style='height:17.60pt;mso-height-source:userset;mso-height-alt:352;'>
    <td class="xl68" height="17.60" colspan="3" style='height:17.60pt;border-right:none;border-bottom:none;' x:str>SARS-CoV-2-like genome</td>
    <td class="xl68" colspan="3" style='border-right:none;border-bottom:none;' x:str>Mitochondrial-like genome</td>
    <td class="xl68" colspan="3" style='border-right:none;border-bottom:none;' x:str>SARS-CoV-2-like genome</td>
    <td class="xl68" colspan="3" style='border-right:none;border-bottom:none;' x:str>Mitochondrial-like genome</td>
   </tr>
   <tr height="22" style='height:22.00pt;'>
    <td class="xl69" height="22" style='height:22.00pt;' x:str>Mean similarity</td>
    <td class="xl70" x:str>Mean length difference</td>
    <td class="xl70" x:str>Tree length</td>
    <td class="xl69" x:str>Mean similarity</td>
    <td class="xl70" x:str>Mean length difference</td>
    <td class="xl70" x:str>Tree length</td>
    <td class="xl69" x:str>Tree length</td>
    <td class="xl70" x:str>Mean length difference</td>
    <td class="xl70" x:str>Mean similarity</td>
    <td class="xl69" x:str>Tree length</td>
    <td class="xl70" x:str>Mean length difference</td>
    <td class="xl70" x:str>Mean similarity</td>
   </tr>
   <tr height="17.60" style='height:17.60pt;'>
    <td class="xl71" height="17.60" style='height:17.60pt;' x:num>0.99</td>
    <td class="xl68" x:num>48</td>
    <td class="xl68" x:num>0.1</td>
    <td class="xl71" x:num>0.99</td>
    <td class="xl68" x:num>24</td>
    <td class="xl68" x:num>0.1</td>
    <td class="xl71" x:num>0.5</td>
    <td class="xl68" x:num>213</td>
    <td class="xl68" x:num>0.97</td>
    <td class="xl71" x:num>0.5</td>
    <td class="xl68" x:num>115</td>
    <td class="xl68" x:num>0.98</td>
   </tr>
   <tr height="17.60" style='height:17.60pt;'>
    <td class="xl71" height="17.60" style='height:17.60pt;' x:num>0.98</td>
    <td class="xl68" x:num>145</td>
    <td class="xl68" x:num>0.3</td>
    <td class="xl71" x:num>0.98</td>
    <td class="xl68" x:num>77</td>
    <td class="xl68" x:num>0.3</td>
    <td class="xl71" x:num>1</td>
    <td class="xl68" x:num>439</td>
    <td class="xl68" x:num>0.95</td>
    <td class="xl71" x:num>1</td>
    <td class="xl68" x:num>223</td>
    <td class="xl68" x:num>0.96</td>
   </tr>
   <tr height="17.60" style='height:17.60pt;'>
    <td class="xl71" height="17.60" style='height:17.60pt;' x:num>0.97</td>
    <td class="xl68" x:num>213</td>
    <td class="xl68" x:num>0.45</td>
    <td class="xl71" x:num>0.97</td>
    <td class="xl68" x:num>119</td>
    <td class="xl68" x:num>0.5</td>
    <td class="xl71" x:num>1.5</td>
    <td class="xl68" x:num>662</td>
    <td class="xl68" x:num>0.93</td>
    <td class="xl71" x:num>1.5</td>
    <td class="xl68" x:num>333</td>
    <td class="xl68" x:num>0.95</td>
   </tr>
   <tr height="17.60" style='height:17.60pt;'>
    <td class="xl71" height="17.60" style='height:17.60pt;' x:num>0.96</td>
    <td class="xl68" x:num>317</td>
    <td class="xl68" x:num>0.65</td>
    <td class="xl71" x:num>0.96</td>
    <td class="xl68" x:num>188</td>
    <td class="xl68" x:num>0.7</td>
    <td class="xl71" x:num>2</td>
    <td class="xl68" x:num>869</td>
    <td class="xl68" x:num>0.92</td>
    <td class="xl71" x:num>2</td>
    <td class="xl68" x:num>456</td>
    <td class="xl68" x:num>0.95</td>
   </tr>
   <tr height="17.60" style='height:17.60pt;'>
    <td class="xl71" height="17.60" style='height:17.60pt;' x:num>0.95</td>
    <td class="xl68" x:num>340</td>
    <td class="xl68" x:num>0.75</td>
    <td class="xl71" x:num>0.95</td>
    <td class="xl68" x:num>258</td>
    <td class="xl68" x:num>1</td>
    <td class="xl71" x:num>2.5</td>
    <td class="xl68" x:num>1050</td>
    <td class="xl68" x:num>0.91</td>
    <td class="xl71" x:num>2.5</td>
    <td class="xl68" x:num>553</td>
    <td class="xl68" x:num>0.94</td>
   </tr>
   <tr height="17.60" style='height:17.60pt;'>
    <td class="xl71" height="17.60" style='height:17.60pt;' x:num>0.94</td>
    <td class="xl68" x:num>491</td>
    <td class="xl68" x:num>1</td>
    <td class="xl71" x:num>0.94</td>
    <td class="xl68" x:num>372</td>
    <td class="xl68" x:num>1.5</td>
    <td class="xl71" x:num>3</td>
    <td class="xl68" x:num>1291</td>
    <td class="xl68" x:num>0.9</td>
    <td class="xl71" x:num>3</td>
    <td class="xl68" x:num>658</td>
    <td class="xl68" x:num>0.93</td>
   </tr>
   <tr height="17.60" style='height:17.60pt;'>
    <td class="xl71" height="17.60" style='height:17.60pt;' x:num>0.93</td>
    <td class="xl68" x:num>596</td>
    <td class="xl68" x:num>1.3</td>
    <td class="xl71" x:num>0.93</td>
    <td class="xl68" x:num>551</td>
    <td class="xl68" x:num>2.15</td>
    <td class="xl71" x:num>3.5</td>
    <td class="xl68" x:num>1484</td>
    <td class="xl68" x:num>0.88</td>
    <td class="xl71" x:num>3.5</td>
    <td class="xl68" x:num>769</td>
    <td class="xl68" x:num>0.93</td>
   </tr>
   <tr height="17.60" style='height:17.60pt;'>
    <td class="xl71" height="17.60" style='height:17.60pt;' x:num>0.92</td>
    <td class="xl68" x:num>697</td>
    <td class="xl68" x:num>1.5</td>
    <td class="xl71" x:num>0.92</td>
    <td class="xl68" x:num>683</td>
    <td class="xl68" x:num>2.7</td>
    <td class="xl71" x:num>4</td>
    <td class="xl68" x:num>1692</td>
    <td class="xl68" x:num>0.87</td>
    <td class="xl71" x:num>4</td>
    <td class="xl68" x:num>879</td>
    <td class="xl68" x:num>0.92</td>
   </tr>
   <tr height="17.60" style='height:17.60pt;'>
    <td class="xl71" height="17.60" style='height:17.60pt;' x:num>0.91</td>
    <td class="xl68" x:num>838</td>
    <td class="xl68" x:num>1.7</td>
    <td class="xl71" x:num>0.91</td>
    <td class="xl68" x:num>795</td>
    <td class="xl68" x:num>3.3</td>
    <td class="xl71" x:num>4.5</td>
    <td class="xl68" x:num>1907</td>
    <td class="xl68" x:num>0.86</td>
    <td class="xl71" x:num>4.5</td>
    <td class="xl68" x:num>998</td>
    <td class="xl68" x:num>0.91</td>
   </tr>
   <tr height="17.60" style='height:17.60pt;'>
    <td class="xl71" height="17.60" style='height:17.60pt;' x:num>0.9</td>
    <td class="xl68" x:num>961</td>
    <td class="xl68" x:num>2</td>
    <td class="xl71" x:num>0.9</td>
    <td class="xl68" x:num>991</td>
    <td class="xl68" x:num>4</td>
    <td class="xl71" x:num>5</td>
    <td class="xl68" x:num>2110</td>
    <td class="xl68" x:num>0.86</td>
    <td class="xl71" x:num>5</td>
    <td class="xl68" x:num>1104</td>
    <td class="xl68" x:num>0.91</td>
   </tr>
   <tr height="17.60" style='height:17.60pt;'>
    <td class="xl71" height="17.60" style='height:17.60pt;' x:num>0.85</td>
    <td class="xl68" x:num>1930</td>
    <td class="xl68" x:num>4.1</td>
    <td class="xl71" x:num>0.85</td>
    <td class="xl68" x:num>1831</td>
    <td class="xl68" x:num>7.5</td>
    <td class="xl71" x:num>7.5</td>
    <td class="xl68" x:num>3169</td>
    <td class="xl68" x:num>0.82</td>
    <td class="xl71" x:num>7.5</td>
    <td class="xl68" x:num>1630</td>
    <td class="xl68" x:num>0.89</td>
   </tr>
   <tr height="17.60" style='height:17.60pt;'>
    <td class="xl71" height="17.60" style='height:17.60pt;' x:num>0.8</td>
    <td class="xl68" x:num>3376</td>
    <td class="xl68" x:num>7.5</td>
    <td class="xl71" x:num>0.8</td>
    <td class="xl68" x:num>2958</td>
    <td class="xl68" x:num>12.1</td>
    <td class="xl71" x:num>10</td>
    <td class="xl68" x:num>4175</td>
    <td class="xl68" x:num>0.79</td>
    <td class="xl71" x:num>10</td>
    <td class="xl68" x:num>2166</td>
    <td class="xl68" x:num>0.86</td>
   </tr>
   <tr height="17.60" style='height:17.60pt;'>
    <td class="xl71" height="17.60" style='height:17.60pt;' x:num>0.75</td>
    <td class="xl68" x:num>4971</td>
    <td class="xl68" x:num>11</td>
    <td class="xl71" x:num>0.75</td>
    <td class="xl68" x:num>4097</td>
    <td class="xl68" x:num>16.75</td>
    <td class="xl71" x:num>12.5</td>
    <td class="xl68" x:num>5145</td>
    <td class="xl68" x:num>0.76</td>
    <td class="xl71" x:num>12.5</td>
    <td class="xl68" x:num>2698</td>
    <td class="xl68" x:num>0.84</td>
   </tr>
   <tr height="17.60" style='height:17.60pt;'>
    <td class="xl72" height="17.60" style='height:17.60pt;' x:num>0.7</td>
    <td class="xl73" x:num>6504</td>
    <td class="xl73" x:num>15.68</td>
    <td class="xl72" x:num>0.7</td>
    <td class="xl73" x:num>5232</td>
    <td class="xl73" x:num>23</td>
    <td class="xl72" x:num>15</td>
    <td class="xl73" x:num>6195</td>
    <td class="xl73" x:num>0.75</td>
    <td class="xl72" x:num>15</td>
    <td class="xl73" x:num>3220</td>
    <td class="xl73" x:num>0.82</td>
   </tr>
   <![if supportMisalignedColumns]>
    <tr width="0" style='display:none;'>
     <td width="57" style='width:57;'></td>
    </tr>
   <![endif]>
  </table>
 </body>
</html>






# Star-tree simulated datasets

## 14 simulated datasets

There are 1000 sequences in each dataset with different similarities (99%, 98%, 97%, 96%, 95%, 94%, 93%, 92%, 91%, 90%, 85%, 80%, 70%, 60%). The DNA center sequences were simulated randomly with 25% of A, C, T and G in the length of 30kb. Then the other 999 sequences, randomly mutated from the center sequence with substitutions: deletions = 10:1 and fixed 2 insertions (Perl script: [small_variation_simulation_splice.pl](https://github.com/malabz/MSATOOLS/tree/main/small_variation_simulation)), together with center sequence act as reference alignment. After deleted all the gaps in the reference alignments, they were used as test datasets.  Use the following command line to download and uncompress <a href="http://lab.malab.cn/%7Etfr/HAlign3_testdata/star_simudata_1000seq.tar.xz" download="star_simudata_1000seq.tar.xz">star_simudata_1000seq.tar.xz</a> :

```bash
#download
wget http://lab.malab.cn/%7Etfr/HAlign3_testdata/simudata_1000seq.tar.xz

#uncompress
tar -Jxf simudata_1000seq.tar.xz
```

reference datasets:

```
file            format  type  num_seqs     sum_len  min_len  avg_len  max_len
refer_60.fasta  FASTA   DNA      1,000  31,932,000   31,932   31,932   31,932
refer_70.fasta  FASTA   DNA      1,000  31,921,000   31,921   31,921   31,921
refer_80.fasta  FASTA   DNA      1,000  31,936,000   31,936   31,936   31,936
refer_85.fasta  FASTA   DNA      1,000  31,926,000   31,926   31,926   31,926
refer_90.fasta  FASTA   DNA      1,000  31,937,000   31,937   31,937   31,937
refer_91.fasta  FASTA   DNA      1,000  31,932,000   31,932   31,932   31,932
refer_92.fasta  FASTA   DNA      1,000  31,936,000   31,936   31,936   31,936
refer_93.fasta  FASTA   DNA      1,000  31,933,000   31,933   31,933   31,933
refer_94.fasta  FASTA   DNA      1,000  31,932,000   31,932   31,932   31,932
refer_95.fasta  FASTA   DNA      1,000  31,931,000   31,931   31,931   31,931
refer_96.fasta  FASTA   DNA      1,000  31,949,000   31,949   31,949   31,949
refer_97.fasta  FASTA   DNA      1,000  31,930,000   31,930   31,930   31,930
refer_98.fasta  FASTA   DNA      1,000  31,938,000   31,938   31,938   31,938
refer_99.fasta  FASTA   DNA      1,000  31,941,000   31,941   31,941   31,941
```

test datasets:

```
file                format  type  num_seqs     sum_len  min_len   avg_len  max_len
simulated_60.fasta  FASTA   DNA      1,000  28,923,078   28,922  28,923.1   30,000
simulated_70.fasta  FASTA   DNA      1,000  29,192,808   29,192  29,192.8   30,000
simulated_80.fasta  FASTA   DNA      1,000  29,462,538   29,462  29,462.5   30,000
simulated_85.fasta  FASTA   DNA      1,000  29,597,403   29,597  29,597.4   30,000
simulated_90.fasta  FASTA   DNA      1,000  29,732,268   29,732  29,732.3   30,000
simulated_91.fasta  FASTA   DNA      1,000  29,759,241   29,759  29,759.2   30,000
simulated_92.fasta  FASTA   DNA      1,000  29,786,214   29,786  29,786.2   30,000
simulated_93.fasta  FASTA   DNA      1,000  29,813,187   29,813  29,813.2   30,000
simulated_94.fasta  FASTA   DNA      1,000  29,840,160   29,840  29,840.2   30,000
simulated_95.fasta  FASTA   DNA      1,000  29,867,133   29,867  29,867.1   30,000
simulated_96.fasta  FASTA   DNA      1,000  29,894,106   29,894  29,894.1   30,000
simulated_97.fasta  FASTA   DNA      1,000  29,921,079   29,921  29,921.1   30,000
simulated_98.fasta  FASTA   DNA      1,000  29,948,052   29,948  29,948.1   30,000
simulated_99.fasta  FASTA   DNA      1,000  29,975,025   29,975    29,975   30,000
```



## Splitted simulated datasets

Since Mafft, Muscle and ClustalO cannot complete the alignment above, reference and test datasets above were split into 9 small datasets with the same corresponding center sequence respectively in order to compare the performance fairly. Use the following command line to uncompress <a href="http://lab.malab.cn/%7Etfr/HAlign3_testdata/star_simudata_split.tar.xz" download="star_simudata_split.tar.xz">star_simudata_split.tar.xz</a> :

