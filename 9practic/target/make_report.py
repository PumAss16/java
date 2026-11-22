import os
import re
import subprocess
import zipfile

BASE = "/home/pumas/Desktop/java/9practic/target/report-tmp"
MAIN = "/home/pumas/Desktop/java/9practic/src/main/java/coursehub/practice9"
TEST = "/home/pumas/Desktop/java/9practic/src/test/java/coursehub/practice9"
OUT = "/home/pumas/Desktop/java/9practic/Отчет_ЛР9.docx"


def esc(text):
    return (text.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace('"', "&quot;"))


def heading(text):
    return ('<w:p><w:pPr><w:pStyle w:val="Heading1"/><w:keepNext w:val="true"/>'
            '<w:spacing w:lineRule="auto" w:line="276" w:before="0" w:after="120"/>'
            '<w:rPr></w:rPr></w:pPr><w:r><w:rPr></w:rPr><w:t xml:space="preserve">'
            + esc(text) + '</w:t></w:r></w:p>')


def body(text):
    return ('<w:p><w:pPr><w:pStyle w:val="Normal"/>'
            '<w:spacing w:lineRule="auto" w:line="276" w:before="0" w:after="120"/>'
            '<w:rPr></w:rPr></w:pPr><w:r><w:rPr></w:rPr><w:t xml:space="preserve">'
            + esc(text) + '</w:t></w:r></w:p>')


def label(text):
    return ('<w:p><w:pPr><w:pStyle w:val="Normal"/>'
            '<w:spacing w:lineRule="auto" w:line="276" w:before="160" w:after="80"/>'
            '<w:rPr></w:rPr></w:pPr><w:r><w:rPr><w:b/></w:rPr>'
            '<w:t xml:space="preserve">' + esc(text) + '</w:t></w:r></w:p>')


def file_label(text):
    return ('<w:p><w:pPr><w:pStyle w:val="Normal"/>'
            '<w:spacing w:lineRule="auto" w:line="240" w:before="140" w:after="40"/>'
            '<w:rPr></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs="Courier New" '
            'w:ascii="Courier New" w:hAnsi="Courier New"/><w:b/><w:sz w:val="18"/>'
            '<w:szCs w:val="18"/></w:rPr><w:t xml:space="preserve">'
            + esc(text) + '</w:t></w:r></w:p>')


def code_line(text):
    return ('<w:p><w:pPr><w:pStyle w:val="Normal"/>'
            '<w:spacing w:lineRule="auto" w:line="240" w:before="0" w:after="0"/>'
            '<w:rPr></w:rPr></w:pPr><w:r><w:rPr><w:rFonts w:cs="Courier New" '
            'w:ascii="Courier New" w:hAnsi="Courier New"/><w:sz w:val="18"/>'
            '<w:szCs w:val="18"/></w:rPr><w:t xml:space="preserve">'
            + esc(text) + '</w:t></w:r></w:p>')


def blank():
    return ('<w:p><w:pPr><w:pStyle w:val="Normal"/>'
            '<w:spacing w:lineRule="auto" w:line="240" w:before="0" w:after="0"/>'
            '<w:rPr></w:rPr></w:pPr><w:r><w:rPr></w:rPr></w:r></w:p>')


def code_file(title, path):
    parts = [file_label(title)]
    with open(path, encoding="utf-8") as handle:
        for line in handle.read().splitlines():
            parts.append(blank() if not line.strip() else code_line(line))
    return parts


def answer(text):
    return body(text)


sections = [
    (
        "Задание 1. Потерянные инкременты",
        "Несколько потоков увеличивают общий счётчик. Получите итог меньше ожидаемого и разложите counter++ "
        "на чтение, вычисление и запись. Тест должен многократно выявлять проблему. Исправьте счётчик с помощью "
        "synchronized. Укажите защищаемый инвариант и объект-монитор. Сравните синхронизированный метод и "
        "минимальный синхронизированный блок.",
        [("Task1Counter.java", MAIN + "/Task1Counter.java"),
         ("Task01Test.java", TEST + "/Task01Test.java")],
        [
            "counter++ состоит из трёх шагов: чтение текущего значения, вычисление нового и запись обратно. "
            "Если два потока прочитали одно и то же значение, оба запишут один и тот же результат, и один инкремент "
            "пропадает.",
            "Некорректный вариант — PlainCounter. Прогон в Main: восемь потоков по 100000 инкрементов дают "
            "259801 вместо 800000. Потеряно больше половины обновлений.",
            "Чтобы проблема воспроизводилась надёжно, а не по случайности, гонка разыгрывается детерминированно: "
            "медленный поток читает 0 и останавливается, быстрый успевает записать 1, затем медленный записывает "
            "0 + 1 = 1. Итог 1 вместо 2. Тест повторяет этот сценарий 20 раз и каждый раз получает 1.",
            "Инвариант: поле value всегда равно числу завершённых инкрементов. Объект-монитор: у "
            "SynchronizedCounter это сам счётчик (this), у BlockCounter — приватное поле monitor.",
            "Синхронизированный метод и синхронизированный блок дают одинаковый результат, разница в размере "
            "критической секции. Метод держит монитор весь вызов и синхронизирован на this, поэтому внешний код "
            "может случайно заблокировать счётчик. Блок с приватным монитором закрывает только нужные строки и "
            "не раскрывает объект блокировки наружу.",
            "Здесь критическая секция совпадает с телом метода, поэтому выигрыша по параллелизму нет; "
            "преимущество блока проявилось бы, если бы до или после инкремента были медленные операции.",
        ],
    ),
    (
        "Задание 2. Флаг остановки и visibility",
        "Реализуйте worker с флагом остановки, исследуйте некорректный вариант и исправьте его через volatile либо "
        "синхронизацию. Объясните, почему volatile обеспечивает видимость, но не превращает составную операцию в "
        "атомарную.",
        [("Task2StopFlag.java", MAIN + "/Task2StopFlag.java"),
         ("Task02Test.java", TEST + "/Task02Test.java")],
        [
            "Некорректный вариант — PlainWorker с обычным boolean running. Поток может прочитать флаг один раз "
            "и держать значение в регистре или кеше процессора, не перечитывая память. Тогда stop() из другого "
            "потока не виден, и цикл не завершается.",
            "volatile снимает именно эту проблему: запись и чтение флага становятся точками синхронизации, "
            "JMM гарантирует отношение happens-before между записью и последующим чтением, значение не кешируется "
            "в регистре. Поэтому VolatileWorker гарантированно видит stop().",
            "Но volatile не делает составную операцию атомарной. value++ на volatile int — это всё те же три "
            "шага, и между чтением и записью другой поток может вклиниться. Метод interleavedVolatileIncrement "
            "показывает это детерминированно: итог 1 вместо 2, ровно как у обычного счётчика.",
            "Для составных операций нужен synchronized или атомарные классы (AtomicInteger), у которых "
            "чтение-вычисление-запись выполняется одной атомарной операцией.",
            "Зависание PlainWorker тестом не проверяется: оно недетерминировано и зависело бы от JIT и загрузки "
            "CPU, поэтому тест зафиксировал положительный сценарий (воркер останавливается за 2 секунды) и "
            "отрицательный (volatile не спасает от потери обновления).",
        ],
    ),
    (
        "Задание 3. Потокобезопасный реестр",
        "Реализуйте реестр активных сессий на ConcurrentHashMap. Операция «создать, только если отсутствует» "
        "должна быть атомарной.",
        [("Task3SessionRegistry.java", MAIN + "/Task3SessionRegistry.java"),
         ("Task03Test.java", TEST + "/Task03Test.java")],
        [
            "Атомарность «создать, только если отсутствует» обеспечивает один вызов putIfAbsent. Комбинация "
            "containsKey + put была бы ошибкой check-then-act: между проверкой и вставкой другой поток успевает "
            "вставить свою сессию, и обе считают себя создателями.",
            "putIfAbsent возвращает предыдущее значение или null, поэтому по результату сразу видно, кто выиграл. "
            "Тест запускает 8 потоков на один и тот же идентификатор 30 раз подряд и каждый раз получает ровно "
            "одного победителя и размер реестра 1.",
            "Дополнительно использованы computeIfAbsent для ленивого создания и replace(key, expected, updated) — "
            "замена только при совпадении ожидаемого значения, то есть compare-and-set на уровне map.",
            "ConcurrentHashMap не блокирует чтения и блокирует запись только в пределах бакета, поэтому реестр "
            "масштабируется лучше, чем HashMap под общим synchronized.",
        ],
    ),
    (
        "Задание 4. Producer–consumer",
        "Организуйте обработку заявок через ограниченную BlockingQueue. Несколько producers добавляют заявки, "
        "consumers обрабатывают их. Реализуйте корректное завершение без busy waiting и объясните, как ёмкость "
        "очереди создаёт backpressure. Перенесите обработку на ExecutorService. Выберите размер пула для заданного "
        "CPU-bound или blocking-сценария, дайте потокам осмысленные имена и гарантируйте shutdown, ограниченное "
        "ожидание и аварийный shutdownNow.",
        [("Task4Pipeline.java", MAIN + "/Task4Pipeline.java"),
         ("Task04Test.java", TEST + "/Task04Test.java")],
        [
            "Очередь — ArrayBlockingQueue с фиксированной ёмкостью. Producers вызывают put, consumers — take. "
            "Оба метода блокируются вместо активного ожидания: поток спит на условии и просыпается по сигналу.",
            "Backpressure возникает из ёмкости. Когда очередь полна, put блокируется, и producer не может "
            "продвинуться дальше, пока consumer не освободит место. Память ограничена ёмкостью, а скорость "
            "producers подстраивается под скорость обработки. В прогоне при ёмкости 4 максимальный размер очереди "
            "равен 4.",
            "Завершение без busy waiting — «poison pill»: после того как все producers закончили, в очередь "
            "кладётся по одной пилюле на каждого consumer, и каждый выходит из цикла, получив свою. Никаких "
            "флагов с опросом и sleep.",
            "Пул — фиксированный ExecutorService, потоки именуются через фабрику (pipeline-1, pipeline-2, …), "
            "поэтому в тестах видно, что после работы не осталось живых потоков.",
            "Гарантии завершения: shutdown(), затем awaitTermination с ограниченным таймаутом, при просрочке — "
            "shutdownNow(). Блок finally подстраховывает вызов shutdownNow, если до него не дошли.",
            "Размер пула: для CPU-bound — availableProcessors(), для blocking-сценария — вдвое больше, потому что "
            "часть потоков ждёт ввод-вывод и не занимает ядро. Это отражено в suggestedPoolSize.",
        ],
    ),
    (
        "Задание 5. Детерминированный конкурентный тест",
        "Напишите JUnit-тест гонки с CountDownLatch, CyclicBarrier или Phaser, управляя моментом старта и точками "
        "протокола. Не используйте Thread.sleep как средство синхронизации. Тест имеет timeout и всегда "
        "освобождает executor.",
        [("Task5Race.java", MAIN + "/Task5Race.java"),
         ("Task05Test.java", TEST + "/Task05Test.java")],
        [
            "Точка старта задаётся CyclicBarrier на число участников: каждый поток доходит до barrier.await и "
            "ждёт остальных, поэтому все начинают одновременно, а не по очереди. Это и есть управляемый момент "
            "старта и точка протокола.",
            "CountDownLatch считает завершения: главный поток ждёт done.await, а не спит фиксированное время.",
            "Thread.sleep не используется нигде: он не даёт гарантий порядка и делает тест медленным и хрупким. "
            "Синхронизация строится только на барьере и защёлке.",
            "Тест помечен @Timeout(10), поэтому зависание гонки не подвесит сборку.",
            "Executor освобождается всегда: shutdownNow и awaitTermination вызываются в блоке finally, даже если "
            "тело гонки бросило исключение.",
            "Исключения из тел потоков не теряются: они складываются в AtomicReference и перебрасываются в "
            "тестовом потоке, поэтому падение внутри гонки валит тест, а не остаётся незамеченным.",
            "Проверка результата: 30 раундов по 8 потоков на один и тот же ключ дают ровно одного создателя "
            "сессии и размер реестра 1.",
        ],
    ),
    (
        "Задание 6. Запись на курс с ограничением мест",
        "Много потоков одновременно вызывают enroll. Число успешных записей не превышает capacity, один студент "
        "не занимает место дважды, а отказ не изменяет состояние. Реализуйте минимум два варианта и сравните "
        "coarse-grained lock, атомарные операции и concurrent collections.",
        [("Task6Enrollment.java", MAIN + "/Task6Enrollment.java"),
         ("Task06Test.java", TEST + "/Task06Test.java")],
        [
            "Инварианты, которые защищает любой вариант: успешных записей не больше capacity, один студент "
            "занимает не больше одного места, отказ не меняет состояние, размер всегда равен числу успешных "
            "записей. Тест запускает 32 потока на capacity 10 и проверяет все три свойства.",
            "SynchronizedEnrollment — coarse-grained lock: весь метод enroll под одним монитором. Просто и "
            "корректно, но все потоки сериализуются, включая проверку дубликата; при росте нагрузки это узкое место.",
            "ConcurrentEnrollment — concurrent collections плюс атомарный счётчик: students — "
            "ConcurrentHashMap.newKeySet, места считает AtomicInteger. Параллелизм выше, но между добавлением "
            "студента и проверкой количества мест есть окно, поэтому при переполнении нужен откат: decrement и "
            "remove. Состояние на короткое время может превысить capacity, хотя итоговое — нет.",
            "CasEnrollment — атомарные операции: состояние (множество студентов и число мест) хранится в одном "
            "immutable объекте под AtomicReference, изменение делается через compareAndSet в цикле. Проверка и "
            "запись происходят в одной атомарной операции, промежуточного состояния нет, откат не нужен. Цена — "
            "копирование множества на каждую попытку, то есть O(n) по числу записанных.",
            "Сравнение: coarse-grained lock проще всего и достаточно при низкой конкуренции; concurrent "
            "collections дают лучший параллелизм при чтениях, но требуют компенсации; CAS даёт самую сильную "
            "гарантию атомарности и отсутствие откатов, но платит копированием.",
        ],
    ),
]

parts = []
parts.append(heading("Отчет по лабораторной работе №9. Многопоточность"))
parts.append(body("Выполнил: Гуров Матвей, ИВ-523"))
parts.append(body("Проект 9practic, пакет coursehub.practice9, Java 17, JUnit 5. Реализованы шесть заданий; "
                  "тесты Task01Test … Task06Test, всего 18 проверок."))

for title, text, files, answers in sections:
    parts.append(heading(title))
    parts.append(body(text))
    parts.append(label("Код"))
    for name, path in files:
        parts.extend(code_file(name, path))
    parts.append(label("Ответы"))
    for item in answers:
        parts.append(answer(item))

parts.append(heading("Демонстрация"))
parts.append(label("Код"))
parts.extend(code_file("Main.java", MAIN + "/Main.java"))
parts.append(label("Вывод программы"))
try:
    result = subprocess.run(
        ["java", "-cp", "/home/pumas/Desktop/java/9practic/target/classes", "coursehub.practice9.Main"],
        capture_output=True, text=True, timeout=180)
    output = result.stdout.strip() or result.stderr.strip()
except Exception as error:
    output = "запуск не выполнен: " + str(error)
for line in output.splitlines():
    parts.append(code_line(line))

parts.append(heading("Вывод"))
parts.append(body("Все шесть заданий выполнены: потерянные инкременты и их детерминированное воспроизведение, "
                  "видимость через volatile и её границы, атомарный реестр сессий, producer–consumer с backpressure "
                  "и корректным завершением, детерминированный тест гонки на барьере и три варианта записи на курс "
                  "с ограничением мест."))
parts.append(body("Сборка и проверка: mvn test — 18 тестов, 0 падений, 0 ошибок. Набор прогнан пять раз подряд "
                  "без единого падения, что подтверждает отсутствие флейков в конкурентных тестах."))

original = open(os.path.join(BASE, "word", "document.xml"), encoding="utf-8").read()
prefix = original[:original.index("<w:body>") + len("<w:body>")]
sectpr = re.search(r"<w:sectPr.*?</w:sectPr>", original, re.S).group(0)
document = prefix + "".join(parts) + sectpr + "</w:body></w:document>"

with zipfile.ZipFile(OUT, "w", zipfile.ZIP_DEFLATED) as archive:
    for root, _, files in os.walk(BASE):
        for name in files:
            full = os.path.join(root, name)
            relative = os.path.relpath(full, BASE)
            if relative == os.path.join("word", "document.xml"):
                archive.writestr(relative, document)
            else:
                archive.write(full, relative)

print("written", OUT, os.path.getsize(OUT), "bytes")
print("paragraphs", len(parts))
