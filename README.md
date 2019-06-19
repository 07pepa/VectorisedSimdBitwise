# SIMD bitwise ByteBufferbuffer procesors
This ***Soon To Be*** library is colection perfomance focused trasnformers and some conviniences (curently just bitwise and or xor and not (shifting in work)) for bytes ints and shorts
it is extensively tested and speed diference is mesurable with profiler.
it has been extracted from my banchelor thesis witch code is mostly avaible at https://github.com/07pepa/NucleicPreprocesor-banchelor-thesis
and extended to do all bitwise operations


## How to include to your project
....  not done yet

## Note
This effort will by compleatly displaced and largely surpased  with vector API planed and developed in java 

## FAQ
* why it is not library?
    * i did not had yet time to make it library (or learn how to do it)
    * there is some work to be done on iterface to make idiotic imput problem of compiletime..
* why is it most time faster ....   
    * on x64 cpus java VM may be able to employ 64 bit instruction thanks to withch proceses 8 bytes in one clock 
    * on 32 or 16 bit machines it is not that fast... it is recomended install x64 Java VM
* size limitation?
  * 2Gb time via maped bytebuffers ... preloading them is advised for performance reasons ...
* why you use SPOCK ?
  * i know it is overkill but i like it
  * i wanted to write something with it from start

## Speed diference
mesured with mapped bytebuffers data ares average of 10 runs
time is in ms always

there is preaty much perfomance parity between operations
as you can se here

***Performance parity between operations of procesors***
| Mb/ms |  AND |  OR  |  XOR | NOT  |
|:-----:|:----:|:----:|:----:|------|
|**Size** |**Time(ms)**|**Time(ms)**|**Time(ms)**|**Time(ms)**|
|  3MB  |  3.2 |  2.9 |  2.4 |  3.1 |
|  5MB  |  3.3 |  3.4 |  2.7 |  4.6 |
|  10MB |  3.7 |  5.3 |  4.6 | 4.60 |
|  50MB | 13.1 | 14.6 | 13.3 | 16.2 |
| 100MB |  28  | 25.7 | 25.7 |  24  |

from that reason only anding is domne

_NOTE:_ from this point forward there are used word lib
and com .... lib mean this ***Soon To Be*** library
and com mean common implementation (in while process all ints separately)

### **Bytes**
|Mb/ms|  LIB |  COM  |
|:---:|:----:|:-----:|
|**Size** |**Time(ms)**|**Time(ms)**|
|   3 |  3.2 |  7.2  |
|   5 |  3.3 |  7.8  |
|  10 |  3.7 |  17.2 |
|  50 | 13.1 |  81.4 |
| 100 |  28  | 163.4 |

### **Shorts**
|Mb/ms|  LIB |  COM |
|:---:|:----:|:----:|
|**Size** |**Time(ms)**|**Time(ms)**|
|   3 |  2.9 |  5.2 |
|   5 |  5.5 |  6.5 |
|  10 |  6.9 | 15.9 |
|  50 | 21.8 |  54  |
| 100 | 40.4 |  104 |

### **Ints**
|Mb/ms|  LIB |  COM |
|:---:|:----:|:----:|
|**Size** |**Time(ms)**|**Time(ms)**|
|  50 | 19.8 | 38.3 |
| 100 | 37.3 |  76  |
| 200 | 72.4 |  103 |
| 300 |  109 |  165 |

