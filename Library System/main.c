#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define AUTHORS_CSV "Yazarlar.csv"
#define STUDENTS_CSV "Ogrenciler.csv"
#define BOOKS_CSV "Kitaplar.csv"
#define BOOK_COPIES_CSV "KitapOrnekleri.csv"
#define BOOK_AUTHOR_CSV "KitapYazar.csv"
#define BORROW_CSV "KitapOdunc.csv"

#define MAX_LINE_LEN 256

typedef struct Author {
    int authorID;
    char firstName[50];
    char lastName[50];
    struct Author* next;
} Author;

typedef struct Student {
    char studentNo[9];
    char firstName[50];
    char lastName[50];
    int points;
    struct Student* prev;
    struct Student* next;
} Student;

typedef struct Book {
    char title[100];
    char ISBN[14];
    int copyCount;
    struct Book* next;
    struct BookCopy* copies;
} Book;

typedef struct BookCopy {
    int copyNumber;
    char label[20];
    char status[15];
    struct BookCopy* next;
} BookCopy;

typedef struct BookAuthor {
    char ISBN[14];
    int authorID;
} BookAuthor;

typedef struct Date{
    int day;
    int month;
    int year;
}Date;

typedef struct Borrow{
    BookCopy* bookcopy;
    Student* student;
    char operation[2];
    Date* date;
}Borrow;

void extractDate(const char* str, Date* date) {

    const char* dateStr;
    const char* lastComma;

    lastComma = strrchr(str, ',');

    if (lastComma == NULL) {
        printf("Invalid input string.\n");
        return;
    }

    dateStr = lastComma + 1;
    sscanf(dateStr, "%d.%d.%d", &date->day, &date->month, &date->year);
}

int substractDates(Date* date1, Date* date2){
    int sum = 0;
    sum += 365 * (date1->year - date2->year);
    sum += 30 *  (date1->month - date2->month);
    sum += 1 *   (date1->day - date2->day);
    return sum;
}

void* nextStudent(void* node){
    return (void*) ((Student*) node)->next;
}

void* nextBook(void* node){
    return (void*) ((Book*) node)->next;
}

void* nextAuthor(void* node){
    return (void*) ((Author*) node)->next;
}

void* nextBookCopy(void* node){
    return (void*) ((BookCopy*) node)->next;
}

void* getToLast(void* head, void* (* next_node) (void*)){
    void* current = head;

    while(next_node(current) != NULL){
        current = next_node(current);
    }

    return current;
}

void charOfStudent(void* node, char* dest){
    char result[MAX_LINE_LEN] = "";
    char buffer[10];
    Student* student = (Student*) node;

    strcat(result, student->studentNo);
    strcat(result, ", ");
    strcat(result, student->firstName);
    strcat(result, ", ");
    strcat(result, student->lastName);
    strcat(result, ", ");
    sprintf(buffer, "%d", student->points);
    strcat(result, buffer);
    strcat(result, "\n");

    strcpy(dest, result);
}

void charOfBook(void* node, char* dest){
    char result[MAX_LINE_LEN] = "";
    char buffer[10];
    Book* book = (Book*) node;

    strcat(result, book->title);
    strcat(result, ", ");
    strcat(result, book->ISBN);
    strcat(result, ", ");
    sprintf(buffer, "%d", book->copyCount);
    strcat(result, buffer);
    strcat(result, "\n");

    strcpy(dest, result);
}

void charOfBookCopy(void* node, char* dest){
    char result[MAX_LINE_LEN] = "";
    BookCopy* book = (BookCopy*) node;

    strcat(result, book->label);
    strcat(result, ", ");
    strcat(result, book->status);
    strcat(result, "\n");

    strcpy(dest, result);
}

void charOfAuthor(void* node, char* dest){
    char result[MAX_LINE_LEN] = "";
    char buffer[10];
    Author* book = (Author*) node;

    strcat(result, book->firstName);
    strcat(result, ", ");
    strcat(result, book->lastName);
    strcat(result, ", ");
    sprintf(buffer, "%d", book->authorID);
    strcat(result, buffer);
    strcat(result, "\n");

    strcpy(dest, result);

}

void printNode(void* node, void (*charOfNode)(void* node, char* dest)){
    char result[MAX_LINE_LEN];

    charOfNode(node, result);

    printf("%s", result);
}

Student* getStudent(){

    Student* student;

    student = (Student*) malloc(sizeof(Student));

    printf("\n");

    printf("Write first name of the student: ");
    scanf("%s", student->firstName);

    printf("Write last name of the student: ");
    scanf("%s", student->lastName);

    printf("Write NO of the student: ");
    scanf("%s", student->studentNo);

    printf("\n");

    student->points = 100;
    student->prev = NULL;
    student->next = NULL;

    return student;
}

void addStudent(Student** headStudent){
    Student* student;
    Student* last;
    FILE* stream;
    char buffer[MAX_LINE_LEN];

    student = getStudent();

    if(*headStudent == NULL){
        *headStudent = student;
    }
    else{
       last = (Student*) getToLast((void*)*headStudent, nextStudent);
       last->next = student;
       student->prev = last;
    }

    stream = fopen(STUDENTS_CSV,"a");

    charOfStudent((void*)student, buffer);
    fprintf(stream, "%s", buffer);

    fclose(stream);
}

int compareStudent(void* node, char* number){
    Student* student = (Student*) node;
    if (strcmp(student->studentNo, number) == 0)
        return 1;
    return 0;
}

int compareBook(void* node, char* number){
    Book* student = (Book*) node;
    if (strcmp(student->title, number) == 0)
        return 1;
    return 0;
}

int compareBookISBN(void* node, char* number){
    Book* student = (Book*) node;
    if (strcmp(student->ISBN, number) == 0)
        return 1;
    return 0;
}

int compareAuthor(void* node, char* number){
    Author* student = (Author*) node;
    if (strcmp(student->firstName, number) == 0)
        return 1;
    return 0;
}

int compareAuthorID(void* node, char* number){
    Author* student = (Author*) node;
    char buffer[MAX_LINE_LEN];
    sprintf(buffer,"%d",student->authorID);
    if (strcmp(buffer, number) == 0)
        return 1;
    return 0;
}

int compareBookCopyStatus(void* node, char* number){
    BookCopy* student = (BookCopy*) node;
    if (strcmp(student->status, number) == 0)
        return 1;
    return 0;
}

void* findInListPrev(void* head, char* number, int (*compare)(void* node, char* number), void* (*nextNode)(void* node))
{
    void* previous = NULL;

    while(head != NULL){
        if(compare(head, number)){
            return previous;
        }
        previous = head;
        head = nextNode(head);
    }
    return NULL;
}

void* findInListSelf(void* head, char* number, int (*compare)(void* node, char* number), void* (*nextNode)(void* node))
{
    while(head != NULL){
        if(compare(head, number)){
            return head;
        }
        head = nextNode(head);
    }
    return NULL;
}

long findTokenInFile(const char* fileName, const char* number) {

    char line[MAX_LINE_LEN];
    long current_pos = 0;

    FILE* file = fopen(fileName, "r");
    if (!file) return -1;

    while (fgets(line, sizeof(line), file)) {
        long line_start_pos = current_pos;
        char line_copy[MAX_LINE_LEN];
        char* token;

        current_pos = ftell(file);

        strncpy(line_copy, line, MAX_LINE_LEN);

        token = strtok(line_copy, ",");

        while (token) {
            if (strcmp(token, number) == 0) {
                fclose(file);
                return line_start_pos;
            }
            token = strtok(NULL, ",");
        }
    }

    fclose(file);
    return -1L;
}

void findTokenInFileReturnLine(const char* fileName, const char* number, char* dest) {

    char line[MAX_LINE_LEN];

    FILE* file = fopen(fileName, "r");
    if (!file) return;

    while (fgets(line, sizeof(line), file)) {

        char line_copy[MAX_LINE_LEN];
        char* token;

        strncpy(line_copy, line, MAX_LINE_LEN);

        token = strtok(line_copy, ",");

        while (token) {
            if (strcmp(token, number) == 0) {
                fclose(file);
                strcpy(dest, line);
                return;
            }
            token = strtok(NULL, ",");
        }
    }

    fclose(file);
    return;
}

int findExactLineInFile(const char* target, const char* fileName, char* dest) {
    char line[MAX_LINE_LEN];
    FILE* file;

    file = fopen(fileName, "r");

    while (fgets(line, sizeof(line), file)) {

        if (strncmp(line, target, strlen(target)) == 0) {
            strcpy(dest, line);
            fclose(file);
            return 1;
        }
    }
    fclose(file);
    return 0;
}

void changeInFileToken(const char* fileName, const char* oldString, const char* newString) {
    char line[MAX_LINE_LEN];
    FILE* tempFile;
    FILE* file = fopen(fileName, "r");

    if (!file) {
        perror("Error opening file for reading");
        return;
    }

    tempFile = tmpfile();
    if (!tempFile) {
        perror("Error creating temporary file");
        fclose(file);
        return;
    }


    while (fgets(line, sizeof(line), file)) {

        int first = 1;
        char* token = strtok(line, ",");

        while (token) {

            if (strcmp(token, oldString) == 0) {
                token = (char*)newString;
            }

            if (!first) {
                fputc(',', tempFile);
            }
            fputs(token, tempFile);

            token = strtok(NULL, ",");
            first = 0;
        }
        fputc('\n', tempFile);
    }

    rewind(file);
    rewind(tempFile);

    freopen(fileName, "w", file);
    if (!file) {
        perror("Error reopening file for writing");
        fclose(tempFile);
        return;
    }

    while (fgets(line, sizeof(line), tempFile)) {
        fputs(line, file);
    }

    fclose(file);
    fclose(tempFile);
}

void changeInFileLine(char* fileName, char* oldString, char* newString) {
    char line[MAX_LINE_LEN];
    FILE *tempFile;
    FILE *file = fopen(fileName, "r");
    if (!file) {
        perror("Error opening file for reading");
        return;
    }

    tempFile = tmpfile();
    if (!tempFile) {
        perror("Error creating temporary file");
        fclose(file);
        return;
    }


    while (fgets(line, sizeof(line), file)) {

        if (strcmp(line, oldString) == 0) {
            fprintf(tempFile, "%s", newString);
        } else {
            fprintf(tempFile, "%s", line);
        }
    }

    rewind(tempFile);
    freopen(fileName, "w", file);

    while (fgets(line, sizeof(line), tempFile)) {
        fputs(line, file);
    }

    fclose(file);
    fclose(tempFile);
}

Date* getDate(char* ofWhat){
    Date* date;

    date = (Date*) malloc(sizeof(Date));

    printf("\n");

    printf("Write the day of %s: ", ofWhat);
    scanf("%d", &date->day);
    printf("Write the month of %s: ", ofWhat);
    scanf("%d", &date->month);
    printf("Write the year of %s: ", ofWhat);
    scanf("%d", &date->year);

    printf("\n");

    return date;
}

void viewStudent(Student** headStudent){
    char studentNo[9];
    void* student;

    printf("\nWrite the student's No: ");
    scanf("%s", studentNo);

    student = findInListPrev((void*) *headStudent, studentNo, compareStudent, nextStudent);

    if(student == NULL){
        if(strcmp((*headStudent)->studentNo, studentNo) == 0){

            printNode((void*)*headStudent,charOfStudent);

        }
        else{
            printf("\nNo such student.\n");
            return;
        }
    }
    else{

        student = (void*) ((Student*)student)->next;
        printNode(student,charOfStudent);
    }

}

void listStudentsWithUnreturnedBooks(Student** headStudent, Book** headBook) {
    Book* book = *headBook;
    BookCopy* copy;
    FILE* file;
    char label[MAX_LINE_LEN];
    char studentID[20];
    char lines[1000][MAX_LINE_LEN];
    int count = 0;
    int found = 0;
    int i;

    Date* borrowDate = (Date*) malloc(sizeof(Date));

    printf("\n--- Students With Unreturned Books ---\n");

    while (book != NULL) {
        copy = book->copies;
        while (copy != NULL) {
            if (strcmp(copy->status, "Available") != 0) {

                file = fopen(BORROW_CSV, "r");
                if (!file) {
                    printf("Failed to open borrow file.\n");
                    free(borrowDate);

                    return;
                }

                count = 0;

                while (fgets(lines[count], MAX_LINE_LEN, file)) {
                    count++;
                }
                fclose(file);

                found = 0;

                for (i = count - 1; i >= 0; i--) {
                    sscanf(lines[i], "%[^,],%[^,],", label, studentID);
                    if (!found && strcmp(label, copy->label) == 0) {
                        extractDate(lines[i], borrowDate);
                        if (1) {

                            Student* student = *headStudent;
                            while (student != NULL) {
                                if (strcmp(student->studentNo, studentID) == 0) {
                                    printNode((void*)student, charOfStudent);
                                }
                                student = (Student*) nextStudent((void*)student);
                            }
                        }
                        found = 1;
                    }
                }
            }
            copy = (BookCopy*) nextBookCopy((void*)copy);
        }
        book = (Book*) nextBook((void*)book);
    }

    free(borrowDate);

}

void listPenalizedStudents(Student* headStudent) {
    Student* current = headStudent;
    int found = 0;

    printf("\nPenalized Students (points < 0):\n");

    while (current != NULL) {
        if (current->points < 0) {
            printNode((void*)current, charOfStudent);
            found = 1;
        }
        current = current->next;
    }

    if (!found) {
        printf("No penalized students found.\n");
    }
}

void listNode(void* head, void (*charOfNode)(void* node, char* dest), void* (*nextNode)(void* node)){

    if (head == NULL) {
        fprintf(stderr, "None in the list.\n");
        return;
    }

    while(head != NULL){

        printNode(head, charOfNode);

        head = nextNode(head);
    }
}

void* free_node_student(void* node){
    void* next = (void*) ((Student*) node)->next;
    free((Student*) node);
    return next;
}

void* free_node_author(void* node){
    void* next = (void*) ((Author*) node)->next;
    free((Author*) node);
    return next;
}

void* free_node_book(void* node){
    void* next = (void*) ((Book*) node)->next;
    free((Book*) node);
    return next;
}

void* free_node_bookCopy(void* node){
    void* next = (void*) ((BookCopy*) node)->next;
    free((BookCopy*) node);
    return next;
}

void free_list(void* head, void* (*free_node)(void*)) {
    void* current = head;
    while (current != NULL) {
        current = free_node(current);
    }
}

void updateStudent(Student* headStudent, Student* studentToUpdate, const char* newNo, const char* newFirstName, const char* newLastName, int newPoints) {
    FILE* stream;
    char buffer[MAX_LINE_LEN];
    char oldStudentNo[9];
    Student* current;
    FILE* file;
    FILE* temp;
    char lineCopy[MAX_LINE_LEN];
    char* token;
    char* studentField;

    strcpy(oldStudentNo, studentToUpdate->studentNo);

    strncpy(studentToUpdate->studentNo, newNo, 9);
    strncpy(studentToUpdate->firstName, newFirstName, 50);
    strncpy(studentToUpdate->lastName, newLastName, 50);
    studentToUpdate->points = newPoints;

    stream = fopen(STUDENTS_CSV, "w");
    if (stream == NULL) {
        perror("Failed to open student file for writing");
        return;
    }

    current = headStudent;
    while (current != NULL) {
        charOfStudent((void*)current, buffer);
        fprintf(stream, "%s", buffer);
        current = current->next;
    }

    fclose(stream);

    if (strcmp(oldStudentNo, newNo) != 0) {
        file = fopen(BORROW_CSV, "r");
        temp = fopen("Temp.csv", "w");
        if (!file || !temp) {
            perror("Failed to open borrow file for update");
            if (file) fclose(file);
            if (temp) fclose(temp);
            return;
        }

        while (fgets(buffer, MAX_LINE_LEN, file)) {

            strcpy(lineCopy, buffer);

            token = strtok(lineCopy, ",");
            studentField = strtok(NULL, ",");

            if (studentField && strcmp(studentField, oldStudentNo) == 0) {

                char newLine[MAX_LINE_LEN];
                snprintf(newLine, sizeof(newLine), "%s,%s,%s",
                         strtok(buffer, ","), newNo, buffer + strlen(token) + strlen(studentField) + 2);
                fputs(newLine, temp);
            } else {
                fputs(buffer, temp);
            }
        }

        fclose(file);
        fclose(temp);

        remove(BORROW_CSV);
        rename("Temp.csv", BORROW_CSV);
    }

    printf("Student updated successfully.\n");
}

void updateStudentGetInfo(Student** headStudent) {
    char studentNo[9];
    Student* student;

    char newNo[9];
    char newFirstName[50];
    char newLastName[50];
    int newPoints;

    printf("\nWrite the student's number you want to update: ");
    scanf("%s", studentNo);

    student = (Student*) findInListSelf((void*) *headStudent, studentNo, compareStudent, nextStudent);

    if (student == NULL) {
        printf("No student found with number: %s\n", studentNo);
    } else {
        printf("\nCurrent Information:\n");
        printf("Student No: %s\n", student->studentNo);
        printf("First Name: %s\n", student->firstName);
        printf("Last Name: %s\n", student->lastName);
        printf("Points: %d\n", student->points);

        printf("\nEnter new student number: ");
        scanf("%s", newNo);
        printf("Enter new first name: ");
        scanf("%s", newFirstName);
        printf("Enter new last name: ");
        scanf("%s", newLastName);
        printf("Enter new points: ");
        scanf("%d", &newPoints);

        updateStudent(*headStudent, student, newNo, newFirstName, newLastName, newPoints);

        printf("\nStudent has been updated.\n");
    }
}

void deleteStudent(Student** headStudent){
    char studentNo[9];
    Student* student;
    Student* studentPrev;

    printf("\nWrite the students number you want to delete: ");
    scanf("%s", studentNo);

    studentPrev = (Student*) findInListPrev((void*) *headStudent, studentNo, compareStudent, nextStudent);
    student = (Student*) findInListSelf((void*) *headStudent, studentNo, compareStudent, nextStudent);

    if(student == NULL){
        printf("\nNo such student.\n");
        return;
    }

    updateStudent(*headStudent,student, "-1", student->firstName,student->lastName,student->points);

    if(studentPrev == NULL){
        *headStudent = student->next;
        free(student);
    }
    else{
        studentPrev->next = student->next;
        free(student);
    }

    printf("\nStudent has been deleted.\n");
}

void charOfDate(void* node, char* dest){
    Date* date = (Date*) node;
    char buffer[10] = "";
    char result[MAX_LINE_LEN] = "";

    sprintf(buffer, "%d", date->day);
    strcat(result, buffer);
    strcat(result, ".");
    sprintf(buffer, "%d", date->month);
    strcat(result, buffer);
    strcat(result, ".");
    sprintf(buffer, "%d", date->year);
    strcat(result, buffer);

    strcpy(dest, result);
}

void updateBookCopy(BookCopy* bookCopy, char* newLabel, char* newStatus) {
    char oldBuffer[MAX_LINE_LEN] = "";
    char newBuffer[MAX_LINE_LEN] = "";
    FILE* file;
    FILE* temp;

    char oldLabel[MAX_LINE_LEN];
    strcpy(oldLabel, bookCopy->label);

    charOfBookCopy((void*) bookCopy, oldBuffer);

    strcpy(bookCopy->label, newLabel);
    strcpy(bookCopy->status, newStatus);

    charOfBookCopy((void*) bookCopy, newBuffer);

    changeInFileLine(BOOK_COPIES_CSV, oldBuffer, newBuffer);

    if (strcmp(oldLabel, newLabel) != 0) {
        file = fopen(BORROW_CSV, "r");
        temp = fopen("temp.csv", "w");

        char line[MAX_LINE_LEN];
        while (fgets(line, sizeof(line), file)) {
            char label[MAX_LINE_LEN], studentNo[50], operationId[50], date[50];
            sscanf(line, "%[^,],%[^,],%[^,],%[^\n]", label, studentNo, operationId, date);

            if (strcmp(label, oldLabel) == 0) {

                fprintf(temp, "%s,%s,%s,%s\n", newLabel, studentNo, operationId, date);
            } else {
                fputs(line, temp);
            }
        }

        fclose(file);
        fclose(temp);

        remove(BORROW_CSV);
        rename("temp.csv", BORROW_CSV);
    }
}

void borrowBook(Student** headStudent, Book** headBook){
    char ISBN[14];
    char studentID[9];
    char operation[2] = {'0','\0'};
    char dateChar[50];
    Date* date;

    Book* book;
    Student* student;
    BookCopy* bookCopy;
    Borrow* borrow;

    char buffer[MAX_LINE_LEN] = "";

    FILE* file;

    borrow = (Borrow*) malloc(sizeof(Borrow));

    printf("Please write the isbn: ");
    scanf("%s", ISBN);
    printf("Please write the student id: ");
    scanf("%s", studentID);
    date = getDate("Borrowing Time");

    book = (Book*) findInListSelf((void*)*headBook, ISBN, compareBookISBN, nextBook);
    if(book==NULL){
        printf("No such book.");
        return;
    }

    student = (Student*) findInListSelf((void*)*headStudent, studentID, compareStudent, nextStudent);
    if(student==NULL){
        printf("No such student.");
        return;
    }
    else{
        if(student->points < 0){
            printf("Student has below zero points.");
            return;
        }
    }

    bookCopy = (BookCopy*) findInListSelf((void*)book->copies, "Available",compareBookCopyStatus, nextBookCopy);
    if(bookCopy == NULL){
        printf("No available books.");
        return;
    }

    updateBookCopy(bookCopy, bookCopy->label, studentID);

    borrow->bookcopy = bookCopy;
    borrow->student = student;
    strcpy(borrow->operation,operation);
    borrow->date = date;

    strcat(buffer, bookCopy->label);
    strcat(buffer,",");
    strcat(buffer, studentID);
    strcat(buffer,",");
    strcat(buffer, operation);
    strcat(buffer,",");

    charOfDate((void*)date,dateChar);
    strcat(buffer, dateChar);
    strcat(buffer,"\n");

    file = fopen(BORROW_CSV, "a");
    fputs(buffer, file);
    fclose(file);

    printf("Borrow successful.");
    free(borrow);
    free(date);

}

void returnBook(Student** headStudent, Book** headBook){
    char ISBN[14];
    char studentID[9];
    char operation[2] = {'1','\0'};
    char dateString[50];
    char oldChar[MAX_LINE_LEN];
    Date *dateOld, *dateNew;
    FILE* file;

    Book* book;
    Student* student;
    BookCopy* bookCopy;
    Borrow* borrow;

    char buffer[MAX_LINE_LEN] = "";
    char buffer2[MAX_LINE_LEN] = "";

    dateOld = (Date*) malloc(sizeof(Date));

    printf("Please write the isbn of the book: ");
    scanf("%s", ISBN);
    printf("Please write the student id: ");
    scanf("%s", studentID);
    dateNew = getDate("Returning Time");

    book = (Book*) findInListSelf((void*)*headBook, ISBN, compareBookISBN, nextBook);
    if(book==NULL){
        printf("No such book.");
        return;
    }

    student = (Student*) findInListSelf((void*)*headStudent, studentID, compareStudent, nextStudent);
    if(student==NULL){
        printf("No such student.");
        return;
    }

    bookCopy = (BookCopy*) findInListSelf((void*)book->copies, student->studentNo,compareBookCopyStatus, nextBookCopy);

    snprintf(buffer2, sizeof(buffer2), "%s,%s,0", bookCopy->label, studentID);

    findExactLineInFile(buffer2, BORROW_CSV, oldChar);
    extractDate(oldChar, dateOld);

    if (substractDates(dateNew, dateOld) > 15){
        printf("\nExceeded Return Time, reducing 10 points.\n");
        updateStudent(*headStudent,student,student->studentNo,student->firstName,student->lastName,student->points-10);
    }
    updateBookCopy(bookCopy, bookCopy->label, "Available");

    charOfDate((void*)dateNew, dateString);
    snprintf(buffer, sizeof(buffer), "%s,%s,%s,%s\n", bookCopy->label, studentID, operation, dateString);

    file = fopen(BORROW_CSV, "a");
    fputs(buffer, file);
    fclose(file);

    borrow = (Borrow*) malloc(sizeof(Borrow));
    borrow->bookcopy = bookCopy;
    borrow->student = student;
    strcpy(borrow->operation,operation);
    borrow->date = dateNew;


    printf("Return successful.");

    free(borrow);
    free(dateOld);
    free(dateNew);

}

void studentMenu(Student** headStudent, Book** headBook) {
    int choice;

    while (1) {
        printf("\n--- Student Operations ---\n");
        printf("1. Add Student\n");
        printf("2. Delete Student\n");
        printf("3. Update Student\n");
        printf("4. View Student Info\n");
        printf("5. List Students With Unreturned Books\n");
        printf("6. List Penalized Students\n");
        printf("7. List All Students\n");
        printf("8. Borrow Book\n");
        printf("9. Return Book\n");
        printf("0. Back to Main Menu\n");
        printf("Enter your choice: ");
        scanf("%d", &choice);

        switch (choice) {
            case 1: addStudent(headStudent); break;
            case 2: deleteStudent(headStudent); break;
            case 3: updateStudentGetInfo(headStudent); break;
            case 4: viewStudent(headStudent); break;
            case 5: listStudentsWithUnreturnedBooks(headStudent,headBook); break;
            case 6: listPenalizedStudents(*headStudent);break;
            case 7: listNode((void*)*headStudent, charOfStudent, nextStudent); break;
            case 8: borrowBook(headStudent, headBook); break;
            case 9: returnBook(headStudent,headBook); break;
            case 0: return;
            default: printf("Invalid choice. Please try again.\n");
        }
    }
}

Book* getBook(){
    Book* book;
    BookCopy* currentBookCopy;
    BookCopy* prevBookCopy;
    char buffer[12];
    int i;

    book = (Book*) malloc(sizeof(Book));
    printf("\n");
    printf("Write the ISBN: ");
    scanf(" %[^\n]", book->ISBN);
    printf("Write the title: ");
    scanf(" %[^\n]", book->title);
    printf("Write the number of books: ");
    scanf("%d", &book->copyCount);
    book->next = NULL;
    printf("\n");

    book->copies = (BookCopy*) malloc(sizeof(BookCopy));
    currentBookCopy = book->copies;
    currentBookCopy->copyNumber = 1;
    strcpy(currentBookCopy->label, book->ISBN);
    strcat(currentBookCopy->label, "_");
    sprintf(buffer,"%d",1);
    strcat(currentBookCopy->label, buffer);
    strcpy(currentBookCopy->status, "Available");
    currentBookCopy->next = NULL;

    for(i=2; i<=book->copyCount; i++){
        prevBookCopy = currentBookCopy;
        currentBookCopy = (BookCopy*) malloc(sizeof(BookCopy));
        currentBookCopy->copyNumber = i;
        strcpy(currentBookCopy->label, book->ISBN);
        strcat(currentBookCopy->label, "_");
        sprintf(buffer,"%d",i);
        strcat(currentBookCopy->label, buffer);
        strcpy(currentBookCopy->status, "Available");
        currentBookCopy->next = NULL;
        prevBookCopy->next = currentBookCopy;
    }

    return book;
}

void addBook(Book** headBook){
    Book* book;
    Book* last;
    BookCopy* bookCopy;
    FILE* file;
    char buffer[MAX_LINE_LEN];

    book = getBook();

    if(*headBook == NULL){
        *headBook = book;
    }
    else{
       last = (Book*) getToLast((void*)*headBook, nextBook);
       last->next = book;
    }

    file = fopen(BOOKS_CSV,"a");
    charOfBook((void*)book, buffer);
    fprintf(file, "%s", buffer);
    fclose(file);

    file = fopen(BOOK_COPIES_CSV,"a");
    for(bookCopy = book->copies; bookCopy != NULL; bookCopy = bookCopy->next){
        charOfBookCopy((void*)bookCopy, buffer);
        fprintf(file, "%s", buffer);
    }
    fclose(file);
}

void updateBook(Book* headBook, Book* targetBook, const char* newTitle, const char* newISBN) {
    char oldBookLine[MAX_LINE_LEN], newBookLine[MAX_LINE_LEN];
    char oldISBN[14];
    char line[MAX_LINE_LEN];
    FILE* file;
    FILE* temp;
    BookCopy* currentCopy;
    int copyNum;

    strcpy(oldISBN, targetBook->ISBN);

    charOfBook((void*) targetBook, oldBookLine);

    strcpy(targetBook->title, newTitle);
    strcpy(targetBook->ISBN, newISBN);

    charOfBook((void*) targetBook, newBookLine);
    changeInFileLine(BOOKS_CSV, oldBookLine, newBookLine);

    file = fopen(BOOK_AUTHOR_CSV, "r");
    temp = fopen("temp.csv", "w");

    while (fgets(line, sizeof(line), file)) {
        char isbn[20], authorID[50];
        sscanf(line, "%[^,],%[^\n]", isbn, authorID);

        if (strcmp(isbn, oldISBN) == 0) {
            fprintf(temp, "%s,%s\n", newISBN, authorID);
        } else {
            fputs(line, temp);
        }
    }

    fclose(file);
    fclose(temp);

    remove(BOOK_AUTHOR_CSV);
    rename("temp.csv", BOOK_AUTHOR_CSV);

    currentCopy = targetBook->copies;
    copyNum = 1;
    while (currentCopy != NULL) {
        char newLabel[40];
        sprintf(newLabel, "%s_%d", newISBN, copyNum);
        updateBookCopy(currentCopy, newLabel, currentCopy->status);
        currentCopy = currentCopy->next;
        copyNum++;
    }
}

void updateBookByInfo(Book** headBook) {
    char ISBN[14];
    Book* book;
    char newTitle[100];
    char newISBN[14];

    printf("\nWrite the book's ISBN you want to update: ");
    scanf("%s", ISBN);

    book = (Book*) findInListSelf((void*) *headBook, ISBN, compareBookISBN, nextBook);

    if (book == NULL) {
        printf("No book found with ISBN: %s\n", ISBN);
    } else {
        printf("\nCurrent Information:\n");
        printf("Title: %s\n", book->title);
        printf("ISBN: %s\n", book->ISBN);
        printf("Copy Count: %d\n", book->copyCount);

        printf("\nEnter new title: ");
        scanf(" %[^\n]", newTitle);
        printf("Enter new ISBN: ");
        scanf("%s", newISBN);

        updateBook(*headBook, book, newTitle, newISBN);

        printf("\nBook has been updated.\n");
    }
}

void deleteBookByInfo(Book** headBook) {
    char ISBN[14];
    Book* book;
    Book* bookPrev;
    BookCopy* currentCopy;

    printf("\nWrite the ISBN of the book you want to delete: ");
    scanf("%s", ISBN);

    bookPrev = (Book*) findInListPrev((void*) *headBook, ISBN, compareBookISBN, nextBook);
    book = (Book*) findInListSelf((void*) *headBook, ISBN, compareBookISBN, nextBook);

    if (book == NULL) {
        printf("\nNo such book.\n");
        return;
    }

    updateBook(*headBook, book, book->title, "-1");

    currentCopy = book->copies;
    while (currentCopy != NULL) {
        BookCopy* temp = currentCopy;
        currentCopy = currentCopy->next;
        free(temp);
    }

    if (bookPrev == NULL) {
        *headBook = book->next;
    } else {
        bookPrev->next = book->next;
    }

    free(book);

    printf("\nBook has been deleted.\n");
}

void viewBookInfo(Book** headBook){
    Book* book = *headBook;
    BookCopy* bookCopy;
    char title[100];

    printf("Write the title of the book: ");
    scanf(" %[^\n]", title);

    book = (Book*) findInListPrev((void*) *headBook, title, compareBook, nextBook);

    if(book == NULL){
        if(strcmp((*headBook)->title, title) == 0){
            book = *headBook;
        }
        else{
            printf("\nNo such book.\n");
            return;
        }
    }
    else{
        book = book->next;
    }

    printf("\n");
    printNode((void*)book, charOfBook);
    bookCopy = book->copies;
    while(bookCopy != NULL){
        printNode((void*)bookCopy, charOfBookCopy);
        bookCopy = (BookCopy*) nextBookCopy((void*) bookCopy);
    }

}

int isAv(char* str){
    if(strcmp(str, "Available") == 0){
        return 1;
    }
    return 0;
}

int isNotAv(char* str){
    if(strcmp(str, "Available") != 0){
        return 1;
    }
    return 0;
}

void listBooks(Book** headBook, int (*isAv)(char* str)){
    Book* head1 = *headBook;
    BookCopy* head2;

    printf("\n");
    while (head1 != NULL){
        printf("---");
        printNode((void*)head1, charOfBook);
        head2 = head1->copies;
        while(head2 != NULL){
            if(isAv(head2->status)){
                printNode((void*)head2, charOfBookCopy);
            }

            head2 = (BookCopy*) nextBookCopy((void*) head2);

        }
        head1 = (Book*) nextBook((void*) head1);
    }
}

void matchBookAuthor(Book* headBook, Author* headAuthor, BookAuthor** arrayPtr, int* countPtr){
    char ISBN[14];
    int authorID;
    void* voidPtr;
    char buffer[MAX_LINE_LEN] = "";
    char buffer2[MAX_LINE_LEN] = "";
    FILE* file;
    BookAuthor* temp;

    printf("Please write the ISBN: ");
    scanf("%s", ISBN);
    printf("Please write the author ID: ");
    scanf("%d", &authorID);
    sprintf(buffer, "%d", authorID);

    voidPtr = findInListSelf((void*)headBook, ISBN, compareBookISBN, nextBook);
    if(voidPtr == NULL){
        printf("No such book.\n");
        return;
    }

    voidPtr = findInListSelf((void*)headAuthor, buffer, compareAuthorID, nextAuthor);
    if(voidPtr == NULL){
        printf("No such author.\n");
        return;
    }

    snprintf(buffer2, sizeof(buffer2), "%s,%d\n", ISBN, authorID);
    file = fopen(BOOK_AUTHOR_CSV, "a");
    if(file != NULL){
        fputs(buffer2, file);
        fclose(file);
    } else {
        printf("Failed to open file.\n");
        return;
    }

    temp = realloc(*arrayPtr, (*countPtr + 1) * sizeof(BookAuthor));
    if(temp == NULL){
        printf("Memory allocation failed.\n");
        return;
    }

    *arrayPtr = temp;
    strcpy((*arrayPtr)[*countPtr].ISBN, ISBN);
    (*arrayPtr)[*countPtr].authorID = authorID;
    (*countPtr)++;

    printf("Book-Author relationship added successfully.\n");
}

void updateBookAuthor(Book* headBook, Author* headAuthor, BookAuthor** arrayPtr, int* countPtr) {
    char oldISBN[14], newISBN[14];
    int oldAuthorID, newAuthorID;
    char oldAuthorIDStr[20], newAuthorIDStr[20];
    char idBuffer[20];
    void* voidPtr;
    FILE* file;
    FILE* tempFile;
    char line[MAX_LINE_LEN];
    int found;
    int i;

    printf("Please enter the old book ISBN: ");
    scanf("%s", oldISBN);
    printf("Please enter the old author ID: ");
    scanf("%d", &oldAuthorID);
    printf("Please enter the new book ISBN: ");
    scanf("%s", newISBN);
    printf("Please enter the new author ID: ");
    scanf("%d", &newAuthorID);

    sprintf(oldAuthorIDStr, "%d", oldAuthorID);
    sprintf(newAuthorIDStr, "%d", newAuthorID);


    voidPtr = findInListSelf((void*)headBook, newISBN, compareBookISBN, nextBook);
    if (voidPtr == NULL) {
        printf("No such book with new ISBN.\n");
        return;
    }

    sprintf(idBuffer, "%d", newAuthorID);
    voidPtr = findInListSelf((void*)headAuthor, idBuffer, compareAuthorID, nextAuthor);
    if (voidPtr == NULL) {
        printf("No such author with new ID.\n");
        return;
    }

    file = fopen(BOOK_AUTHOR_CSV, "r");
    if (file == NULL) {
        printf("Could not open file.\n");
        return;
    }

    tempFile = fopen("temp.csv", "w");
    if (tempFile == NULL) {
        printf("Could not create temp file.\n");
        fclose(file);
        return;
    }

    found = 0;

    while (fgets(line, sizeof(line), file)) {
        char isbn[14], authorIDstr[20];
        sscanf(line, "%13[^,],%19[^\n]", isbn, authorIDstr);

        if (strcmp(isbn, oldISBN) == 0 && strcmp(authorIDstr, oldAuthorIDStr) == 0) {
            fprintf(tempFile, "%s,%s\n", newISBN, newAuthorIDStr);
            found = 1;
        } else {
            fputs(line, tempFile);
        }
    }

    fclose(file);
    fclose(tempFile);

    if (!found) {
        printf("Old book-author pair not found.\n");
        remove("temp.csv");
        return;
    }

    remove(BOOK_AUTHOR_CSV);
    rename("temp.csv", BOOK_AUTHOR_CSV);

    found = 0;

    for (i = 0; i < *countPtr && !found; i++) {
        if (strcmp((*arrayPtr)[i].ISBN, oldISBN) == 0 && (*arrayPtr)[i].authorID == oldAuthorID) {
            strcpy((*arrayPtr)[i].ISBN, newISBN);
            (*arrayPtr)[i].authorID = newAuthorID;
            found = 1;
        }
    }

    printf("Book-author pair updated successfully.\n");
}

void listOverdueBooks(Book** headBook){
    Book* book = *headBook;
    BookCopy* copy;
    char buffer[MAX_LINE_LEN];
    char borrowLine[MAX_LINE_LEN];
    int hasOverdue;

    Date* today = getDate("Today's Date");
    Date* borrowDate = (Date*) malloc(sizeof(Date));

    printf("\n--- Overdue Books ---\n");

    while (book != NULL){
        hasOverdue = 0;
        copy = book->copies;
        while (copy != NULL){
            if (strcmp(copy->status, "Available") != 0){

                snprintf(buffer, sizeof(buffer), "%s,%s,0", copy->label, copy->status);
                if (findExactLineInFile(buffer, BORROW_CSV, borrowLine)){
                    extractDate(borrowLine, borrowDate);
                    if (substractDates(today, borrowDate) > 15){
                        if (!hasOverdue){
                            printf("---\n");
                            printNode((void*)book, charOfBook);
                            hasOverdue = 1;
                        }
                        printNode((void*)copy, charOfBookCopy);
                    }
                }
            }
            copy = (BookCopy*) nextBookCopy((void*) copy);
        }
        book = (Book*) nextBook((void*) book);
    }

    free(today);
    free(borrowDate);
}

void bookMenu(Book** headBook, Author** headAuthor, BookAuthor** arrayPtr, int* countPtr) {
    int choice;

    while (1) {
        printf("\n--- Book Operations ---\n");
        printf("1. Add Book\n");
        printf("2. Delete Book\n");
        printf("3. Update Book\n");
        printf("4. View Book Info\n");
        printf("5. List Books On Shelf\n");
        printf("6. List Overdue Books\n");
        printf("7. Match Book-Author\n");
        printf("8. Update Book Author\n");
        printf("0. Back to Main Menu\n");
        printf("Enter your choice: ");
        scanf("%d", &choice);

        switch (choice) {
            case 1: addBook(headBook); break;
            case 2: deleteBookByInfo(headBook); break;
            case 3: updateBookByInfo(headBook); break;
            case 4: viewBookInfo(headBook); break;
            case 5: listBooks(headBook, isAv); break;
            case 6: listOverdueBooks(headBook); break;
            case 7: matchBookAuthor(*headBook, *headAuthor,arrayPtr,countPtr);break;
            case 8: updateBookAuthor(*headBook, *headAuthor,arrayPtr,countPtr);break;
            case 0: return;
            default: printf("Invalid choice. Please try again.\n");
        }

    }
}

Author* getAuthor(){
    Author* student;

    student = (Author*) malloc(sizeof(Author));

    printf("\n");

    printf("Write first name of the author: ");
    scanf("%s", student->firstName);

    printf("Write last name of the author: ");
    scanf("%s", student->lastName);
    printf("\n");

    student->next = NULL;
    student->authorID = 1;
    return student;

}

void addAuthor(Author** headStudent){
    Author* student;
    Author* last;
    FILE* file;
    char buffer[MAX_LINE_LEN];

    student = getAuthor();

    if(*headStudent == NULL){
        *headStudent = student;
    }
    else{
        last = (Author*) getToLast((void*)*headStudent, nextAuthor);
        last->next = student;
        student->authorID = last->authorID+1;
    }

    file = fopen(AUTHORS_CSV,"a");

    charOfAuthor((void*)student, buffer);
    fprintf(file, "%s", buffer);

    fclose(file);
}

void updateAuthor(int newID, const char* newFirstName, const char* newLastName, Author* headAuthor, Author* targetAuthor) {
    int oldID;
    FILE* file;
    char line[MAX_LINE_LEN];
    FILE* temp;

    if (targetAuthor == NULL) {
        printf("Target author is NULL.\n");
        return;
    }

    oldID = targetAuthor->authorID;

    targetAuthor->authorID = newID;
    strncpy(targetAuthor->firstName, newFirstName, sizeof(targetAuthor->firstName));
    strncpy(targetAuthor->lastName, newLastName, sizeof(targetAuthor->lastName));

    file = fopen(AUTHORS_CSV, "r");
    if (!file) {
        printf("Failed to open %s for reading.\n", AUTHORS_CSV);
        return;
    }

    temp = fopen("temp.csv", "w");
    if (!temp) {
        printf("Failed to open temporary file for writing.\n");
        fclose(file);
        return;
    }


    while (fgets(line, sizeof(line), file)) {
        char fname[50], lname[50];
        int id;

        if (sscanf(line, "%49[^,],%49[^,],%d", fname, lname, &id) == 3) {
            if (id == oldID) {
                fprintf(temp, "%s,%s,%d\n", newFirstName, newLastName, newID);
            } else {
                fputs(line, temp);
            }
        }
    }

    fclose(file);
    fclose(temp);
    remove(AUTHORS_CSV);
    rename("temp.csv", AUTHORS_CSV);

    if (newID != oldID) {
        file = fopen(BOOK_AUTHOR_CSV, "r");
        if (!file) {
            printf("Failed to open %s for reading.\n", BOOK_AUTHOR_CSV);
            return;
        }

        temp = fopen("temp.csv", "w");
        if (!temp) {
            printf("Failed to open temporary file for writing.\n");
            fclose(file);
            return;
        }

        while (fgets(line, sizeof(line), file)) {
            char isbn[20];
            int id;
            sscanf(line, "%[^,],%d", isbn, &id);
            if (id == oldID) {
                fprintf(temp, "%s,%d\n", isbn, newID);
            } else {
                fputs(line, temp);
            }
        }

        fclose(file);
        fclose(temp);
        remove(BOOK_AUTHOR_CSV);
        rename("temp.csv", BOOK_AUTHOR_CSV);
    }

    printf("Author updated successfully.\n");
}

void updateAuthorGetInfo(Author** headAuthor) {
    Author* author;
    char input[50];
    int currentID, newID;
    char newFirstName[50];
    char newLastName[50];

    printf("\nEnter the author ID you want to update: ");
    scanf("%d", &currentID);
    sprintf(input, "%d", currentID);

    author = (Author*) findInListSelf((void*) *headAuthor, input, compareAuthorID, nextAuthor);

    if (author == NULL) {
        printf("No author found with ID %d.\n", currentID);
        return;
    }

    printf("Enter new author ID: ");
    scanf("%d", &newID);

    printf("Enter new first name: ");
    scanf("%s", newFirstName);

    printf("Enter new last name: ");
    scanf("%s", newLastName);

    updateAuthor(newID, newFirstName, newLastName, *headAuthor, author);

    printf("\nAuthor has been updated.\n");
}

void deleteAuthorGetInfo(Author** headAuthor) {

    int deleteID;
    char idStr[20];
    Author* authorToDelete;
    Author *current = *headAuthor, *prev = NULL;

    if (*headAuthor == NULL) {
        printf("Author list is empty.\n");
        return;
    }



    printf("Enter the ID of the author to delete: ");
    scanf("%d", &deleteID);
    sprintf(idStr, "%d", deleteID);

    authorToDelete = (Author*) findInListSelf((void*)*headAuthor, idStr, compareAuthorID, nextAuthor);

    if (authorToDelete == NULL) {
        printf("No author found with ID %d.\n", deleteID);
        return;
    }

    updateAuthor(-1, authorToDelete->firstName, authorToDelete->lastName, *headAuthor, authorToDelete);

    current = *headAuthor;
    while (current != NULL) {
        if (current == authorToDelete) {
            if (prev == NULL) {

                *headAuthor = current->next;
            } else {
                prev->next = current->next;
            }
            free(current);
            printf("Author deleted from memory.\n");
            return;
        }
        prev = current;
        current = current->next;
    }
}

void viewAuthor(Author** headStudent){
    char studentNo[50];
    void* student;

    printf("\nWrite the author'n name: ");
    scanf("%s", studentNo);

    student = findInListPrev((void*) *headStudent, studentNo, compareAuthor, nextAuthor);

    if(student == NULL){
        if(strcmp((*headStudent)->firstName, studentNo) == 0){

            printNode((void*)*headStudent,charOfAuthor);

        }
        else{
            printf("\nNo such Author.\n");
            return;
        }
    }
    else{

        student = (void*) ((Author*)student)->next;
        printNode(student,charOfAuthor);
    }
}

void authorMenu(Author** headAuthor) {
    int choice;

    while (1) {
        printf("\n--- Author Operations ---\n");
        printf("1. Add Author\n");
        printf("2. Delete Author\n");
        printf("3. Update Author\n");
        printf("4. View Author Info\n");
        printf("0. Back to Main Menu\n");
        printf("Enter your choice: ");
        scanf("%d", &choice);

        switch (choice) {
            case 1: addAuthor(headAuthor); break;
            case 2: deleteAuthorGetInfo(headAuthor); break;
            case 3: updateAuthorGetInfo(headAuthor); break;
            case 4: viewAuthor(headAuthor);break;
            case 0: return;
            default: printf("Invalid choice. Please try again.\n");
        }

    }
}

void resetFiles(){
    FILE* file;

    file = fopen(AUTHORS_CSV,"w");
    fclose(file);
    file = fopen(STUDENTS_CSV,"w");
    fclose(file);
    file = fopen(BOOKS_CSV,"w");
    fclose(file);
    file = fopen(BOOK_COPIES_CSV,"w");
    fclose(file);
    file = fopen(BOOK_AUTHOR_CSV ,"w");
    fclose(file);
    file = fopen(BORROW_CSV,"w");
    fclose(file);

}

int main() {
    int choice;
    int flag = 1;
    Student** headStudent;
    Book** headBook;
    Author** headAuthor;
    BookAuthor* bookAuthorArray = NULL;
    int bookAuthorCount = 0;

    headStudent = (Student**) malloc(sizeof(Student*));
    *headStudent = NULL;
    headBook = (Book**) malloc(sizeof(Book*));
    *headBook = NULL;
    headAuthor = (Author**) malloc(sizeof(Author*));
    *headAuthor = NULL;

    resetFiles();

    while (flag) {
        printf("\n=== LIBRARY AUTOMATION SYSTEM ===\n");
        printf("1. Student Operations\n");
        printf("2. Book Operations\n");
        printf("3. Author Operations\n");
        printf("0. Exit\n");
        printf("Enter your choice: ");
        scanf("%d", &choice);

        switch (choice) {
            case 1: studentMenu(headStudent, headBook); break;
            case 2: bookMenu(headBook, headAuthor, &bookAuthorArray, &bookAuthorCount); break;
            case 3: authorMenu(headAuthor); break;
            case 0: printf("Exiting the program. Goodbye!\n"); flag = 0; break;
            default: printf("Invalid choice. Please try again.\n");
        }
    }

    free_list(*headStudent, free_node_student);
    free(headStudent);
    free_list(*headBook, free_node_book);
    free(headBook);
    free_list(*headAuthor, free_node_author);
    free(headAuthor);

    return 0;
}
