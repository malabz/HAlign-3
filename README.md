# HAlign 3: Fast multiple alignment of ultra-large similar DNA/RNA sequences

This project is aimed at improving a part of [HAlign2.0](https://github.com/malabz/HAlign-2), which could align multiple nucleotide sequences fast and accurately.

[![Anaconda-Server Badge](https://anaconda.org/malab/halign/badges/version.svg)](https://anaconda.org/malab/halign)
[![Anaconda-Server Badge](https://anaconda.org/malab/halign/badges/latest_release_date.svg)](https://anaconda.org/malab/halign)
[![Anaconda-Server Badge](https://anaconda.org/malab/halign/badges/latest_release_relative_date.svg)](https://anaconda.org/malab/halign)
[![Anaconda-Server Badge](https://anaconda.org/malab/halign/badges/platforms.svg)](https://anaconda.org/malab/halign)
[![Anaconda-Server Badge](https://anaconda.org/malab/halign/badges/license.svg)](https://anaconda.org/malab/halign)
[![Anaconda-Server Badge](https://anaconda.org/malab/halign/badges/downloads.svg)](https://anaconda.org/malab/halign)
[![Anaconda-Server Badge](https://anaconda.org/malab/halign/badges/installer/conda.svg)](https://anaconda.org/malab/halign)
<a href="https://anaconda.org/malab/halign"> <img src="https://anaconda.org/malab/halign/badges/downloads.svg" /> </a>



## Introduction

HAlign is a cross-platform program that performs multiple sequence alignment based on the center star strategy. Here, we present two major updates of HAlign 3 which help to improve the time efficiency and the alignment quality. 1) The suffix tree data structure is specifically modified to fit the nucleotide sequence: Left-child right-sibling is replaced by K-ary tree to build the suffix tree to reach a higher identical substring searching efficiency at the cost of a few extra space; 2) a global substring selection algorithm combining directed acyclic graphs with dynamic programming is adopted to screen out the unsatisfactory identical substrings. These improvements make HAlign 3 the only program which can align 1 million SARS-CoV-2 sequences fast and accurately. HAlign 3 can be easily installed via anaconda and released package on MacOS, Linux, Windows Subsystem for Linux and Windows systems, and the source code is available on github (https://github.com/malabz/HAlign-3).



## Installation and Usage

### 1.1 OSX/Linux/WSL(Windows Subsystem for Linux ) - using anaconda
1.Intall WSL for Windows. Instructional video [1](https://www.youtube.com/watch?v=X-DHaQLrBi8&t=5s) or [2](http://lab.malab.cn/%7Etfr/1.mp4) (Copyright belongs to the original work).

2.Download and install Anaconda. Download Anaconda versions for different systems from [here](https://www.anaconda.com/products/distribution#Downloads). Instructional video of anaconda installation [1](https://www.youtube.com/watch?v=AshsPB3KT-E) or [2](http://lab.malab.cn/%7Etfr/Install_anaconda_in_Linux.mp4) (Copyright belongs to the original work).

3.Install HAlign 3.

```bash
#1 Create an environment and install the required package openjdk for running halign and wget package for retrieving files using HTTP, HTTPS and FTP.
conda create -n halign_env -c conda-forge openjdk=11 wget

#2 Activate halign environment everytime when using halign program.
conda activate halign_env

#3 Install halign
conda install -c malab halign

#4 Test halign
halign -h
```



### 1.2 Usage for conda version

```
halign [options] <infile>
```

```
positional argument: 
    infile           nucleotide sequences in fasta format

optional arguments: 
    -o <filename>    output aligned file, with option (-s) on, sequence identifiers will not be outputted
    -t <integer>     multi-thread, with a default setting of half of the cores available
    -c <integer>     centre sequence index (0-based), (default: the longest sequence)
    -s               output alignments without sequence identifiers, i.e. in plain txt format but with sequence order retained, (default: off)
    -h               produce help message and exit
    -v               produce version message and exit
```



### 1.3 Example usage for conda version

1.Download [testdata](https://github.com/malabz/HAlign-3/tree/main/dataset).

2.Run HAlign 3.

Align mt_genome.fasta dataset by HAlign 3 with the setting of: 5 threads for paralization alignment, the 7th sequence as center sequence during alignment, output alignment block without sequence identifiers.

```shell
# Download dataset
wget http://lab.malab.cn/%7Etfr/HAlign3_testdata/mt_genome.tar.xz

# Uncompress dataset
tar -Jxf mt_genome.tar.xz

# Run halign
halign -o halign3ed_mt_genome.fasta -t 5 -c 6 -s mt_genome.fasta
```







### 2.1 OSX/Linux/windows - from a released package
1.Download and install  JDK 11 for different systems from [here](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html).  

2.Download HAlign3 from [relseases](https://github.com/malabz/HAlign-3/releases/download/v3.0.0-rc1/HAlign-3.0.0_rc1.jar).



### 2.2 Usage for released package

```
java -jar halign-stmsa.jar [options] <infile>
```

```
positional argument: 
    infile           nucleotide sequences in fasta format

optional arguments: 
    -o <filename>    output aligned file, with option (-s) on, sequence identifiers will not be outputted
    -t <integer>     multi-thread, with a default setting of half of the cores available
    -c <integer>     centre sequence index (0-based), (default: the longest sequence)
    -s               output alignments without sequence identifiers, i.e. in plain txt format but with sequence order retained, (default: off)
    -h               produce help message and exit
    -v               produce version message and exit
```



### 2.3 Example usage for released package

1.Download [testdata](https://github.com/malabz/HAlign-3/tree/main/dataset).

2.Run HAlign 3.

Align mt_genome.fasta dataset by HAlign 3 with the setting of: 5 threads for paralization alignment, the 7th sequence as center sequence during alignment, output alignment block without sequence identifiers.

```shell
# Download dataset
wget http://lab.malab.cn/%7Etfr/HAlign3_testdata/mt_genome.tar.xz

# Uncompress dataset
tar -Jxf mt_genome.tar.xz

# Run halign
java -jar halign-stmsa.jar -o halign3ed_mt_genome.fasta -t 5 -c 6 -s mt_genome.fasta
```



## License

HAlign 3 is a free software, licensed under [MIT](https://github.com/malabz/HAlign-3/blob/main/LICENSE).



## Feedback/Issues

HAlign 3 is supported by ZOU's Lab. If you have any questions and suggestions, please feel free to contact us on the [issues page](https://github.com/malabz/HAlign-3/issues). You are also welcomed to send a copy to Furong.TANG@hotmail.com to make sure we could answer you ASAP! 



## Citation

If you use this software please cite:

HAlign 3: Fast multiple alignment of ultra-large similar DNA/RNA sequences. [(2022)]()



## Change Log

* 28-07-2020

pairwise-alignment scoring rules adjusted

* 11-07-2020

optimizations

* 16-12-2019

refactoring and a new feature added

* 15-12-2019

`SuffixTree.java` rewritten to improve the performance

* 20-10-2019

initialisation



## Build

- JDK 11
- Maven
