package core;

import collection.*;

import java.util.Scanner;
import java.util.Stack;

public class Creator {
    public static Product createProduct(Product product, boolean isInteractive) {
        Stack<String> errors = new Stack<>();
        String name;
        while (true) {
            if (isInteractive) {
                System.out.println("Введите название продукта:");
                Scanner in = new Scanner(System.in);
                name = in.nextLine();
            } else {
                name = product.getName();
            }
            if (name == null || name.matches("\\s*")) {
                errors.push("Неправильный ввод названия продукта! Оно не может быть пустой строкой.");
                if (isInteractive) {
                    System.out.println(errors.pop());
                    continue;
                }
            }
            break;
        }
        Double x = null;
        while (true) {
            try {
                if (isInteractive) {
                    System.out.println("Введите координату x (дробное число):");
                    Scanner in = new Scanner(System.in);
                    x = Double.parseDouble(in.nextLine());
                } else {
                    if (product.getCoordinates() == null || (x = product.getCoordinates().getX()) == null) {
                        throw new NumberFormatException();
                    }
                }
            } catch (NumberFormatException e) {
                errors.push("Неправильный ввод координаты x! Требуемый формат: дробное число.");
                if (isInteractive) {
                    System.out.println(errors.pop());
                    continue;
                }
            }
            break;
        }
        Long y = null;
        while (true) {
            try {
                if (isInteractive) {
                    System.out.println("Введите координату y (целое число):");
                    Scanner in = new Scanner(System.in);
                    y = Long.parseLong(in.nextLine());
                } else {
                    if (product.getCoordinates() == null || (y = product.getCoordinates().getY()) == null) {
                        throw new NumberFormatException();
                    }
                }
            } catch (NumberFormatException e) {
                errors.push("Неправильный ввод координаты y! Требуемый формат: целое число.");
                if (isInteractive) {
                    System.out.println(errors.pop());
                    continue;
                }
            }
            break;
        }
        float price = -1;
        while (true) {
            try {
                if (isInteractive) {
                    System.out.println("Введите цену продукта (положительное дробное число):");
                    Scanner in = new Scanner(System.in);
                    price = Float.parseFloat(in.nextLine());
                } else {
                    price = product.getPrice();
                }
                if (price <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                errors.push("Неправильный ввод цены продукта! Требуемый формат: положительное дробное число.");
                if (isInteractive) {
                    System.out.println(errors.pop());
                    continue;
                }
            }
            break;
        }
        String partNumber;
        while (true) {
            if (isInteractive) {
                System.out.println("Введите код производителя (#xxxxxx, где x - цифры):");
                Scanner in = new Scanner(System.in);
                partNumber = in.nextLine();
            } else {
                partNumber = product.getPartNumber();
            }
            if (partNumber == null || !partNumber.matches("#\\d{6}")) {
                errors.push("Неправильный ввод кода производителя! Требуемый формат: #xxxxxx, где x - цифры.");
                if (isInteractive) {
                    System.out.println(errors.pop());
                    continue;
                }
            }
            break;
        }
        float manufactureCost = -1;
        while (true) {
            try {
                if (isInteractive) {
                    System.out.println("Введите цену производства продукта (положительное дробное число):");
                    Scanner in = new Scanner(System.in);
                    manufactureCost = Float.parseFloat(in.nextLine());
                } else {
                    manufactureCost = product.getManufactureCost();
                }
                if (manufactureCost <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                errors.push("Неправильный ввод цены производства продукта! Требуемый формат: положительное дробное число.");
                if (isInteractive) {
                    System.out.println(errors.pop());
                    continue;
                }
            }
            break;
        }
        UnitOfMeasure unitOfMeasure = null;
        while (true) {
            try {
                if (isInteractive) {
                    System.out.println("Введите единицу измерения (" + UnitOfMeasure.valueList() + "):");
                    Scanner in = new Scanner(System.in);
                    unitOfMeasure = UnitOfMeasure.fromString(in.nextLine());
                } else {
                    unitOfMeasure = product.getUnitOfMeasure();
                    if (unitOfMeasure == null) {
                        throw new IllegalArgumentException();
                    }
                }
            } catch (IllegalArgumentException e) {
                errors.push("Неправильный ввод единиц измерения! Возможные варианты ввода: " + UnitOfMeasure.valueList() + ".");
                if (isInteractive) {
                    System.out.println(errors.pop());
                    continue;
                }
            }
            break;
        }
        String manufacturerName = null;
        while (true) {
            if (isInteractive) {
                System.out.println("Введите название компании-производителя:");
                Scanner in = new Scanner(System.in);
                manufacturerName = in.nextLine();
            } else {
                if (product.getManufacturer() != null) {
                    manufacturerName = product.getManufacturer().getName();
                }
            }
            if (manufacturerName == null || manufacturerName.matches("\\s*")) {
                errors.push("Неправильный ввод названия компании-производителя! Оно не может быть пустой строкой.");
                if (isInteractive) {
                    System.out.println(errors.pop());
                    continue;
                }
            }
            break;
        }
        Long annualTurnover = null;
        while (true) {
            try {
                if (isInteractive) {
                    System.out.println("Введите годовой оборот компании-производителя (пустая строка или целое положительное число):");
                    Scanner in = new Scanner(System.in);
                    String s = in.nextLine();
                    if (!s.matches("\\s*")) {
                        annualTurnover = Long.parseLong(s);
                    }
                } else {
                    if (product.getManufacturer() != null) {
                        annualTurnover = product.getManufacturer().getAnnualTurnover();
                    } else {
                        throw new NumberFormatException();
                    }
                }
                if (annualTurnover != null && annualTurnover <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                errors.push("Неправильный ввод ежегодного оборота компании-производителя! Требуемый формат: пустая строка или целое положительное число.");
                if (isInteractive) {
                    System.out.println(errors.pop());
                    continue;
                }
            }
            break;
        }
        Long employeesCount = null;
        while (true) {
            try {
                if (isInteractive) {
                    System.out.println("Введите количество сотрудников компании-производителя (пустая строка или целое положительное число):");
                    Scanner in = new Scanner(System.in);
                    String s = in.nextLine();
                    if (!s.matches("\\s*")) {
                        employeesCount = Long.parseLong(s);
                    }
                } else {
                    if (product.getManufacturer() != null) {
                        employeesCount = product.getManufacturer().getEmployeesCount();
                    } else {
                        throw new NumberFormatException();
                    }
                }
                if (employeesCount != null && employeesCount <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                errors.push("Неправильный ввод количества сотрудников компании-производителя! Требуемый формат: пустая строка или целое положительное число.");
                if (isInteractive) {
                    System.out.println(errors.pop());
                    continue;
                }
            }
            break;
        }
        OrganizationType type = null;
        while (true) {
            try {
                if (isInteractive) {
                    System.out.println("Введите тип компании-производителя (пустая строка, " + OrganizationType.valueList() + "):");
                    Scanner in = new Scanner(System.in);
                    String s = in.nextLine();
                    if (!s.equals("")) {
                        type = OrganizationType.fromString(s);
                    }
                } else {
                    if (product.getManufacturer() == null) {
                        throw new IllegalArgumentException();
                    } else {
                        type = product.getManufacturer().getType();
                    }
                }
            } catch (IllegalArgumentException e) {
                errors.push("Неправильный ввод типа компании-производителя! Возможные варианты ввода: пустая строка, " + OrganizationType.valueList() + ".");
                if (isInteractive) {
                    System.out.println(errors.pop());
                    continue;
                }
            }
            break;
        }
        if (errors.isEmpty()) {
            Organization manufacturer = new Organization(manufacturerName, annualTurnover, employeesCount, type);
            return new Product(name, new Coordinates(x, y), price, partNumber, manufactureCost, unitOfMeasure, manufacturer);
        } else {
            System.out.println("Возникли следующие ошибки:");
            errors.forEach(System.out::println);
        }
        return null;
    }

    public static Organization createManufacturer(Organization manufacturer, boolean isInteractive) {
        Stack<String> errors = new Stack<>();
        String name;
        while (true) {
            if (isInteractive) {
                System.out.println("Введите название компании-производителя:");
                Scanner in = new Scanner(System.in);
                name = in.nextLine();
            } else {
                name = manufacturer.getName();
            }
            if (name == null || name.matches("\\s*")) {
                errors.push("Неправильный ввод названия компании-производителя! Оно не может быть пустой строкой.");
                if (isInteractive) {
                    System.out.println(errors.pop());
                    continue;
                }
            }
            break;
        }
        Long annualTurnover = null;
        while (true) {
            try {
                if (isInteractive) {
                    System.out.println("Введите годовой оборот компании-производителя (пустая строка или целое положительное число):");
                    Scanner in = new Scanner(System.in);
                    String s = in.nextLine();
                    if (!s.matches("\\s*")) {
                        annualTurnover = Long.parseLong(s);
                    }
                } else {
                    annualTurnover = manufacturer.getAnnualTurnover();
                }
                if (annualTurnover != null && annualTurnover <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                errors.push("Неправильный ввод ежегодного оборота компании-производителя! Требуемый формат: пустая строка или целое положительное число.");
                if (isInteractive) {
                    System.out.println(errors.pop());
                    continue;
                }
            }
            break;
        }
        Long employeesCount = null;
        while (true) {
            try {
                if (isInteractive) {
                    System.out.println("Введите количество сотрудников компании-производителя (пустая строка или целое положительное число):");
                    Scanner in = new Scanner(System.in);
                    String s = in.nextLine();
                    if (!s.matches("\\s*")) {
                        employeesCount = Long.parseLong(s);
                    }
                } else {
                    employeesCount = manufacturer.getEmployeesCount();
                }
                if (employeesCount != null && employeesCount <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                errors.push("Неправильный ввод количества сотрудников компании-производителя! Требуемый формат: пустая строка или целое положительное число.");
                if (isInteractive) {
                    System.out.println(errors.pop());
                    continue;
                }
            }
            break;
        }
        OrganizationType type = null;
        while (true) {
            try {
                if (isInteractive) {
                    System.out.println("Введите тип компании-производителя (пустая строка, " + OrganizationType.valueList() + "):");
                    Scanner in = new Scanner(System.in);
                    String s = in.nextLine();
                    if (!s.equals("")) {
                        type = OrganizationType.fromString(s);
                    }
                } else {
                    type = manufacturer.getType();
                }
            } catch (IllegalArgumentException e) {
                errors.push("Неправильный ввод типа компании-производителя! Возможные варианты ввода: пустая строка, " + OrganizationType.valueList() + ".");
                if (isInteractive) {
                    System.out.println(errors.pop());
                    continue;
                }
            }
            break;
        }
        if (errors.isEmpty()) {
            return new Organization(name, annualTurnover, employeesCount, type);
        } else {
            System.out.println("Возникли следующие ошибки:");
            errors.forEach(System.out::println);
        }
        return null;
    }
}
