import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;

// Parent class
class Player {
    int secret_code;
    
    Player(int secret_code) {
        this.secret_code = secret_code;
    }

    int GetSecretCode() {
        return this.secret_code;
    }

    // Method to validate input from user is valid code
    boolean isValidCode(int num) {
        // Check user secret code is four digits
        if (num < 1000 || num > 9999) {
            return false;
        }

        // check individual digit in the secret code is distinct
        Set<Integer> numSet = new HashSet<>();
        while (num > 0) {
            int digit = num % 10;
            //check digit is not zero
            if(digit == 0){
                return false;
            }
            if (!numSet.add(digit)) {
                // digit already exists
                return false;
            }
            num = num/10;
        }

        // digits are distinct
        return true;
    }

    
}

// Child class Computer player inheriting from Player
class Computer extends Player {
    Level level;
    List<Integer> guesses;

    Computer() {
        super(1234);
        this.guesses = new ArrayList<>();
        
    }

    void SetLevel (Level level) {
        this.level = level;
        if(level == Level.HARD){
            for(int i =1000; i < 9999; i++){
                if(this.isValidCode(i)){
                    this.guesses.add(i);
                }
            }
        }
    }

    //Method for computer to generate random valid secret key
    int GenerateSecretCode(){
        List<Integer> num_array = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            num_array.add(i);
        }

        Collections.shuffle(num_array);
        int num = num_array.get(0) + num_array.get(1)*10 + num_array.get(2)*100 + num_array.get(3)*1000;
        return num;
    }

    void SetSecretCode(){
        this.secret_code = this.GenerateSecretCode();
    }

    void UpdateGuesses(int target, int bulls, int cows){
        List<Integer> tempArr = new ArrayList<>();
        Calculations calc = new Calculations();
        int len = this.guesses.size();
        int input;
        int b;
        int c;
        for(int i =0; i < len; i++){
            input = this.guesses.get(i);
            b = calc.FindBulls(input, target);
            c = calc.FindCows(input, target);
            if(bulls == b && cows == c){
                tempArr.add(input);
            }

        }
        this.guesses.clear();
        this.guesses = tempArr;
        
        
    }

    int GetGuess(){
        if (level == Level.EASY){
            return this.GenerateSecretCode();
        }

        if (level == Level.MEDIUM){
            int curr;
            while (true) {
                curr = this.GenerateSecretCode();
                if(!this.guesses.contains(curr)){
                    this.guesses.add(curr);
                    break;
                }
            } 
            return curr;
        }

        Random random = new Random();
        int randIndex = random.nextInt(this.guesses.size());
        return this.guesses.get(randIndex);
        
    }


    
}

// Child class Human player inheriting from Player
class Human extends Player {
    
    Human() {
        super(1234);
    }

    void SetSecretCode(Scanner scanner) {
        
        System.out.println("Enter your Secret Code: ");
        
        while(true){
            
            int num = scanner.nextInt();
            
            if (this.isValidCode(num)){
                this.secret_code = num;
                break;
            }else{
                System.out.println("Invalid Secret Code. Try again.");
            }
        }        
        
    }

    
    int GetGuess(Scanner sc){
        int guess;
        while (true){
            System.out.print("Your Guess: ");
            guess = sc.nextInt();
            if(this.isValidCode(guess)){
                break;
            }else{
                System.out.println("Invalid Code: Try again");
            }
        }
        
        return guess;
    }
    
}

enum Level {
    EASY,
    MEDIUM,
    HARD
}

class Calculations {
    int target;
    int input;
    
    Calculations () {
        this.target =0;
        this.input =0;
    }

    int FindBulls (int input, int target){
        String input_str = Integer.toString(input);
        String target_str = Integer.toString(target);

        int bulls = 0;
        
        for (int i = 0; i < 4; i++) {
            if (input_str.charAt(i) == target_str.charAt(i)) {
                bulls ++;
            }
        }

        return bulls;
    }

    int FindCows (int input, int target){
        String input_str = Integer.toString(input);
        String target_str = Integer.toString(target);

        int cows = 0;
        
        for (int i = 0; i < 4; i++) {
            char curr_digit = input_str.charAt(i);
            int index = target_str.indexOf(curr_digit);
            if (index != -1){
                if (index != i){
                    cows ++;
                }
            }                       
        }
        return cows;
    }



}
// Game class to manage game process
class Game {
    
    Level level;

    Game(){
        System.out.println("Welcome");
        
    }

    void setUpLevel(Scanner scanner){
       

        System.out.println("Select the AI difficulty level:");
        System.out.println("1. Easy");
        System.out.println("2. Medium");
        System.out.println("3. Hard");
       
        
        
        while(true){
            int choice = scanner.nextInt();
            if (choice == 1) {
                this.level = Level.EASY;
                break;
            } else if (choice == 2) {
                this.level = Level.MEDIUM;
                break;
            } else if (choice == 3) {
                this.level = Level.HARD;
                break;
            }
            
            else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
        
        
    }

    void Play (Human human, Computer computer, Scanner sc) {
        Calculations calc = new Calculations();
        int bulls;
        int cows;

        int human_secret = human.GetSecretCode();
        int comp_secret = computer.GetSecretCode();

        int rounds = 7;
        int i = 0;
        for(; i < rounds; i++){
            System.out.println("--------------");
            int human_guess = human.GetGuess(sc);
            int comp_guess = computer.GetGuess();
            bulls = calc.FindBulls(human_guess, comp_secret);
            cows = calc.FindCows(human_guess, comp_secret);
            this.PrintResult(bulls, cows);
            if (bulls == 4){
                System.out.print("You Win :)");
                break;
            }
            bulls = calc.FindBulls(comp_guess, human_secret);
            cows = calc.FindCows(comp_guess, human_secret);
            System.out.println("\nComputer guess: " + comp_guess);
            this.PrintResult(bulls, cows);
            if (bulls == 4){
                System.out.print("You Loose (:");
                break;
            }
            if(this.level == Level.HARD){computer.UpdateGuesses(comp_guess, bulls, cows);}
            
        }
        if(i == 7){System.out.print("!! DRAW !!");}

        
        
        
    }

    void PrintResult(int bulls, int cows){
        String b = "bulls";
        if(bulls == 1){b = "bull";}
        String c = "cows";
        if(cows == 1){c = "cow";}
        System.out.println("Result: "+ bulls + " " + b + " and "+ cows + " "+ c );
        
    }
}

public class GuessingGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //System.out.println("BlackBox");
        Game curr_game = new Game();
        curr_game.setUpLevel(scanner);

        Computer comp = new Computer();
        comp.SetSecretCode();
        comp.SetLevel(curr_game.level);
        //System.out.println("Computer Secret Code: " + comp.GetSecretCode() + " " + comp.level);
        Human player = new Human();
        player.SetSecretCode(scanner);
        //System.out.println("Human Secret Code: " + player.GetSecretCode());
        
        
        curr_game.Play(player, comp, scanner);

        scanner.close();
        
    }
}