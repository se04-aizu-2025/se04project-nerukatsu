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

            System.out.println("\nSelect array type:");
            DataGenerator.ArrayType[] arrayTypes = DataGenerator.getAvailableTypes();
            for (int i = 0; i < arrayTypes.length; i++) {
                System.out.println((i + 1) + ". " + arrayTypes[i].getDisplayName());
            }
            System.out.print("Enter your choice: ");
            int typeChoice = scanner.nextInt();
            if (typeChoice < 1 || typeChoice > arrayTypes.length) {
                System.out.println("Invalid choice. Using Random.");
                typeChoice = 1;
            }
            DataGenerator.ArrayType arrayType = arrayTypes[typeChoice - 1];

            System.out.print("Enter the size of the array to sort: ");
            int size = scanner.nextInt();

            int[] originalArray = dataGenerator.generateArray(arrayType, size);
            int[] array = originalArray.clone();

            System.out.println("\nBefore sorting (" + arrayType.getDisplayName() + "):");
            printArray(array);

            selectedAlgorithm.sort(array);

            System.out.println("After sorting using " + selectedAlgorithm.getName() + ":");
            printArray(array);

            boolean valid = TestEngine.isValidSort(originalArray, array);
            System.out.println("Is valid sort: " + valid);
            System.out.println(TestEngine.generateTestReport(originalArray, array));
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