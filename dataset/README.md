# Real datasets 

## Human mitochondrial genomes

<a href="http://lab.malab.cn/%7Etfr/HAlign3_testdata/mt_genome.tar.xz" download="mt_genome.tar.xz">mt_genome.tar.xz</a> is composed of 672 sequences with the longest length of 16,579 bp and the shortest length of 16,556 bp, which is highly similar to each other  (>99%). Use the following command line to uncompress:
```
tar -Jxf sars_cov_2_1Mseq.tar.xz
```

## Respiratory syndrome coronavirus 2 genomes

Respiratory syndrome coronavirus 2 (SARS‑CoV‑2) is an RNA virus, which causes coronavirus disease 2019  and is responsible for the COVID-19 pandemic. Its sequences are all recorded in the form of DNA for the convenience of sequencing and recording. SARS-CoV-2 genomes in high quality (without ambiguous nucleotides) were download from GISAID website (https://www.gisaid.org, updated on November 11, 2021). 

<a href="http://lab.malab.cn/%7Etfr/HAlign3_testdata/sars_cov_2_1Mseq.tar.xz" download="sars_cov_2_1Mseq.tar.xz">sars_cov_2_1Mseq.tar.xz</a> is 1 million SARS-CoV-2 sequences (21111 to 29891bp), which have >99% similarity to the reference genome (GISAID accession ID: EPI_ISL_402124), were selected by CD-HIT (threshold=0.99). The uncompressed file is nearly 29GB, which needs ～500GB RAM to be aligned by HAlign 3. 

<a href="http://lab.malab.cn/%7Etfr/HAlign3_testdata/sars_cov_2_500seq.tar.xz" download="sars_cov_2_500seq.tar.xz">sars_cov_2_500seq.tar.xz</a> is the first 500 sequences (29283 to 29891bp) from the dataset above. 500 SARS-CoV-2 genomes are less similar to each other (>97%), ranging from 21,111bp to 29,891bp.



## Mycobacterium 23S rRNA sequences

<a href="http://lab.malab.cn/%7Etfr/HAlign3_testdata/23s-rRNA.tar.xz" download="23s-rRNA.tar.xz">23s-rRNA.tar.xz</a> is composed of 641 Mycobacterium 23S rRNA sequences in the range from 1909 to 3485 bp, downloaded from the SLIVA rRNA database (http://www.arb-silva.de/) of bacteria, archaea and eukarya. Sequences from this dataset are more diverse to each other (>30%).


# Simulated datasets

## 14 simulated datasets

There are 1000 sequences in each dataset with different similarities (99%, 98%, 97%, 96%, 95%, 94%, 93%, 92%, 91%, 90%, 85%, 80%, 70%, 60%). The DNA center sequences were simulated randomly with 25% of A, C, T and G in the length of 30kb. Then the other 999 sequences, randomly mutated from the center sequence with substitutions: deletions = 10:1 and fixed 2 insertions (Perl script: [small_variation_simulation_splice.pl](https://github.com/malabz/MSATOOLS/tree/main/small_variation_simulation)), together with center sequence act as reference alignment. After deleted all the gaps in the reference alignments, they were used as test datasets.  Use the following command line to uncompress <a href="http://lab.malab.cn/%7Etfr/HAlign3_testdata/simudata_1000seq.tar.xz" download="simudata_1000seq.tar.xz">simudata_1000seq.tar.xz</a> :

```
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
file                 format  type  num_seqs     sum_len  min_len   avg_len  max_len
simulated_60.fasta   FASTA   DNA      1,000  28,923,078   28,922  28,923.1   30,000
simulated_70.fasta   FASTA   DNA      1,000  29,192,808   29,192  29,192.8   30,000
simulated_80.fasta   FASTA   DNA      1,000  29,462,538   29,462  29,462.5   30,000
simulated_85.fasta   FASTA   DNA      1,000  29,597,403   29,597  29,597.4   30,000
simulated_90.fasta   FASTA   DNA      1,000  29,732,268   29,732  29,732.3   30,000
simulated_91.fasta   FASTA   DNA      1,000  29,759,241   29,759  29,759.2   30,000
simulated_92.fasta   FASTA   DNA      1,000  29,786,214   29,786  29,786.2   30,000
simulated_93.fasta   FASTA   DNA      1,000  29,813,187   29,813  29,813.2   30,000
simulated_94.fasta   FASTA   DNA      1,000  29,840,160   29,840  29,840.2   30,000
simulated_95.fasta   FASTA   DNA      1,000  29,867,133   29,867  29,867.1   30,000
simulated_96.fasta   FASTA   DNA      1,000  29,894,106   29,894  29,894.1   30,000
simulated_97.fasta   FASTA   DNA      1,000  29,921,079   29,921  29,921.1   30,000
```



## Splitted simulated datasets

Since Mafft, Muscle and ClustalO cannot complete the alignment above, reference and test datasets above were splitted into 9 small datasets with the same corresponding center sequence respectively in order to compare the performance fairly. Use the following command line to uncompress <a href="http://lab.malab.cn/%7Etfr/HAlign3_testdata/simudata_splitted.tar.xz" download="simudata_splitted.tar.xz">simudata_splitted.tar.xz</a> :

```
tar -Jxf simudata_splitted.tar.xz
```
