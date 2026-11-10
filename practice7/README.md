# Лабораторная работа №7. Файловый ввод-вывод

Проект: `practice7`, пакет `coursehub.practice7`, Java 17, JUnit 5.
Каждое задание — отдельный класс `TaskN...` и отдельный тест `TaskNNTest`.

## Структура

```text
practice7
├── pom.xml
├── README.md
└── src
    ├── main/java/coursehub/practice7
    │   ├── Course.java
    │   ├── CatalogLineParser.java
    │   ├── CatalogFormatException.java
    │   ├── TruncatedCatalogException.java
    │   ├── UnsupportedVersionException.java
    │   ├── ImportError.java
    │   ├── Task1TextCatalog.java
    │   ├── Task2BufferedText.java
    │   ├── Task3ResourcePipeline.java
    │   ├── Task4LazyLines.java
    │   ├── Task5BinaryCatalog.java
    │   ├── Task6AtomicReplace.java
    │   ├── Task7CatalogImport.java
    │   ├── Task8StreamingImport.java
    │   └── Task9VersionedCatalog.java
    └── test/java/coursehub/practice7
        ├── Task01Test.java
        ├── ...
        └── Task09Test.java
```

Запуск:

```bash
cd practice7
mvn test
```

## Задание 1. `Files.readString` / `Files.writeString`

Класс: [Task1TextCatalog.java](src/main/java/coursehub/practice7/Task1TextCatalog.java).

Каталог хранится строками `code|title` в UTF-8. Кодировка передана явно в
`Files.writeString` и `Files.readString`, поэтому результат не зависит от
платформенной кодировки по умолчанию.

Контракт чтения:

- файла нет — это нормальное состояние «каталог ещё не создан»,
  возвращается `LoadResult(found=false, courses=[])`;
- по пути существует каталог, а не файл — это ошибка `IOException`
  (`not a regular file`), а не пустой успешный каталог;
- строка не разбирается — `CatalogFormatException` с номером строки.

Ошибка нигде не ловится «для печати»: исключение идёт вызывающему коду.
Именно поэтому отсутствие файла и битый файл — разные исходы, а не один
`catch (IOException) { return List.of(); }`.

Тест `Task01Test` проверяет UTF-8 с кириллицей, отсутствие файла, каталог
вместо файла и битую строку с номером `line 2`.

## Задание 2. `BufferedReader` / `BufferedWriter`

Класс: [Task2BufferedText.java](src/main/java/coursehub/practice7/Task2BufferedText.java).

Копирование идёт кусками `char[8192]`: `Reader.read(char[])` и
`Writer.write(char[], int, int)`. Одновременно считаются:

- `characters` — сколько `char` прочитано;
- `lines` — число строк с учётом `\n`, `\r\n` и одиночного `\r`;
- `bytes` — `Files.size(target)` после закрытия writer'а.

Граница **bytes → chars** проходит в декодере:

```text
байты файла --CharsetDecoder--> char[] (UTF-16) --> обработка --> CharsetEncoder --> байты target
```

`char` — 16-битная кодовая единица UTF-16, а не «байт» и не всегда «один
Unicode code point». Кириллица в UTF-8 занимает 2 байта, в UTF-16 — 2 байта
плюс BOM; эмодзи в UTF-16 — суррогатная пара, то есть два `char`. Поэтому
`characters` считает именно `char`, а не байты и не code points. Например,
`"Привет\nмир\n"` — это 11 `char`, 20 байт в UTF-8 и 24 байта в UTF-16.

Тесты проверяют UTF-8, UTF-16, пустой файл, последнюю строку без перевода
строки и разные виды переводов строк.

## Задание 3. Try-with-resources

Класс: [Task3ResourcePipeline.java](src/main/java/coursehub/practice7/Task3ResourcePipeline.java).

Ресурсы объявлены в таком порядке:

```java
try (TrackingReader reader = ...;
     BufferedReader bufferedReader = ...;
     TrackingWriter writer = ...;
     BufferedWriter bufferedWriter = ...) {
    ...
}
```

Java закрывает их в обратном порядке: `bufferedWriter` (он же flush'ит и
закрывает `writer`), затем `bufferedReader` (закрывает `reader`). Тест
подставляет mapper, который бросает `IllegalStateException` на строке
`boom`, и проверяет:

- оба ресурса закрыты: лог равен `[writer, reader]`;
- строки, записанные до исключения, успели уйти в target при закрытии
  writer'а;
- исключение обработки не потеряно.

Если `close()` сам бросит исключение, try-with-resources добавит его к
исходному как suppressed. Здесь этого не требуется, но механизм тот же.

## Задание 4. Ленивые строки

Класс: [Task4LazyLines.java](src/main/java/coursehub/practice7/Task4LazyLines.java).

`Files.lines` возвращает ленивый `Stream<String>`, который владеет открытым
`BufferedReader`. Сам по себе terminal operation (`toList`, `count`, `sum`) не
закрывает поток. Поэтому корректные методы `totalCharacters` и `head`
создают поток и закрывают его в одном try-with-resources:

```java
try (Stream<String> lines = Files.lines(file, UTF_8)) {
    return lines.limit(limit).toList();
}
```

В тесте `Task04Test` вспомогательный метод `brokenLines` специально
возвращает поток наружу без закрытия. Это ошибка проектирования: вызывающий
код получает поток, но API не напоминает, что теперь именно он обязан
вызвать `close()`. Если он этого не сделает, дескриптор файла останется
открытым до сборки мусора. В `src/main` такого метода нет: рабочий код
закрывает поток там же, где открыл.

Правило: ресурсом владеет тот, кто его открыл. Если метод открыл
`Files.lines`, он же должен закрыть поток; если поток всё-таки возвращается,
это должно быть явно задокументировано, а вызывающий обязан использовать
try-with-resources. Тест закрывает ошибочно возвращённый поток сам и затем
проверяет, что повторный terminal operation бросает `IllegalStateException` —
поток действительно закрыт.

## Задание 5. Бинарный формат

Класс: [Task5BinaryCatalog.java](src/main/java/coursehub/practice7/Task5BinaryCatalog.java).

Формат:

```text
int magic   = 0x43415431 ("CAT1")
int version = 1
int count
count раз:
  UTF code
  UTF title
```

Запись и чтение идут через `DataOutputStream` / `DataInputStream` поверх
буферизованных потоков. Читатель проверяет:

- magic — иначе `CatalogFormatException`;
- version — иначе `UnsupportedVersionException` (версия не угадывается);
- count — `0..1_000_000`, чтобы повреждённое число не привело к попытке
  выделить огромный массив;
- конец файла — `EOFException` превращается в `TruncatedCatalogException`.

Тесты проверяют round-trip, пустой каталог, неизвестную версию, обрезанный
файл и неверный magic.

## Задание 6. Временный файл и move

Класс: [Task6AtomicReplace.java](src/main/java/coursehub/practice7/Task6AtomicReplace.java).

Порядок замены:

1. `Files.createTempFile(directory, ".catalog-", ".tmp")` — временный файл
   создаётся в каталоге целевого файла, то есть на той же файловой системе;
2. содержимое пишется в temp, затем вызывается `flush()`, поток закрывается;
3. `Files.move(temp, target, ATOMIC_MOVE, REPLACE_EXISTING)`;
4. если файловая система не поддерживает atomic move, ловится
   `AtomicMoveNotSupportedException` и выполняется обычный
   `Files.move(temp, target, REPLACE_EXISTING)`;
5. в `finally` вызывается `deleteIfExists(temp)`, чтобы temp не остался
   после ошибки.

Даже fallback не публикует половину файла: к моменту move temp уже полностью
записан и закрыт. Atomic move дополнительно гарантирует, что читатель увидит
либо старый, либо новый файл целиком.

Тесты проверяют замену существующего файла, создание отсутствующего,
отсутствие temp-файлов, падение move без порчи цели и fallback через
подставную `MoveOperation`, которая бросает `AtomicMoveNotSupportedException`
на atomic-попытке.

## Задание 7. Частично повреждённый импорт

Класс: [Task7CatalogImport.java](src/main/java/coursehub/practice7/Task7CatalogImport.java).

Выбран вариант **all-or-nothing**. Контракт зафиксирован sealed-типом:

```text
ImportResult = Success(List<Course>) | Failure(List<ImportError>)
```

В `Failure` нет поля с курсами, поэтому «половину каталога» невозможно
случайно получить из результата. Импорт разбирает все строки и собирает все
ошибки с номерами строк (нумерация с 1): пустая строка, нет `|`, пустой код,
пустое название, повтор кода.

Публикация вынесена в `importAndPublish`: `publisher` вызывается только для
`Success`. При `Failure` целевой каталог не меняется. Тесты проверяют полный
успех, список ошибок `2, 3, 4, 5` и то, что при ошибке не публикуется ничего.

## Задание 8. Потоковый импорт большого файла

Класс: [Task8StreamingImport.java](src/main/java/coursehub/practice7/Task8StreamingImport.java).

Файл читается через `BufferedReader.readLine()` в цикле. `readAllLines` не
используется, список всех строк и список всех курсов не накапливаются.
Память: одна строка + буфер reader'а + ограниченный список ошибок.

`Options` задаёт:

- `maxStoredErrors` — сколько ошибок хранить;
- `cancelAfterErrors` — после какого общего числа ошибок прекратить чтение.

`Stats` возвращает счётчики `linesRead`, `validLines`, `invalidLines`,
`totalErrors`, флаг `cancelled` и не более `maxStoredErrors` ошибок.
`cancelled=true` означает, что файл прочитан не до конца и результат нельзя
считать полным.

В потоковом режиме сознательно не проверяются дубликаты кодов: для этого
понадобилось бы хранить все уникальные коды, то есть память порядка размера
файла. Дубликаты проверяет задание 7, где файл и так читается в память.
Тест на 200 000 строк проверяет, что счётчики не зависят от размера файла.

## Задание 9. Версионируемый формат

Класс: [Task9VersionedCatalog.java](src/main/java/coursehub/practice7/Task9VersionedCatalog.java).

Версия 2:

```text
int    magic     = 0x43415456 ("CATV")
int    version   = 2
int    payloadLen
byte[] payload   = int count, (UTF code, UTF title, int level) * count
long   checksum  = CRC32(payload)
```

Версия 1 — старый формат без checksum и без уровня:

```text
int magic
int version = 1
int count
count раз:
  UTF code
  UTF title
```

Чтение:

- версия 1 — читается, уровень курса получает значение по умолчанию `1`;
- версия 2 — сначала читается весь payload, проверяется CRC32, и только
  потом payload разбирается в объекты `Course`;
- неизвестная версия (например, 3) — `UnsupportedVersionException` до
  чтения тела, поэтому будущий формат не интерпретируется как старый;
- несовпадение checksum — `CatalogFormatException`, объекты не публикуются.

`migrateV1ToV2` читает v1, записывает v2 и заново читает целевой файл для
проверки. `readAndPublish` вызывает publisher только после успешной проверки
версии и checksum; тест с испорченным payload проверяет, что список
опубликованных курсов остался пустым.

CRC32 обнаруживает случайное повреждение, но не является криптографической
защитой от подмены. У версии 1 checksum нет, поэтому её повреждение может
быть обнаружено только по структуре (например, обрезанный файл), а не по
контрольной сумме.

## Итог

Все девять заданий закрыты отдельными классами и тестами:

- текст: UTF-8, `Files.readString/writeString`, buffered reader/writer;
- ресурсы: try-with-resources, закрытие в обратном порядке, владелец
  ленивого `Files.lines`;
- бинарные данные: `DataInput/DataOutput`, magic, version, count, CRC32;
- публикация: temp + move, all-or-nothing импорт, checksum до создания
  объектов;
- большой файл: построчное чтение, счётчики, ограниченный список ошибок,
  отмена по порогу.

Тесты: 35 проверок в `Task01Test` … `Task09Test`.
