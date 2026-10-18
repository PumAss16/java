package coursehub.practice6.strings;

import java.util.List;

class ReportBuilder {

    static String withPlus(List<String> lines) {
        String s = "";
        for (String line : lines) {
            s = s + line + "\n";
        }
        return s;
    }

    static String withBuilder(List<String> lines) {
        StringBuilder s = new StringBuilder();
        for (String line : lines) {
            s.append(line).append("\n");
        }
        return s.toString();
    }
}
