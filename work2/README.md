# Проект 1

Анализировался проект https://github.com/HxnDev/Multithreaded-Merge-Sort, в котором реализована 
версия алгоритма merge-sort. Шаги алгоритма: сначала массив делится на 4 последовательные части,
дальше все они сортируются параллельно: функции сортировки каждой части запускаются каждая
в своем потоке. Затем вызывается функция слияния, которая объединяет получившиеся части в единый 
массив, алгоритм окончен.

## Helgrind
Сборка исполняемого файла и первый запуск helgrind:
```
$ g++ -g -pthread -o main Multithreaded-Merge-Sort/q2.cpp
$ valgrind --tool=helgrind --log-file=helgrind-log-1-1 ./main < input1.txt 
```
Файл [input1.txt](https://github.com/aartdem/multithreaded-programming/blob/main/work2/input1.txt) -
исходные данные для программы (число элементов в массиве и сам массив). Результат работы helgrind доступны в
файле [helgrind-log-1-1.txt](https://github.com/aartdem/multithreaded-programming/blob/main/work2/helgrind-log-1-1.txt).

Мы можем видеть, что helgrind нашел 85 ошибок. При детальном рассмотрении ставновится понятно, что все эти ошибки 
связаны с потоком вывода, так как каждая функция сортировки выводит результат своей работы в консоль.
Возможны два решения:
1. В начале и в конце каждой функции сортировки захватывать и отпускать мьютекс соотвественно. В таком случае 
исполнение программы будет эквивалентно исполнению на одном потоке, что уменьшит производительность.
2. Закомментировать/удалить промежуточные выводы функций сортировки в консоль, так как эта информация 
полезна только при отладке.

Вывод helgrind после применения второго решения доуступен в файле 
[helgrind-log-1-2.txt](https://github.com/aartdem/multithreaded-programming/blob/main/work2/helgrind-log-1-2.txt), 
теперь количество ошибок стало равняться нулю

## Threadsanitizer
Первый вывод инструмета:
```
$ clang++ -fsanitize=thread -g -pthread -o main Multithreaded-Merge-Sort/q2.cpp 
$ ./main < input1.txt
FATAL: ThreadSanitizer: unexpected memory mapping 0x5f5a01a36000-0x5f5a01a57000
```
Как оказалалось, такая ошибка может возникать на некоторых версиях ядра Linux, что обсуждается
в [этом треде](https://github.com/google/sanitizers/issues/1716). Ошибка связана с ASLR. Решение, 
что мне помогло, ниже:
```
$ sudo sysctl vm.mmap_rnd_bits=28
```
После повторного запуска санитайзера получаем более осмысленные результаты, вывод в
[thread-sanitizer-log-1.txt](https://github.com/aartdem/multithreaded-programming/blob/main/work2/thread-sanitizer-log-1.txt).
SUMMARY:
```
SUMMARY: ThreadSanitizer: heap-use-after-free /home/aartdem/multithreaded-programming/work2/Multithreaded-Merge-Sort/q2.cpp:184:34 in mergeArray(void*)
```
Проблемы начинаются со строчки 172. Массив second_half имеет размер size_/2, но в строчках 182-190
к нему происходит обращение по индексам в диапазоне [size_/2; size_). Таким образом, ThreadSanitizer правильно определил
место в коде и поток, в котором происходит некорректное обращение к памяти.
