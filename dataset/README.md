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



# Hierarchical tree simulated datasets


Two sets of SARS-CoV-2-like and two sets of mitochondrial-like genome datasets were simulated using INDELible v1.03 (Fletcher and Yang 2009). The substitution models in the simulation were based on the estimated models of 500 SARS‑CoV‑2 and 672 mitochondrial genome alignments, respectively, obtained via IQ-TREE v2.2.0-beta (Minh et al. 2020). One hundred SARS‑CoV‑2 and mitochondrial genomes randomly selected from the datasets above were aligned to build the tree for simulation. The simulation sequence length is 30 kb and 16 kb for the SARS-CoV-2-like and mitochondrial-like genomes, respectively. The indel model parameter was LAV 5 50 with the rates of 0.01 and 0.1 for insertion and deletion.

For the simulation of datasets with various mean similarities, every branch in the tree was a random length between 0 and 1 (NON-ULTRAMETRIC); then, tree length (i.e., the sum of branch lengths) was tuned to meet the mean similarities of 99%, 98%, 97%, 96%, 95%, 94%, 93%, 92%, 91%, 90%, 85%, 80%, 75%, and 70%. The mean similarity is the mean of the similarities between any two sequences in the dataset ([length_similarity_distribution.py](https://github.com/malabz/MSATOOLS/tree/main/length_similarity_distribution)), which is the percentage of matched characters in their pairwise alignment performed via MAFFT.

For the simulation of datasets with varying scales of tree length, every branch in the tree was the same as the tree of 100 SARS‑CoV‑2 and mitochondrial genomes; then, different scales of tree length (0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0, 7.5, 10.0, 12.5, and 15.0) were set.

The detailed information of those four sets of 14 datasets is summarized in the following table. Each dataset contained nine replicates with 100 simulated sequences each. The mean length difference is the mean of nine replicates of the difference between the reference alignment length and the average length of simulated sequences. The tree length, the difference between alignment length and the average sequence length, and the mean similarity of the 100 randomly selected SARS‑CoV‑2 and mitochondrial genomes are 0.028, 111, 0.996 and 0.048, 55, 0.998.


Download the datasets:                                                                                    
<a href="http://lab.malab.cn/%7Etfr/HAlign3_testdata/sars_cov_2_like_diff_similarity.tar.xz" download="sars_cov_2_like_diff_similarity.tar.xz">sars_cov_2_like_diff_similarity.tar.xz</a>                               
<a href="http://lab.malab.cn/%7Etfr/HAlign3_testdata/sars_cov_2_like_diff_treelength.tar.xz" download="sars_cov_2_like_diff_treelength.tar.xz">sars_cov_2_like_diff_treelength.tar.xz</a>                                           
<a href="http://lab.malab.cn/%7Etfr/HAlign3_testdata/mt_like_diff_similarity.tar.xz" download="mt_like_diff_similarity.tar.xz">mt_like_diff_similarity.tar.xz</a>                            
<a href="http://lab.malab.cn/%7Etfr/HAlign3_testdata/mt_like_diff_treelength.tar.xz" download="mt_like_diff_treelength.tar.xz">mt_like_diff_treelength.tar.xz</a>            

<html xmlns:v="urn:schemas-microsoft-com:vml" xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40">
 </head>
 <body link="blue" vlink="purple">
  <table width="660" border="0" cellpadding="0" cellspacing="0" style='width:660pt;border-collapse:collapse;table-layout:fixed;'>
   <col width="55" span="12" style='mso-width-source:userset;mso-width-alt:2423;'/>
   <tr height="19.10" style='height:19.10pt;mso-height-source:userset;mso-height-alt:382;'>
    <td class="xl65" height="19.10" width="330" colspan="6" style='height:19.10pt;width:330pt;border-right:none;border-bottom:none;' x:str>Datasets with different mean similarities</td>
    <td class="xl65" width="330" colspan="6" style='width:330pt;border-right:none;border-bottom:none;' x:str>Datasets with different scales of tree length</td>
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
  </table>
 </body>
</html>





# Star-tree simulated datasets

## 14 simulated datasets

Each dataset contained 1000 sequences in each dataset with different similarities (99%, 98%, 97%, 96%, 95%, 94%, 93%, 92%, 91%, 90%, 85%, 80%, 70%, and 60%). The DNA center sequences/templates were simulated randomly with 25% of A, C, T, and G at a length of 30 kb. Then, the other 999 sequences, which were randomly mutated from the center sequence with substitutions: deletions = 10:1 and fixed two insertions (Perl script: [small_variation_simulation_splice.pl](https://github.com/malabz/MSATOOLS/tree/main/small_variation_simulation)), together with the center sequence, acted as reference alignment. After deleting all the gaps in the reference alignments, they were used as test datasets. Use the following command line to download and uncompress <a href="http://lab.malab.cn/%7Etfr/HAlign3_testdata/star_simudata_1000seq.tar.xz" download="star_simudata_1000seq.tar.xz">star_simudata_1000seq.tar.xz</a> :

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

Since Mafft, Muscle and ClustalO cannot complete the alignment above, reference and test datasets were split into 9 small datasets with the same corresponding center sequence, respectively, to compare the performance fairly. Use the following command line to uncompress <a href="http://lab.malab.cn/%7Etfr/HAlign3_testdata/star_simudata_split.tar.xz" download="star_simudata_split.tar.xz">star_simudata_split.tar.xz</a> :

