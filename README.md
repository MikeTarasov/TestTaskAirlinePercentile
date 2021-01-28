﻿# TestTaskTicketsPercentileДемонстрационный запуск приложения через консоль:> java -jar Test.jarОбычный запуск приложения через консоль:> java -jar Test.jar path directory/tickets.json from Москва to Пекин from-zone 3 to-zone 2 percentile 90Параметры:- path -> Путь до *.json файла -> демонстрационный входит в архив- from -> Город отправления -> (по умолчанию) Владивосток- to -> Город прибытия -> (по умолчанию) Тель-Авив- from-zone -> Число – временная зона времени отправления в файле (например: для UTC+10 = 10, для UTC-2 = -2) -> (по умолчанию) 10- to-zone -> Число – временная зона времени прибытия в файле -> (по умолчанию) 2- percentile -> Число = величине перцентиля -> (по умолчанию) 90*Параметры, значения которых совпадают со значениями по умолчанию можно не указывать.**При проблемах с кодировкой (“Average time: not found!” – при существующих в .json городах отправления и прибытия) – добавлять в строку запуска -Dfile.encoding=UTF-8 (при кодировке .json = UTF-8)(java -Dfile.encoding=UTF-8 -jar Test.jar)***Test.jar помещен в директорию /out/