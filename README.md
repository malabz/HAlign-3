# Multiple Sequence Alignment with Suffix Tree

This project is aimed at improving a part of [HAlign2.0](https://github.com/malabz/HAlign), which could align multiple nucleotide sequences fast and accurately.

## Usage

```
java -jar halign-stmsa.jar [-h] [-v] [-o] [-t thread] [-c centre_index] [-s] infile
```

```
positional argument: 
  infile   nucleotide sequences in fasta format

optional arguments: 
  -h       produce help message and exit
  -v       produce version message and exit
  -o       target file
  -t       multi-thread, with a default setting of half of the cores available
  -c       centre sequence index (0-based)
  -s       output alignments without sequence identifiers, i.e. in plain txt format but with sequence order retained
```

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
