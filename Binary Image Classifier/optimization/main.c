#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dirent.h>
#include <time.h>
#include <math.h>

#define MAX_FILES 250 // Adjust to change how many files to be used
#define MAX_PIXELS 625 // Adjust this based on the maximum pixels per image
#define DIMENSIONS 626
#define TRAINDATA 400
#define TESTDATA 100
#define MINI_BATCH 1

void read_pgm_file(const char *filename, double *pixels, int *pixel_count) {
    FILE *file = fopen(filename, "r");
    if (!file) {
        perror("Failed to open file");
        return;
    }

    char format[3];
    int width, height, max_val;
    *pixel_count = 0;

    // Read header
    fscanf(file, "%2s", format);
    if (strcmp(format, "P2") != 0) {
        fprintf(stderr, "File %s is not a valid P2 PGM file\n", filename);
        fclose(file);
        return;
    }

    // Skip comments
    int c;
    while ((c = fgetc(file)) == '#') {
        while (fgetc(file) != '\n');
    }
    ungetc(c, file);

    // Read dimensions and max value
    fscanf(file, "%d %d", &width, &height);
    fscanf(file, "%d", &max_val);

    // Read pixel data and normalize
    for (int i = 0; i < width * height; i++) {
        int pixel_value;
        fscanf(file, "%d", &pixel_value);
        pixels[i] = pixel_value / 255.0f; // Normalize
        (*pixel_count)++;
    }

    fclose(file);
}

void read_pgm_files_in_folder(const char *folder_path, double** pixel_data) {
    struct dirent *entry;
    DIR *dp = opendir(folder_path);

    if (!dp) {
        perror("Unable to open directory");
        return;
    }

    int file_count = 0;

    while ((entry = readdir(dp)) != NULL && file_count <= MAX_FILES-1) {

        if (strstr(entry->d_name, ".pgm")) {
            char file_path[256];
            snprintf(file_path, sizeof(file_path), "%s/%s", folder_path, entry->d_name);

            int pixel_count = 0;
            read_pgm_file(file_path, pixel_data[file_count], &pixel_count);
            //printf("Read %d pixels from %s\n", pixel_count, entry->d_name);
            file_count++;
        }
    }

    printf("Read All the files in one folder. \n");

    closedir(dp);
}

void shuffleBasicArray(double** dataTrain, int size) {

    double* tempRow;

    for (int i = size - 1; i > 0; i--) {

        int j = rand() % (i + 1);


        tempRow = dataTrain[i];
        dataTrain[i] = dataTrain[j];
        dataTrain[j] = tempRow;
    }
}

void shuffleValuedArray(double** dataTrain, double* realValuesTrain, int size) {

    double* tempRow;
    double tempValue;

    for (int i = size - 1; i > 0; i--) {

        int j = rand() % (i + 1);


        tempRow = dataTrain[i];
        dataTrain[i] = dataTrain[j];
        dataTrain[j] = tempRow;


        tempValue = realValuesTrain[i];
        realValuesTrain[i] = realValuesTrain[j];
        realValuesTrain[j] = tempValue;
    }
}

void shuffleFirstValueOnly(double** dataTrain, double* realValuesTrain, int size) {
    if (size <= 1) return;


    int randIndex = 1 + rand() % (size - 1);


    double* tempRow = dataTrain[0];
    dataTrain[0] = dataTrain[randIndex];
    dataTrain[randIndex] = tempRow;


    double tempValue = realValuesTrain[0];
    realValuesTrain[0] = realValuesTrain[randIndex];
    realValuesTrain[randIndex] = tempValue;
}


void print2DArray(double** array, int rows, int cols) {
    if (array == NULL) {
        fprintf(stderr, "Array is NULL.\n");
        return;
    }

    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            printf("%.2f ", array[i][j]);
        }
        printf("\n");
    }

    printf("\n\n");
}

void print1DArray(double* array, int size) {
    if (array == NULL) {
        fprintf(stderr, "Array is NULL.\n");
        return;
    }

    for (int i = 0; i < size; i++) {
        printf("%.4f ", array[i]);
    }
    printf("\n\n");
}


void sliceTrainArray(double** femaleData, double** femaleDataTrain)
{
    for(int i = 0; i<(TRAINDATA/2); i++)
    {
        for(int j = 0; j<MAX_PIXELS; j++)
        {
            femaleDataTrain[i][j] = femaleData[i][j];
        }
    }

    printf("Sliced Train Arrays \n");
}

void sliceTestArray(double** femaleData, double** femaleDataTest)
{
    for(int i = TRAINDATA/2; i<MAX_FILES; i++)
    {
        for(int j = 0; j<MAX_PIXELS; j++)
        {
            femaleDataTest[i-TRAINDATA/2][j] = femaleData[i][j];
        }
    }

    printf("Sliced Test Arrays \n");
}


void combineTrainArray(double** femaleDataTrain, double** maleDataTrain, double** dataTrain, double* realValuesTrain)
{
    for(int i = 0; i< TRAINDATA/2; i++)
    {
        for(int j = 0; j< MAX_PIXELS; j++)
        {
            dataTrain[i][j] = maleDataTrain[i][j];
            realValuesTrain[i] = 1.0;
        }
    }
    for(int i = TRAINDATA/2; i< TRAINDATA; i++)
    {
        for(int j = 0; j< MAX_PIXELS; j++)
        {
            dataTrain[i][j] = femaleDataTrain[i-TRAINDATA/2][j];
            realValuesTrain[i] = -1.0;
        }
    }

    printf("Combined Train Arrays \n");
}

void combineTestArray(double** femaleDataTest, double** maleDataTest, double** dataTest, double* realValuesTest)
{
    for(int i = 0; i< TESTDATA/2; i++)
    {
        for(int j = 0; j< MAX_PIXELS; j++)
        {
            dataTest[i][j] = maleDataTest[i][j];
            realValuesTest[i] = 1.0;
        }
    }
    for(int i = TESTDATA/2; i< TESTDATA; i++)
    {
        for(int j = 0; j< MAX_PIXELS; j++)
        {
            dataTest[i][j] = femaleDataTest[i-TESTDATA/2][j];
            realValuesTest[i] = -1.0;
        }
    }

    printf("Combined Test Arrays \n");
}

double matrix_multiplication(double* weights, double* pixels)
{
    double value = weights[0];

    for(int i = 0; i<DIMENSIONS-1; i++)
    {
        value += weights[i+1] * pixels[i];
    }
    //printf("Matrix Multiplied and found: %f \n", value);
    return value;


}

double findError(double* weights, double* pixels, double realValue)
{
    double error = realValue - tanh(matrix_multiplication(weights, pixels));
    //printf("Error Found as %f\n tanh had %f \n tanh gave %f", error, matrix_multiplication(weights, pixels), tanh(matrix_multiplication(weights, pixels)));
    return error;
}


void createErrorArrayForNData(double* array, double* weights, double** dataTrain, double* realValues,int N)
{
    for(int i = 0; i < N; i++)
    {
        array[i] = findError(weights,dataTrain[i],realValues[i]);
    }
    //printf("Error Array Has been created. \n");
}

double sech(double x) {
    return 1/cosh(x);
}

void createJacobianTransposeForNData(double** array, double* weights, double** dataTrain, int N)
{
    double sechx;
    double sechxArrayForData[N];

    for(int j = 0; j<N;j++)
    {
        sechxArrayForData[j] = sech(matrix_multiplication(weights, dataTrain[j]));
    }

    for(int i = 0; i<DIMENSIONS;i++)
    {
        if (i == 0)
        {
            for(int j = 0; j<N;j++)
            {
                sechx = sechxArrayForData[j];
                array[i][j] = -1 * sechx * sechx;
            }
        }
        else
        {
            for(int j = 0; j<N;j++)
            {
                sechx = sechxArrayForData[j];
                array[i][j] = -1 * dataTrain[j][i-1] * sechx * sechx;
            }
        }


    }
    //printf("Jacobian Transpose has been created. \n");
}

void findTheSubstractForNData(double* result, double eps, double** jacobianTranspose, double* error, int N)
{
    for(int i = 0; i<DIMENSIONS; i++)
    {
        double value = 0.0;

        for(int j = 0; j<N; j++)
        {
            value += jacobianTranspose[i][j] * error[j];
            //printf("%f added to the value new value is %f\n",jacobianTranspose[i][j] * error[j], value );
        }

        //printf("\n\n%f is added to result array \n\n", value );

        result[i] = value*eps;
    }
    //printf("Substract Array has been found. \n");
}

void update(double* oldArray, double* substract)
{
    for(int i = 0; i<DIMENSIONS; i++)
    {
        oldArray[i] = oldArray[i] - substract[i];
    }
    //printf("guess has been updated \n");
}

double sumOfVector(double* substract, int count)
{
    double sum = 0.0;

    for(int i = 0; i<count; i++)
    {
        sum += fabs(substract[i]);
    }

    //printf("Sum of the vector is: %f \n",sum);

    return sum/count;
}

double** allocate2DArray(int rows, int cols) {
    double** array = calloc(rows, sizeof(double*));
    if (array == NULL) {
        fprintf(stderr, "Memory allocation failed for rows\n");
        return NULL;
    }

    for (int i = 0; i < rows; i++) {
        array[i] = calloc(cols, sizeof(double));
        if (array[i] == NULL) {
            fprintf(stderr, "Memory allocation failed for row %d\n", i);
            // Free already allocated memory before returning
            for (int j = 0; j < i; j++) {
                free(array[j]);
            }
            free(array);
            return NULL;
        }
    }

    return array;
}

void free2DArray(double** array, int rows) {
    for (int i = 0; i < rows; i++) {
        free(array[i]);
    }
    free(array);
}

double* allocate1DArray(int size) {
    double* array = calloc(size, sizeof(double));
    if (array == NULL) {
        fprintf(stderr, "Memory allocation failed for 1D array of size %d\n", size);
        return NULL;
    }
    return array;
}

void free1DArray(double* array) {
    free(array);
}

double lossCalculator(double* weights, double** dataSet, double* values, int dataCount)
{
    double loss = 0;
    double add = 0;
    double guess = 0;

    for(int i = 0; i<dataCount; i++)
    {
        //printf("\n%d inci iterasyon\n",i);
        guess = tanh(matrix_multiplication(weights, dataSet[i]));
        add = (values[i] - guess);
        loss += add*add;
        //printf("\n for data %d, calculated tanh is (guess) %f, real value is %f, added loss is: %.3f \n", i,guess,values[i], add*add);
    }

    loss = loss/dataCount*100;
    //printf("Loss is: %f\n", loss);
    return loss;
}

double successCalculator(double* weights, double** dataSet, double* values, int dataCount)
{
    int right = 0;
    int wrong = 0;
    double guess;

    for(int i = 0; i<dataCount; i++)
    {
        //printf("\n%d inci iterasyon\n",i);
        guess = tanh(matrix_multiplication(weights, dataSet[i]));
        if (fabs(guess - values[i]) < 1)
        {
            right++;
        }
        else{
            wrong++;
        }
        //printf("\n for data %d, calculated tanh is (guess) %f, real value is %f, added loss is: %.3f \n", i,guess,values[i], add*add);
    }
    printf("\nRight: %d, Wrong: %d", right, wrong);
    return ((double)right / dataCount) * 100;
}

void getSquareMatrix(double* matrix, int dimension)
{
    double a;
    for(int i = 0; i<dimension; i++)
    {
        a = matrix[i];
        matrix[i] = a*a;
    }
}

void multiplyMatrix(double* matrix,int dimension, double value)
{
    double a;
    for(int i = 0; i<dimension; i++)
    {
        a = matrix[i];
        matrix[i] = a*value;
    }
}

void saveArrayToFile(const char *filename, double *array, int size) {
    // Dosyayi "append" modunda aciyoruz
    FILE *file = fopen(filename, "a");
    if (file == NULL) {
        perror("Dosya acilamadi");
        exit(EXIT_FAILURE);
    }

    // Iterasyon numarasini yaz
    //fprintf(file, "Iterasyon %d:\n", iteration);

    // Diziyi dosyaya yaz
    for (int i = 0; i < size; i++) {
        fprintf(file, "%.6f ", array[i]); // 6 basamak hassasiyetle yaziyoruz
    }
    fprintf(file, "\n"); // Satir sonu

    // Dosyayi kapat
    fclose(file);
}

void saveDoubleToFile(const char *filename, double value)
{
    // Dosyayi "append" modunda aciyoruz
    FILE *file = fopen(filename, "a");
    if (file == NULL) {
        perror("Dosya acilamadi");
        exit(EXIT_FAILURE);
    }

    // Diziyi dosyaya yaz
    fprintf(file, "%.6f ", value); // 6 basamak hassasiyetle yaziyoruz

    fprintf(file, "\n"); // Satir sonu

    // Dosyayi kapat
    fclose(file);
}

void saveIntToFile(const char *filename, int value)
{
    // Dosyayi "append" modunda aciyoruz
    FILE *file = fopen(filename, "a");
    if (file == NULL) {
        perror("Dosya acilamadi");
        exit(EXIT_FAILURE);
    }

    // Diziyi dosyaya yaz
    fprintf(file, "%d", value); // 6 basamak hassasiyetle yaziyoruz

    fprintf(file, "\n"); // Satir sonu

    // Dosyayi kapat
    fclose(file);
}

void clearFile(const char *filename) {
    // Dosyayi yazma modunda ac ("w")
    FILE *file = fopen(filename, "w");
    if (file == NULL) {
        perror("Dosya acilamadi");
        exit(EXIT_FAILURE);
    }

    // Dosya iceriğini temizlemek icin hiçbir şey yazmadan kapatiyoruz
    fclose(file);
    printf("%s dosyasi temizlendi.\n", filename);
}

int main() {
    printf("Start: \n");

    srand(time(NULL));

    double** femaleData = allocate2DArray(MAX_FILES, MAX_PIXELS);
    double** maleData = allocate2DArray(MAX_FILES, MAX_PIXELS);

    const char *folder_path = "../HeShe/female3";
    read_pgm_files_in_folder(folder_path, femaleData);

    folder_path = "../HeShe/male3";
    read_pgm_files_in_folder(folder_path, maleData);

    //shuffleBasicArray(maleData, MAX_FILES);
    //shuffleBasicArray(femaleData, MAX_FILES);


    double** femaleDataTrain = allocate2DArray(TRAINDATA/2, MAX_PIXELS);
    double** femaleDataTest = allocate2DArray(TESTDATA/2, MAX_PIXELS);

    double** maleDataTrain = allocate2DArray(TRAINDATA/2, MAX_PIXELS);
    double** maleDataTest = allocate2DArray(TESTDATA/2, MAX_PIXELS);

    sliceTrainArray(femaleData, femaleDataTrain);
    sliceTestArray(femaleData, femaleDataTest);

    sliceTrainArray(maleData, maleDataTrain);
    sliceTestArray(maleData, maleDataTest);

    free2DArray(femaleData,MAX_FILES);
    free2DArray(maleData,MAX_FILES);


    double** dataTrain = allocate2DArray(TRAINDATA, MAX_PIXELS);
    double* realValuesTrain = allocate1DArray(TRAINDATA);


    double** dataTest = allocate2DArray(TESTDATA, MAX_PIXELS);
    double* realValuesTest = allocate1DArray(TESTDATA);



    combineTrainArray(femaleDataTrain,maleDataTrain,dataTrain,realValuesTrain);
    combineTestArray(femaleDataTest,maleDataTest,dataTest,realValuesTest);

    free2DArray(femaleDataTrain,TRAINDATA/2);
    free2DArray(femaleDataTest,TESTDATA/2);

    free2DArray(maleDataTrain,TRAINDATA/2);
    free2DArray(maleDataTest,TESTDATA/2);

    double* firstGuess = allocate1DArray(DIMENSIONS);
    double* guess = allocate1DArray(DIMENSIONS);

    double aralik = 0.09;

    for (int i = 0; i<DIMENSIONS;i++)
    {
        firstGuess[i] = ((double)rand() / RAND_MAX) * 2 * aralik - aralik;
    }

    double* errorArray = allocate1DArray(TRAINDATA);

    double** jacobianTranspose = allocate2DArray(DIMENSIONS, TRAINDATA);

    double* substractArray = allocate1DArray(DIMENSIONS);



    int count = 0;
    double degisim = 1;

    double treshold1 = 0.0000035;
    double treshold2 = 0.0000003;
    double treshold3 = 0.000001;

    int countNumber1 = 1500;
    int countNumber2 = 3000;
    int countNumber3 = 3000;

    double loss1 = 31;
    double loss2 = 31;
    double success1 = 31;
    double success2 = 31;

    printf("Optimzation is starting... \n");

    //print2DArray(jacobianTranspose, DIMENSIONS, TRAINDATA);
    //print2DArray(dataTrain, TRAINDATA, MAX_PIXELS);


    clock_t start;
    clock_t end;
    double time_spent;

    //////////////////////////////////////////////////////////Gradient Descent While loop

    double eps = 0.000033;

    for (int i = 0; i<DIMENSIONS;i++)
    {
        guess[i] = firstGuess[i];
    }


    clearFile("../Bkismi/W1/gd/weights.txt");
    clearFile("../Bkismi/W1/gd/loss_train.txt");
    clearFile("../Bkismi/W1/gd/loss_test.txt");
    clearFile("../Bkismi/W1/gd/epoch.txt");
    clearFile("../Bkismi/W1/gd/time.txt");
    clearFile("../Bkismi/W1/gd/success_train.txt");
    clearFile("../Bkismi/W1/gd/success_test.txt");

    start = clock();

    while (count < countNumber1 && degisim > treshold1)
    {

        saveArrayToFile("../Bkismi/W1/gd/weights.txt", guess, DIMENSIONS);

        loss1 = lossCalculator(guess, dataTrain, realValuesTrain, TRAINDATA);
        loss2 = lossCalculator(guess, dataTest, realValuesTest, TESTDATA);

        success1 = successCalculator(guess, dataTrain, realValuesTrain, TRAINDATA);
        success2 = successCalculator(guess, dataTest, realValuesTest, TESTDATA);


        printf("\n%d th iteration: \n", count+1);
        printf("Train loss: %.2f, Test loss: %.2f\nTrain success: %.2f%%, Test success: %.2f%%\n", loss1, loss2, success1, success2);

        saveDoubleToFile("../Bkismi/W1/gd/loss_train.txt", loss1);
        saveDoubleToFile("../Bkismi/W1/gd/loss_test.txt", loss2);
        saveDoubleToFile("../Bkismi/W1/gd/success_train.txt", success1);
        saveDoubleToFile("../Bkismi/W1/gd/success_test.txt", success2);

        createErrorArrayForNData(errorArray, guess, dataTrain, realValuesTrain, TRAINDATA);
        //print1DArray(errorArray, TRAINDATA);

        createJacobianTransposeForNData(jacobianTranspose, guess,dataTrain, TRAINDATA);
        //print2DArray(jacobianTranspose, DIMENSIONS, TRAINDATA);

        findTheSubstractForNData(substractArray,eps,jacobianTranspose,errorArray, TRAINDATA);
        //print1DArray(substractArray, DIMENSIONS);

        update(guess,substractArray);

        degisim = sumOfVector(substractArray, DIMENSIONS);

        count++;
    }

    end = clock();
    time_spent = (double)(end - start) / CLOCKS_PER_SEC;
    printf("\n\nTime taken: %.6f seconds\n", time_spent);

    saveDoubleToFile("../Bkismi/W1/gd/time.txt", time_spent);
    saveIntToFile("../Bkismi/W1/gd/epoch.txt", count);

//    printf("----------------------\n(eps is : %f) \nError Value Now is %.6f \n",eps,lossCalculator(guess, dataTrain, realValuesTrain, TRAINDATA));
//    printf("Error Value Now is %.6f \n----------------------\n",lossCalculator(guess, dataTest, realValuesTest, TESTDATA));

    ///////////////////////////////////////////////////////////////////////////////////////////////Stochastic Gradient Descent While loop

    free1DArray(errorArray);
    free2DArray(jacobianTranspose,DIMENSIONS);

    errorArray = allocate1DArray(MINI_BATCH);

    jacobianTranspose = allocate2DArray(DIMENSIONS, MINI_BATCH);

    count = 0;
    degisim = 1;
    eps = 0.0002;

    for (int i = 0; i<DIMENSIONS;i++)
    {
        guess[i] = firstGuess[i];
    }

    clearFile("../Bkismi/W1/sgd/weights.txt");
    clearFile("../Bkismi/W1/sgd/loss_train.txt");
    clearFile("../Bkismi/W1/sgd/loss_test.txt");
    clearFile("../Bkismi/W1/sgd/epoch.txt");
    clearFile("../Bkismi/W1/sgd/time.txt");
    clearFile("../Bkismi/W1/sgd/success_train.txt");
    clearFile("../Bkismi/W1/sgd/success_test.txt");

    start = clock();

    while (count < countNumber2 && degisim > treshold2)
    {
        saveArrayToFile("../Bkismi/W1/sgd/weights.txt", guess, DIMENSIONS);
        shuffleFirstValueOnly(dataTrain,realValuesTrain,TRAINDATA);

        loss1 = lossCalculator(guess, dataTrain, realValuesTrain, TRAINDATA);
        loss2 = lossCalculator(guess, dataTest, realValuesTest, TESTDATA);

        success1 = successCalculator(guess, dataTrain, realValuesTrain, TRAINDATA);
        success2 = successCalculator(guess, dataTest, realValuesTest, TESTDATA);

        printf("\n%d th iteration: \n", count+1);
        printf("Train loss: %.2f, Test loss: %.2f\nTrain success: %.2f%%, Test success: %.2f%%\n", loss1, loss2, success1, success2);

        saveDoubleToFile("../Bkismi/W1/sgd/loss_train.txt", loss1);
        saveDoubleToFile("../Bkismi/W1/sgd/loss_test.txt", loss2);
        saveDoubleToFile("../Bkismi/W1/sgd/success_train.txt", success1);
        saveDoubleToFile("../Bkismi/W1/sgd/success_test.txt", success2);

        createErrorArrayForNData(errorArray, guess, dataTrain, realValuesTrain, MINI_BATCH);
        //print1DArray(errorArray, TRAINDATA);

        createJacobianTransposeForNData(jacobianTranspose, guess,dataTrain, MINI_BATCH);
        //print2DArray(jacobianTranspose, DIMENSIONS, TRAINDATA);

        findTheSubstractForNData(substractArray,eps,jacobianTranspose,errorArray, MINI_BATCH);
        //print1DArray(substractArray, DIMENSIONS);

        update(guess,substractArray);

        degisim = sumOfVector(substractArray, DIMENSIONS);

        count++;
    }

    end = clock();
    time_spent = (double)(end - start) / CLOCKS_PER_SEC;
    printf("\n\nTime taken: %.6f seconds\n", time_spent);

    saveDoubleToFile("../Bkismi/W1/sgd/time.txt", time_spent);
    saveIntToFile("../Bkismi/W1/sgd/epoch.txt", count);


//    printf("----------------------\n(eps is : %f) \nFirst error value of Train: %0.6f \nError Value Now is %.6f \n",eps,firstLossOfTrain,lossCalculator(guess, dataTrain, realValuesTrain, TRAINDATA));
//    printf("First error value of Test: %0.8f \nError Value Now is %.6f \n----------------------\n",firstLossOfTest,lossCalculator(guess, dataTest, realValuesTest, TESTDATA));


    /////////////////////////////////////////////////////////////////// ADAM


    for (int i = 0; i<DIMENSIONS;i++)
    {
        guess[i] = firstGuess[i];
    }


    double stepsize = 0.0001;
    double epsilon = 0.00000001;
    double beta1 = 0.9;
    double beta2 = 0.999;
    double* mt = allocate1DArray(DIMENSIONS);
    double* vt = allocate1DArray(DIMENSIONS);
    double* mt_head = allocate1DArray(DIMENSIONS);
    double* vt_head = allocate1DArray(DIMENSIONS);
    count = 0;
    degisim = 1;

    clearFile("../Bkismi/W1/adam/weights.txt");
    clearFile("../Bkismi/W1/adam/loss_train.txt");
    clearFile("../Bkismi/W1/adam/loss_test.txt");
    clearFile("../Bkismi/W1/adam/epoch.txt");
    clearFile("../Bkismi/W1/adam/time.txt");
    clearFile("../Bkismi/W1/adam/success_train.txt");
    clearFile("../Bkismi/W1/adam/success_test.txt");

    start = clock();

    while(count < countNumber3 && degisim > treshold3)
    {
        count++;
        saveArrayToFile("../Bkismi/W1/adam/weights.txt", guess, DIMENSIONS);

        loss1 = lossCalculator(guess, dataTrain, realValuesTrain, TRAINDATA);
        loss2 = lossCalculator(guess, dataTest, realValuesTest, TESTDATA);

        saveDoubleToFile("../Bkismi/W1/adam/loss_train.txt", loss1);
        saveDoubleToFile("../Bkismi/W1/adam/loss_test.txt", loss2);

        success1 = successCalculator(guess, dataTrain, realValuesTrain, TRAINDATA);
        success2 = successCalculator(guess, dataTest, realValuesTest, TESTDATA);
        saveDoubleToFile("../Bkismi/W1/adam/success_train.txt", success1);
        saveDoubleToFile("../Bkismi/W1/adam/success_test.txt", success2);


        printf("\n%d th iteration: \n", count);
        printf("Train loss: %.2f, Test loss: %.2f\nTrain success: %.2f%%, Test success: %.2f%%\n", loss1, loss2, success1, success2);

        shuffleFirstValueOnly(dataTrain,realValuesTrain,TRAINDATA);

        createErrorArrayForNData(errorArray, guess, dataTrain, realValuesTrain, MINI_BATCH);

        createJacobianTransposeForNData(jacobianTranspose, guess,dataTrain, MINI_BATCH);

        findTheSubstractForNData(substractArray,1.0,jacobianTranspose,errorArray, MINI_BATCH);

        degisim = 0;

        for (int i = 0; i<DIMENSIONS; i++)
        {
            mt[i] = mt[i] * beta1 + (1.0-beta1) * substractArray[i];
        }

        getSquareMatrix(substractArray, DIMENSIONS);

        for (int i = 0; i<DIMENSIONS; i++)
        {
            vt[i] = vt[i] * beta2 + (1.0-beta2) * substractArray[i];
        }

        ////////////////////////////////////////////////////////

        for (int i = 0; i<DIMENSIONS; i++)
        {
            mt_head[i] = mt[i]/(1.0 - pow(beta1, count));
        }

        for (int i = 0; i<DIMENSIONS; i++)
        {
            vt_head[i] = vt[i]/(1.0 - pow(beta2, count));
        }

        ////////////////////////////////////////////////

        for (int i = 0; i<DIMENSIONS; i++)
        {
            double fark = stepsize * (mt_head[i]/(sqrt(vt_head[i])+epsilon));
            guess[i] = guess[i] - fark;
            degisim += fabs(fark);
        }

        degisim /= DIMENSIONS;

    }

    end = clock();
    time_spent = (double)(end - start) / CLOCKS_PER_SEC;
    printf("\n\nTime taken: %.6f seconds\n", time_spent);

    saveDoubleToFile("../Bkismi/W1/adam/time.txt", time_spent);
    saveIntToFile("../Bkismi/W1/adam/epoch.txt", count);

//    printf("----------------------\nstepsize is: %f\nFirst error value of Train: %0.6f \nError Value Now is %.6f \n",stepsize,firstLossOfTrain,lossCalculator(guess, dataTrain, realValuesTrain, TRAINDATA));
//    printf("First error value of Test: %0.8f \nError Value Now is %.6f \n----------------------\n",firstLossOfTest,lossCalculator(guess, dataTest, realValuesTest, TESTDATA));

    ///////////////////////////////////////////////

    free2DArray(dataTrain,TRAINDATA);
    free1DArray(realValuesTrain);

    free2DArray(dataTest,TESTDATA);
    free1DArray(realValuesTest);

    free1DArray(guess);
    free1DArray(firstGuess);
    free1DArray(errorArray);
    free2DArray(jacobianTranspose,DIMENSIONS);
    free1DArray(substractArray);

    free1DArray(mt);
    free1DArray(vt);
    free1DArray(mt_head);
    free1DArray(vt_head);

    return 0;
}
