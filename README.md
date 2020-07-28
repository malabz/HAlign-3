# Multiple Sequence Alignment with Suffix Tree

This project is aimed at improving a part of [HAlign2.0](https://github.com/malabz/HAlign), which could align multiple nucleotide sequences fast and accurately.

## Usage

```
java -jar halign-stmsa.jar [-h] [-o] [-t] [-F] [-s] infile
```

```
positional argument: 
  infile   nucleotide sequences in fasta format

optional arguments: 
  -h       show this help message and exit
  -o       target file
  -t       multi-thread
  -F       align all the sequences despite the huge difference of some sequences from the mainstream
  -s       output alignments without sequence identifiers, i.e. in plain txt format but with sequence order retained
```

## Update

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
