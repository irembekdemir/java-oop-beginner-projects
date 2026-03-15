public class Student {

    private String name;
    private double midterm;
    private double finalExam;

    //Constructor -> parametreden gelen degeri object icine koyar
    public Student (String name, double midterm, double finalExam) {
        this.name = name;
        this.midterm = midterm;
        this.finalExam = finalExam;
    }
    //Accessors (getters) -> to read the private variables
    public String getName() { return name;}

    public double getMidterm() {return midterm;}

    public double getFinalExam() {return finalExam;}

    //Mutators (setters) -> to change the private variables
    public void setMidterm(double midterm) {this.midterm = midterm;}

    public void setFinalExam(double finalExam) {this.finalExam = finalExam;}

    public double calculateAverage() {return midterm*0.4 + finalExam*0.6;}

    public static String getLetterGrade(double avg) { // ->objecte bagli degil

        int grade = (int) avg / 10;

        switch (grade) {
            case 10:
                return "A+";
            case 9:
                return "A";

            case 8:
                return "B+";

            case 7:
                return "B";

            case 6:
                return "C";

            case 5:
                return "D";

            default:
                return "F";
        }
    }
    public static void main (String [] args) {
        if (args.length<3) {
            System.out.println("Usage: java Student <name> <midterm> <final>");
                    return;
        }

        String name = args[0];
        int midterm = Integer.parseInt(args[1]);  //terminal inputs are strings as default
        int finalExam = Integer.parseInt(args[2]);

        Student s1 = new Student(name, midterm, finalExam); //object olusturur and calls the constructor

        double avg = s1.calculateAverage();

        System.out.println(s1.getName() + "'s GPA: " + s1.calculateAverage() + "/100.0");

        String letter = getLetterGrade(avg);

        System.out.println("Letter grade: " + letter);

        }
    }