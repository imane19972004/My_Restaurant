package fr.unice.polytech.users; // Assuming this package

import fr.unice.polytech.paymentProcessing.BankInfo;

// Note: Requires UserAccount class from above.
public class StudentAccount extends UserAccount {

    private String studentID;
    private double balance = 30 ;
    private BankInfo bankInfo;

    /**
     * Constructor for StudentAccount.
     */
    private StudentAccount(Builder builder) {
        super(builder.name, builder.surname, builder.email); // Initialize attributes from UserAccount
        this.studentID = builder.studentID;
        this.bankInfo = builder.bankInfo;
    }
    
    public String getStudentID() {
        return studentID;
    }

    public double getBalance() {
        return balance;
    }

    public BankInfo getBankInfo() {
        return bankInfo;
    }

    public static class Builder{
        private String name;
        private String surname;
        private String email;
        private String studentID;
        private BankInfo bankInfo;

        public Builder(String name, String surname){
            this.name = name;
            this.surname = surname;
        }

        public Builder email(String email){
            this.email = email;
            return this;
        }

        public Builder studentId(String studentID){
            this.studentID = studentID;
            return this;
        }

        public Builder bankInfo(String cardNumber, int CVV, int month, int year){
            this.bankInfo = new BankInfo(cardNumber, CVV, month, year);
            return this;
        }

        public StudentAccount build(){
            return new StudentAccount(this);
        }

    }
   
}