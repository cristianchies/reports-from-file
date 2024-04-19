package sample;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static final int SALES_DATE_MAX_RESULT = 3;
    public static final int SALES_CLIENTS_MAX_RESULT = 3;

    public static void main(String[] args) {
        // Test
        File input = new File("src/main/resources/sales.txt");
        try {
            Report report = createReport(input);
            System.out.println(report);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Report createReport(File input) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(input)))) {
            String line;

            Map<LocalDate, Set<Client>> salesByDate = new HashMap<>();

            while ((line = br.readLine()) != null) {
                String[] splitStr = line.split(",");

                LocalDate saleDate = LocalDate.parse(splitStr[0], DateTimeFormatter.BASIC_ISO_DATE);
                Integer clientId = Integer.parseInt(splitStr[1]);
                Integer saleCount = Integer.parseInt(splitStr[2]);
                BigDecimal saleValue = new BigDecimal(splitStr[3]);


                Set<Client> clientsInDate = salesByDate.getOrDefault(saleDate, new HashSet<>());
                Optional<Client> clientFound = clientsInDate.stream().filter(it -> clientId.equals(it.getId())).findFirst();
                if (clientFound.isPresent()) {
                    Client cli = clientFound.get();
                    cli.setSaleCount(saleCount);
                    cli.setSaleValue(saleValue);
                } else {
                    clientsInDate.add(new Client(clientId, saleCount, saleValue));
                }
                salesByDate.put(saleDate, clientsInDate);

            }
            return new Report(
                    getMaxSalesDate(salesByDate),
                    getTopSalesDates(salesByDate, SALES_DATE_MAX_RESULT),
                    getTopSalesClients(salesByDate, SALES_CLIENTS_MAX_RESULT));
        } catch (Exception e) {
            System.out.println("Error generating report");
            throw e;
        }

    }

    public static LocalDate getMaxSalesDate(Map<LocalDate, Set<Client>> salesByDate) {
        LocalDate maxDate = null;
        int maxSalesCount = 0;

        for (Map.Entry<LocalDate, Set<Client>> entry : salesByDate.entrySet()) {
            int salesCount = entry.getValue().stream().mapToInt(Client::getSaleCount).sum();
            if (maxDate == null || salesCount > maxSalesCount) {
                maxDate = entry.getKey();
                maxSalesCount = salesCount;
            }
        }

        return maxDate;
    }

    public static List<LocalDate> getTopSalesDates(Map<LocalDate, Set<Client>> salesByDate, int maxResult) {

        return salesByDate.entrySet()
                .stream()
                .sorted((date1, date2) -> {
                    BigDecimal totalSales1 = getTotalSalesValue(salesByDate.get(date1.getKey()));
                    BigDecimal totalSales2 = getTotalSalesValue(salesByDate.get(date2.getKey()));
                    return totalSales2.compareTo(totalSales1);
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
                .reversed()
                .subList(0, Math.min(salesByDate.keySet().size(), maxResult));
    }

    public static BigDecimal getTotalSalesValue(Set<Client> clients) {
        BigDecimal totalSales = BigDecimal.ZERO;
        for (Client client : clients) {
            totalSales = totalSales.add(client.getSaleValue());
        }
        return totalSales;
    }

    public static List<Integer> getTopSalesClients(Map<LocalDate, Set<Client>> salesByDate, int maxResult) {
        // Mapeia os IDs dos clientes para o valor total de suas vendas
        Map<Integer, BigDecimal> clientsSalesValue = new HashMap<>();
        // Mapeia os IDs dos clientes para a quantidade total de suas vendas
        Map<Integer, Integer> clientsSalesCount = new HashMap<>();
        salesByDate.entrySet().stream().flatMap(it -> Stream.of(it.getValue())).forEach(clients -> {
            for (Client client : clients) {
                Integer clientId = client.getId();
                clientsSalesValue.put(
                        clientId,
                        clientsSalesValue.getOrDefault(clientId, BigDecimal.ZERO).add(client.getSaleValue())
                );
                clientsSalesCount.put(
                        clientId,
                        clientsSalesCount.getOrDefault(clientId, 0) + client.getSaleCount()
                );
            }
        });

        // Ordena os IDs dos clientes com base na quantidade total de vendas
        Set<Integer> topClientIdsSalesCount = clientsSalesCount.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(maxResult)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        // Ordena os IDs dos clientes com base no valor total de vendas
        return clientsSalesValue.entrySet().stream().filter(entry -> topClientIdsSalesCount.contains(entry.getKey()))
                .sorted(Map.Entry.<Integer, BigDecimal>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


}
