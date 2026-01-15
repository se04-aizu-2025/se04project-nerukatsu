package ui.cui;

import algorithm.BubbleSort;
import algorithm.QuickSort;
import algorithm.ShakerSort;
import algorithm.SortAlgorithm;
import data.DataGenerator;
import test.TestEngine;
import java.util.Scanner;

public class CliApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DataGenerator dataGenerator = new DataGenerator();

        SortAlgorithm[] algorithms = {
            new BubbleSort(),
            new QuickSort(),
            new ShakerSort()
        };

        while (true) {
            System.out.println("Select a sort algorithm:");
            for (int i = 0; i < algorithms.length; i++) {
                System.out.println((i + 1) + ". " + algorithms[i].getName());
            }
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            if (choice == 0) {
                break;
            }
            if (choice < 1 || choice > algorithms.length) {
                System.out.println("Invalid choice. Please try again.");
                continue;
            }

            SortAlgorithm selectedAlgorithm = algorithms[choice - 1];

            System.out.print("Enter the size of the array to sort: ");
            int size = scanner.nextInt();

            int[] array = dataGenerator.generateRandomArray(size);

            System.out.println("Before sorting:");
            printArray(array);

            selectedAlgorithm.sort(array);

            System.out.println("After sorting using " + selectedAlgorithm.getName() + ":");
            printArray(array);

            boolean sorted = TestEngine.isSorted(array);
            System.out.println("Is sorted: " + sorted);
        }

        scanner.close();
    }

    private static void printArray(int[] array) {
        for (int num : array) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
}