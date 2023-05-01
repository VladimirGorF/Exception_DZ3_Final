// Напишите приложение, которое будет запрашивать у пользователя следующие данные в произвольном
// порядке, разделенные пробелом:
// Фамилия Имя Отчество дата рождения номер телефона пол
// Форматы данных:
// фамилия, имя, отчество - строки
// дата_рождения - строка формата dd.mm.yyyy
// номер_телефона - целое беззнаковое число без форматирования
// пол - символ латиницей f или m.
// Приложение должно проверить введенные данные по количеству. Если количество не совпадает с требуемым,
//  вернуть код ошибки, обработать его и показать пользователю сообщение, что он ввел меньше и больше
//  данных, чем требуется.
// Приложение должно попытаться распарсить полученные значения и выделить из них требуемые параметры.
//  Если форматы данных не совпадают, нужно бросить исключение, соответствующее типу проблемы.
// Можно использовать встроенные типы java и создать свои. Исключение должно быть корректно обработано,
//  пользователю выведено сообщение с информацией, что именно неверно.
// Если всё введено и обработано верно, должен создаться файл с названием, равным фамилии, в него в
// одну строку должны записаться полученные данные, вида
// <Фамилия><Имя><Отчество><датарождения> <номертелефона><пол>
// Однофамильцы должны записаться в один и тот же файл, в отдельные строки.
// Не забудьте закрыть соединение с файлом.
// При возникновении проблемы с чтением-записью в файл, исключение должно быть корректно обработано,
// пользователь должен увидеть стектрейс ошибки.
//Проверочная строка " Смирнов Петр Федорович 24.05.1981 89261838059 m ";

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;
import java.util.Scanner;

public class Dz3 {
    public static void main(String[] args) {

        informBase();
    }

    public static void informBase() {

        while (true) {

            System.out.println("Введите данные через пробел:  Фамилия Имя Отчество дата рождения номер телефона пол." +
                    " Имя нужно вводить не ранее фамилии и не позже отчества, а отчество позже имени и фамилии. Между " +
                    "ними можно вводить остальные данные как угодно.");
            Scanner scanner = new Scanner(System.in);
            String str = scanner.nextLine();  // получили строку
            String[] array = str.split(" ");

            // проверка на количество введенных данных
            int codeExc = 0;
            try {
                if (array[0].equals("")) {
                    codeExc = 1;
                    System.out.printf("Код ошибки: %d\n", codeExc);
                    throw new quantityException("");
                }
            } catch (quantityException e) {
                System.out.println("Нельзя начинать ввод с пустого символа!");
                continue;
            }

            try {
                if (array.length < 6) {
                    codeExc = 2;
                    System.out.printf("Код ошибки: %d\n", codeExc);
                    throw new quantityException("");
                }
            } catch (quantityException e) {
                System.out.println("Вы ввели меньше данных, чем требуется!");
                continue;
            }

            try {
                if (array.length > 6) {
                    codeExc = 3;
                    System.out.printf("Код ошибки: %d\n", codeExc);
                    throw new quantityException("");
                }
            } catch (quantityException e) {
                System.out.println("Вы ввели больше данных, чем требуется!");
                continue;
            }

            int checkDate = 0; // счетчик для даты
            int checkSex = 0; // счетчик для пола
            int checkTel = 0; // счетчик для телефона
            String[] arrayNew = new String[6]; // массив для данных в нужном порядке
            int i = 0;

            for (String item : array) {    // по циклу проверяем массив

                // блок проверки телефона
                try {
                    if (item.length() == 11) {
                        Long tel1 = Long.valueOf(item);  // проверяем совпадает тип данных или уйдет в ошибку с этого места
                        arrayNew[4] = item;
                        checkTel += 1;
                    }
                } catch (NumberFormatException ignored) {
                }

                // блок провеки наличия пола
                if (item.equals("f") || item.equals("m")) {
                    checkSex += 1;
                    arrayNew[5] = item;
                }

                // блок проверки даты
                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    Date date1 = formatter.parse(item); // просто проверяем спарсит или уйдет в ошибку с этого места
                    arrayNew[3] = item;
                    checkDate += 1;
                } catch (ParseException ignored) {
                }

                // записываем в новый массив сначала данные, что не прошли проверки выше: это наши 3 элемента ФИО
                arrayNew[i] = item;
                i++;
            }

            // выбосы ошибок
            try {
                if (checkDate == 0) throw new RuntimeException();
            } catch (RuntimeException e) {
                System.out.println("Дата рождения не введна или введена не коректно!");
                continue;
            }
            try {
                if (checkSex == 0) throw new RuntimeException();
            } catch (RuntimeException e) {
                System.out.println("Необходимо вводить пол в формате 'f' или 'm'.");
                continue;
            }
            try {
                if (checkTel == 0) throw new RuntimeException();
            } catch (RuntimeException e) {
                System.out.println("Введите номер телефона 11-значный!");
                continue;
            }

            //  вызываем функцию записи в файл
            try {
                wrFile(arrayNew);
            } catch (IOException | RuntimeException e) {
                System.out.println("Ошибка записи в файл");
                System.out.println(e.toString());
                e.printStackTrace();
                continue;
            }

            //  если нигде не вылетела, то закрыли сканер и цикл
            scanner.close();
            System.out.println("Программа отработала успешно, проверяйте файл новой записью!");
            break;
        }
    }

    public static void wrFile(String[] arrayNew) throws IOException, RuntimeException {

        try (FileWriter writer = new FileWriter(arrayNew[0], true)) {  // реализуем блок try with resources с автоматическим закрытием записи

            for (String item : arrayNew) {
                writer.append('<');
                writer.write(item);
                writer.append('>');
            }
            writer.append('\n');
            writer.flush();
        }
    }
}



