package sample;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class Report {
    public static final DateTimeFormatter OUT_PUT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final int OUT_PUT_ID_SIZE = 4;
    public static final String LINE_BREAK = "\n";
    public static final String DELIMITER = ",";
    private LocalDate mostSalesDate;
    private List<LocalDate> biggestSalesDates;
    private List<Integer> mostSalesClients;

    public Report(LocalDate mostSalesDate, List<LocalDate> biggestSalesDates, List<Integer> mostSalesClients) {
        this.mostSalesDate = mostSalesDate;
        this.biggestSalesDates = biggestSalesDates;
        this.mostSalesClients = mostSalesClients;
    }

    public String padLeftZeros(String inputString) {
        if (inputString.length() >= OUT_PUT_ID_SIZE) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < OUT_PUT_ID_SIZE - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);

        return sb.toString();
    }

    @Override
    public String toString() {
        String firstLine = mostSalesDate.format(OUT_PUT_DATE_FORMAT);
        String secondLine = biggestSalesDates.stream().map(it -> it.format(OUT_PUT_DATE_FORMAT)).collect(Collectors.joining(DELIMITER));
        String thirdLine = mostSalesClients.stream().map(it -> padLeftZeros(it + "")).collect(Collectors.joining(DELIMITER));
        return firstLine.concat(LINE_BREAK).concat(secondLine).concat(LINE_BREAK).concat(thirdLine);
    }
}
