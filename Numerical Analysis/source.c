#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <math.h>

#define MAX_TOKEN_LEN 20
#define MAX_TOKENS 100
#define true 1
#define false 0
#define EPSILON 1e-9

#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif

typedef enum
{
    T_NUMBER,
    T_VARIABLE,
    T_OPERATOR,       /* Binary + - * / ^ */
    T_UNARY_OPERATOR, /* Unary - or + */
    T_FUNCTION,
    T_LPAREN,
    T_RPAREN,
    T_COMMA
} TokenType;


typedef struct
{
    TokenType type;
    char str[MAX_TOKEN_LEN];
    double value;
} Token;

int is_function(const char* s)
{
    int i;

    const char* functions[] =
    {
        "sin", "cos", "tan", "cot", "csc", "sec",
        "arcsin", "arccos", "arctan", "arccot", "arccsc", "arcsec",
        "log"
    };
    for (i = 0; i < sizeof(functions)/sizeof(functions[0]); i++)
    {
        if (strncmp(s, functions[i], strlen(functions[i])) == 0)
            return true;
    }
    return false;
}

typedef struct
{
    Token items[MAX_TOKENS];
    int top; /*Starts with -1 so it has the last token on the top's index*/
} TokenStack;


void initStack(TokenStack* s)
{
    s->top = -1;
}
int isEmpty(TokenStack* s)
{
    return s->top == -1;
}
void push(TokenStack* s, Token t)
{
    s->items[++s->top] = t;
}
Token pop(TokenStack* s)
{
    return s->items[s->top--];
}
Token peek(TokenStack* s)
{
    return s->items[s->top];
}


int tokenize(const char* expr, Token* tokens)
{
    int i = 0; /* index in expr */
    int t = 0; /* index in tokens */
    int start;

    TokenType prevType = T_OPERATOR;

    while (expr[i] != '\0')
    {
        if (isspace(expr[i]))
        {
            /* printf("space found"); */
            i++;
        }
        else if (isdigit(expr[i]) || (expr[i] == '.' && isdigit(expr[i + 1])))
        {

            start = i;
            char numberStr[MAX_TOKEN_LEN];

            while (isdigit(expr[i]) || expr[i] == '.') i++;

            strncpy(numberStr, &expr[start], i - start);
            numberStr[i - start] = '\0';

            tokens[t].type = T_NUMBER;
            tokens[t].value = atof(numberStr);
            strcpy(tokens[t].str, numberStr);
            prevType = T_NUMBER;
            t++;
        }
        else if (strncmp(&expr[i], "pi", 2) == 0)
        {
            tokens[t].type = T_NUMBER;
            tokens[t].value = 3.141592653589793;
            strcpy(tokens[t].str, "pi");
            i += 2;
            prevType = T_NUMBER;
            t++;
        }
        else if (expr[i] == 'e')
        {
            tokens[t].type = T_NUMBER;
            tokens[t].value = 2.718281828459045;
            strcpy(tokens[t].str, "e");
            i++;
            prevType = T_NUMBER;
            t++;
        }
        else if (expr[i] == 'x')
        {
            tokens[t].type = T_VARIABLE;
            strcpy(tokens[t].str, "x");
            i++;
            prevType = T_VARIABLE;
            t++;
        }
        else if (isalpha(expr[i]))
        {
            int start = i;
            char name[MAX_TOKEN_LEN];


            while (isalpha(expr[i])) i++;

            strncpy(name, &expr[start], i - start);
            name[i - start] = '\0';

            if (is_function(name))
            {
                tokens[t].type = T_FUNCTION;
                strcpy(tokens[t].str, name);
                prevType = T_FUNCTION;
                t++;
            }
            else
            {
                printf("Unknown function or identifier: %s\n", name);
                exit(1);
            }
        }
        else if (strchr("+-*/^", expr[i]))
        {
            TokenType opType = T_OPERATOR;
            char op = expr[i];

            if (t == 0 ||
                    prevType == T_OPERATOR ||
                    prevType == T_LPAREN ||
                    prevType == T_COMMA ||
                    prevType == T_UNARY_OPERATOR)
            {
                if (op == '+' || op == '-')
                {
                    opType = T_UNARY_OPERATOR;
                }
            }

            tokens[t].type = opType;
            tokens[t].str[0] = op;
            tokens[t].str[1] = '\0';
            prevType = opType;
            t++;
            i++;
        }
        else if (expr[i] == '(')
        {
            tokens[t].type = T_LPAREN;
            strcpy(tokens[t].str, "(");
            prevType = T_LPAREN;
            t++;
            i++;
        }
        else if (expr[i] == ')')
        {
            tokens[t].type = T_RPAREN;
            strcpy(tokens[t].str, ")");
            prevType = T_RPAREN;
            t++;
            i++;
        }
        else if (expr[i] == ',')
        {
            tokens[t].type = T_COMMA;
            strcpy(tokens[t].str, ",");
            prevType = T_COMMA;
            t++;
            i++;
        }
        else
        {
            printf("Unknown character: %c\n", expr[i]);
            exit(1);
        }
    }

    return (t-1);
}

void printTokenStack(TokenStack* tokenStack)
{

    int i;
    int max = tokenStack->top+1;

    for(i = 0; i<max; i++)
    {
        printf("%s, ", tokenStack->items[i].str);
    }
    printf("\n");
}

int precedence(Token op)
{
    if (op.type == T_UNARY_OPERATOR)
    {
        return 4;
    }
    else if (strcmp(op.str,"^") == 0)
    {
        return 3;
    }
    else if (strcmp(op.str,"*") == 0 || strcmp(op.str,"/") == 0)
    {
        return 2;
    }
    else if (strcmp(op.str,"+") == 0 || strcmp(op.str,"-") == 0)
    {
        return 1;
    }
    else if (strcmp(op.str,"(") == 0)
    {
        return 0;
    }
    return -1;
}

void convertInfixToPostfix(TokenStack* infix, TokenStack* opStack, TokenStack* postfix)
{
    Token currentToken;
    int i;
    int max = infix->top;

    for (i = 0; i <= max; i++)
    {
        currentToken = infix->items[i];

        if (currentToken.type == T_NUMBER || currentToken.type == T_VARIABLE)
        {
            push(postfix, currentToken);
        }
        else if (currentToken.type == T_OPERATOR || currentToken.type == T_UNARY_OPERATOR)
        {
            while (!isEmpty(opStack) && precedence(peek(opStack)) >= precedence(currentToken))
            {
                push(postfix, pop(opStack));
            }
            push(opStack, currentToken);
        }
        else if (currentToken.type == T_FUNCTION)
        {
            push(opStack, currentToken);
        }
        else if (currentToken.type == T_LPAREN)
        {
            push(opStack, currentToken);
        }
        else if (currentToken.type == T_RPAREN)
        {
            while (!isEmpty(opStack) && peek(opStack).type != T_LPAREN)
            {
                push(postfix, pop(opStack));
            }
            if (!isEmpty(opStack)) pop(opStack);


            if (!isEmpty(opStack) && peek(opStack).type == T_FUNCTION)
            {
                push(postfix, pop(opStack));
            }
        }
        else if (currentToken.type == T_COMMA)
        {
            while (!isEmpty(opStack) && peek(opStack).type != T_LPAREN)
            {
                push(postfix, pop(opStack));
            }

        }
    }

    while (!isEmpty(opStack))
    {
        push(postfix, pop(opStack));
    }
}


void clearStack(TokenStack* stack)
{
    while (!isEmpty(stack))
    {
        pop(stack);
    }
}

double calculateFunction(TokenStack* postfix, double xValue)
{

    double result;
    Token currentToken;
    int i;
    TokenStack* calculatorStack;
    int max = postfix->top;

    calculatorStack = (TokenStack*) malloc(sizeof(TokenStack));
    initStack(calculatorStack);

    for (i = 0; i <= max; i++)
    {
        currentToken = postfix->items[i];

        if (currentToken.type == T_NUMBER)
        {

            push(calculatorStack, currentToken);

        }
        else if (currentToken.type == T_VARIABLE)
        {

            Token var = { .type = T_NUMBER, .value = xValue };
            push(calculatorStack, var);

        }
        else if (currentToken.type == T_OPERATOR)
        {

            double b = pop(calculatorStack).value;
            double a = pop(calculatorStack).value;
            double r = 0;

            if (strcmp(currentToken.str, "+") == 0) r = a + b;
            else if (strcmp(currentToken.str, "-") == 0) r = a - b;
            else if (strcmp(currentToken.str, "*") == 0) r = a * b;
            else if (strcmp(currentToken.str, "/") == 0) r = a / b;
            else if (strcmp(currentToken.str, "^") == 0) r = pow(a, b);

            push(calculatorStack, (Token)
            {
                .type = T_NUMBER, .value = r
            });

        }
        else if (currentToken.type == T_UNARY_OPERATOR)
        {

            double a = pop(calculatorStack).value;
            double r = 0;

            if (strcmp(currentToken.str, "+") == 0) r = a;
            else if (strcmp(currentToken.str, "-") == 0) r = -a;

            push(calculatorStack, (Token)
            {
                .type = T_NUMBER, .value = r
            });

        }
        else if (currentToken.type == T_FUNCTION)
        {
            double b = pop(calculatorStack).value;
            double a;
            double r = 0;

            if (strcmp(currentToken.str, "sin") == 0) r = sin(b);
            else if (strcmp(currentToken.str, "cos") == 0) r = cos(b);
            else if (strcmp(currentToken.str, "tan") == 0) r = tan(b);
            else if (strcmp(currentToken.str, "cot") == 0) r = 1/tan(b);
            else if (strcmp(currentToken.str, "csc") == 0) r = 1/sin(b);
            else if (strcmp(currentToken.str, "sec") == 0) r = 1/cos(b);

            else if (strcmp(currentToken.str, "arcsin") == 0) r = asin(b);
            else if (strcmp(currentToken.str, "arccos") == 0) r = acos(b);
            else if (strcmp(currentToken.str, "arctan") == 0) r = atan(b);
            else if (strcmp(currentToken.str, "arccot") == 0) r = (M_PI / 2.0) - atan(b);
            else if (strcmp(currentToken.str, "arccsc") == 0) r = asin(1.0/b);
            else if (strcmp(currentToken.str, "arcsec") == 0) r = acos(1.0/b);

            else if (strcmp(currentToken.str, "log") == 0)
            {
                a = pop(calculatorStack).value;
                r = log(b)/log(a);
            }


            push(calculatorStack, (Token)
            {
                .type = T_NUMBER, .value = r
            });
        }

    }

    result = pop(calculatorStack).value;

    free(calculatorStack);
    return result;
}

TokenStack* getFunction(TokenStack* postfixTokenStack)
{

    char* expr;
    TokenStack* infixTokenStack;
    TokenStack* operatorStack;


    expr = (char*) malloc(100*sizeof(char));
    infixTokenStack = (TokenStack*) malloc(sizeof(TokenStack));
    operatorStack = (TokenStack*) malloc(sizeof(TokenStack));

    initStack(infixTokenStack);
    initStack(postfixTokenStack);
    initStack(operatorStack);

    printf("Write the function: ");
    scanf(" %[^\n]", expr);

    infixTokenStack->top = tokenize(expr, infixTokenStack->items);

    convertInfixToPostfix(infixTokenStack, operatorStack, postfixTokenStack);

    free(expr);
    free(infixTokenStack);
    free(operatorStack);

    return postfixTokenStack;
}

double getDouble(char* name)
{
    double result;
    printf("Write the %s: ",name);
    scanf("%lf",&result);
    return result;
}

int getInt(char* name)
{
    int result;
    printf("Write the %s: ",name);
    scanf("%d",&result);
    return result;
}

void bisectionMethod()
{
    TokenStack* function;
    double start, end;
    double error;
    int maxIteration;

    int i=1;
    double fark, newPoint;
    double funcStart, funcEnd, funcNew;


    function  = (TokenStack*) malloc(sizeof(TokenStack));
    getFunction(function);

    start = getDouble("starting point");
    end = getDouble("ending point");
    error = getDouble("error");
    maxIteration = getInt("max iteration count");

    fark = fabs(end-start);
    while(i<=maxIteration&&fark>error)
    {
        funcStart = calculateFunction(function, start);
        funcEnd = calculateFunction(function, end);
        newPoint = (end+start)/2;
        funcNew = calculateFunction(function, newPoint);

        printf("\niteration number: %d\n", i);
        printf("starting: %lf \n", start);
        printf("ending point: %lf\n", end);
        printf("new point: %lf\n", newPoint);

        if(funcNew*funcStart < 0)
        {

            printf("f(%lf)*f(%lf) < 0\nNew ending point is: %lf\n", start,newPoint,newPoint);
            fark = fabs(end-newPoint);
            end= newPoint;

        }
        else if(funcNew*funcEnd < 0)
        {

            printf("f(%lf)*f(%lf) < 0\nNew starting point is: %lf\n", newPoint,end,newPoint);
            fark = fabs(start-newPoint);
            start = newPoint;

        }
        else
        {

            if(funcStart == 0)
            {
                printf("Starting point is the root.\n");
                newPoint = start;
            }
            else if(funcNew == 0)
            {
                printf("New point is the root.\n");
            }
            else if(funcEnd == 0)
            {
                printf("Ending point is the root.\n");
                newPoint = end;
            }
            else
            {
                printf("There are not roots between those points.\n");
            }
            free(function);
            return;

        }

        i++;
    }

    printf("\nIteration ended. Point found as: %lf, with delta_x: %lf, also f(%lf) = %lf\n\n", newPoint, fark, newPoint, funcNew);

    free(function);
    return;
}

void regulaFalsi()
{

    TokenStack* function;
    double start, end;
    double error;
    int maxIteration;

    int i=1;
    double fark, newPoint;
    double funcStart, funcEnd, funcNew;


    function  = (TokenStack*) malloc(sizeof(TokenStack));
    getFunction(function);

    start = getDouble("starting point");
    end = getDouble("ending point");
    error = getDouble("error");
    maxIteration = getInt("max iteration count");

    fark = fabs(end-start);
    while(i<=maxIteration&&fark>error)
    {
        funcStart = calculateFunction(function, start);
        funcEnd = calculateFunction(function, end);
        newPoint = (end*funcStart-start*funcEnd)/(funcStart-funcEnd);
        funcNew = calculateFunction(function, newPoint);

        printf("\niteration number: %d\n", i);
        printf("starting: %lf \n", start);
        printf("ending point: %lf\n", end);
        printf("new point: %lf\n", newPoint);

        if(funcNew*funcStart < 0)
        {

            printf("f(%lf)*f(%lf) < 0\nNew ending point is: %lf\n", start,newPoint,newPoint);
            fark = fabs(end-newPoint);
            end= newPoint;

        }
        else if(funcNew*funcEnd < 0)
        {

            printf("f(%lf)*f(%lf) < 0\nNew starting point is: %lf\n", newPoint,end,newPoint);
            fark = fabs(start-newPoint);
            start = newPoint;

        }
        else
        {

            if(funcStart == 0)
            {
                printf("Starting point is the root.\n");
                newPoint = start;
            }
            else if(funcNew == 0)
            {
                printf("New point is the root.\n");
            }
            else if(funcEnd == 0)
            {
                printf("Ending point is the root.\n");
                newPoint = end;
            }
            else
            {
                printf("There are not roots between those points.\n");
            }
            free(function);
            return;

        }

        i++;
    }

    printf("\nIteration ended. Point found as: %lf, with delta_x: %lf, also f(%lf) = %lf\n\n", newPoint, fark, newPoint, funcNew);

    free(function);
    return;
}

void numericalDiff()
{

    TokenStack* function;
    double x;
    double h;

    double back, central, forward;

    function  = (TokenStack*) malloc(sizeof(TokenStack));
    getFunction(function);

    x = getDouble("the point you want to derivate");
    h = getDouble("the value of h");

    back = calculateFunction(function, x-h);
    central = calculateFunction(function, x);
    forward = calculateFunction(function, x+h);

    printf("\nBack numerical differentiation: %lf\n", ((central-back)/(h)) );
    printf("Central numerical differentiation: %lf\n", ((forward-back)/(2*h)) );
    printf("Forward numerical differentiation: %lf\n\n", ((forward-central)/(h)) );

    free(function);
    return;
}

double derivative(TokenStack* function, double x)
{

    double h = 0.001;
    double back, forward;

    back = calculateFunction(function, x-h);
    forward = calculateFunction(function, x+h);

    return ((forward-back)/(2*h));
}

void newtonRaphson()
{

    TokenStack* function;
    double start,temp,error,fark=100;
    int maxIteration,i=0;

    function  = (TokenStack*) malloc(sizeof(TokenStack));

    getFunction(function);
    start = getDouble("starting point");
    error = getDouble("error");
    maxIteration = getInt("maximum iteration");

    printf("x_0: %lf\n\n", start);

    while(i<maxIteration && fark>error)
    {

        temp = start - (calculateFunction(function,start)/derivative(function,start));
        fark = fabs(start-temp);
        start = temp;

        printf("%dth iteration\n",i+1);
        printf("x_%d: %lf\n\n",i+1,start);

        i++;
    }

    printf("\nIteration ended. Point found as: %lf, with delta_x: %lf, also f(%lf) = %lf\n\n", start, fark, start, calculateFunction(function,start));


    free(function);
    return;
}


double** createMatrix(m,n)
{

    double** matrix;
    int i;

    matrix = (double**) malloc(m*sizeof(double*));

    for(i=0; i<m; i++)
    {
        matrix[i] = (double*) malloc(n*sizeof(double));
    }

    return matrix;
}

void freeMatrix(double** matrix, int m)
{

    int i;

    for(i=0; i<m; i++)
    {
        free(matrix[i]);
    }
    free(matrix);

    return;
}

double** getMatrix(double** matrix, int m, int n)
{

    int i,j;

    for(i = 0; i<m; i++)
    {
        printf("\nEnter Matrix[%d]: ", i);

        for(j=0; j<n; j++)
        {
            scanf("%lf", (matrix[i]+j));
        }
    }

    return matrix;
}

void printMatrix(double** matrix, int m, int n)
{

    int i,j;

    printf("\n");

    for(i = 0; i<m; i++)
    {

        for(j=0; j<n; j++)
        {
            printf("%8.6lf,  ", matrix[i][j]);
        }

        printf("\n");
    }

    printf("\n");

    return;
}

void inverseMatrix()
{

    double **matrix, **augmented, **inverse;
    int i,j,k;
    int m,n;

    m = getInt("row (m)");
    n = getInt("column (n)");

    matrix = createMatrix(m,n);
    augmented = createMatrix(m, 2*n);
    inverse = createMatrix(m,n);

    matrix = getMatrix(matrix, m, n);

    for (i = 0; i < m; i++)
    {
        for (j = 0; j < 2 * n; j++)
        {
            if (j < n)
                augmented[i][j] = matrix[i][j];
            else
                augmented[i][j] = (n == (j - i)) ? 1.0 : 0.0;
        }
    }

    for (i = 0; i < n; i++)
    {

        double pivot;

        /* Partial pivoting */
        if (fabs(augmented[i][i]) < EPSILON)
        {
            int swapRow = -1;
            for (k = i + 1; k < n; k++)
            {
                if (fabs(augmented[k][i]) > EPSILON)
                {
                    swapRow = k;
                    break;
                }
            }
            if (swapRow == -1)
            {
                printf("Matrix is singular and cannot be inverted.\n");
                return;
            }
            /* Swap rows */
            for (j = 0; j < 2 * n; j++)
            {
                double temp = augmented[i][j];
                augmented[i][j] = augmented[swapRow][j];
                augmented[swapRow][j] = temp;
            }
        }

        /* Normalize row i */
        pivot = augmented[i][i];
        for (j = 0; j < 2 * n; j++)
            augmented[i][j] /= pivot;

        /* Eliminate other rows */
        for (k = 0; k < n; k++)
        {
            if (k != i)
            {
                double factor = augmented[k][i];
                for (j = 0; j < 2 * n; j++)
                    augmented[k][j] -= factor * augmented[i][j];
            }
        }

    }

    /* Extract inverse matrix */
    for (i = 0; i < n; i++)
        for (j = 0; j < n; j++)
            inverse[i][j] = augmented[i][j + n];

    printf("Inverse Matrix:\n");
    printMatrix(inverse,m,n);

    freeMatrix(matrix, m);
    freeMatrix(augmented, m);
    freeMatrix(inverse, m);
    return;
}

double* createArray(int size)
{

    double* array;
    array = (double*) malloc(sizeof(double)*size);
    return array;
}


double* getArray(char* string, int size)
{

    int i;

    double* array = createArray(size);

    printf("\nPlease enter %s: ", string);
    for(i=0; i<size; i++)
    {
        scanf("%lf", array+i);
    }

    return array;
}

void printArray(double* array, int size)
{
    int i;
    printf("\n");
    for(i=0; i<size; i++)
    {
        printf("%lf ", *(array+i));
    }
    printf("\n");

}

void lu_decomposition(double **A, double **L, double **U, int m)
{
    int i, j, k;
    double sum;

    for (i = 0; i < m; i++)
    {
        for (j = 0; j < m; j++)
        {
            if (i == j)
                U[i][i] = 1.0;
            else if (j > i)
                U[i][j] = 0.0;
            else
                U[i][j] = 0.0;
        }

        for (j = i; j < m; j++)
        {
            sum = 0.0;
            for (k = 0; k < i; k++)
                sum += L[j][k] * U[k][i];
            L[j][i] = A[j][i] - sum;
        }

        for (j = i + 1; j < m; j++)
        {
            sum = 0.0;
            for (k = 0; k < i; k++)
                sum += L[i][k] * U[k][j];
            if (L[i][i] == 0.0)
            {
                fprintf(stderr, "LU decomposition failed: zero pivot\n");
                exit(EXIT_FAILURE);
            }
            U[i][j] = (A[i][j] - sum) / L[i][i];
        }
    }
}

void forward_substitution(double **L, double *Y, double *C, int n)
{
    int i, j;
    double sum;

    for (i = 0; i < n; i++)
    {
        sum = 0.0;
        for (j = 0; j < i; j++)
            sum += L[i][j] * Y[j];
        Y[i] = (C[i] - sum) / L[i][i];
    }
}

void back_substitution(double **U, double *Y, double *X, int n)
{
    int i, j;
    double sum;

    for (i = n - 1; i >= 0; i--)
    {
        sum = 0.0;
        for (j = i + 1; j < n; j++)
            sum += U[i][j] * X[j];
        X[i] = Y[i] - sum;
    }
}


void cholesky()
{
    double** A, **L, **U;
    double *Y,*X,*C;
    int m;

    m = getInt("m value of matrix (mxm)");

    A = createMatrix(m,m);
    L = createMatrix(m,m);
    U = createMatrix(m,m);

    Y = createArray(m);
    X = createArray(m);

    A = getMatrix(A,m,m);
    C = getArray("C values",m);

    lu_decomposition(A,L,U,m);
    forward_substitution(L,Y,C,m);
    back_substitution(U,Y,X,m);

    printf("\nLower Matrix: ");
    printMatrix(L,m,m);
    printf("\nUpper Matrix: ");
    printMatrix(U,m,m);

    printf("\nC vector: ");
    printArray(C,m);
    printf("\nY vector: ");
    printArray(Y,m);
    printf("\nX vector: ");
    printArray(X,m);
    printf("\n");

    freeMatrix(A,m);
    freeMatrix(L,m);
    freeMatrix(U,m);
    free(Y);
    free(X);
    free(C);
    return;
}

void swap_rows(double** matrix, int row1, int row2, int n)
{
    int j;
    double temp;
    for (j = 0; j < n; j++)
    {
        temp = matrix[row1][j];
        matrix[row1][j] = matrix[row2][j];
        matrix[row2][j] = temp;
    }
}

void gaussSeidal()
{
    double** matrix;
    double* X;
    double* prevX;
    double error;
    double result;
    int cont = true;
    int maxRow;
    int i,j,k=1;
    int m,n;

    m = getInt("row (m)");
    n = getInt("column (n)");

    matrix = createMatrix(m,n);
    matrix = getMatrix(matrix,m,n);
    prevX = createArray(m);
    X = getArray("the initial values",m);
    error = getDouble("the error");

    printf("\nInitial Matrix: \n");
    printMatrix(matrix,m,n);

    for(i = 0; i<m-1; i++)
    {
        maxRow = i;
        for(j=i+1; j<m; j++)
        {
            if(fabs(matrix[j][i]) > fabs(matrix[maxRow][i]))
            {
                maxRow = j;
            }
        }
        swap_rows(matrix,i,maxRow,n);
    }

    printf("\nDiagonally dominant Matrix: \n");
    printMatrix(matrix,m,n);

    do
    {
        for(i=0; i<m; i++)
        {
            result = 0;
            result += matrix[i][n-1];
            for(j=0; j<n-1; j++)
            {
                if(j != i)
                {
                    result -= X[j]*matrix[i][j];
                }
            }
            result /= matrix[i][i];

            prevX[i] = X[i];
            X[i] = result;
        }
        printf("\niteration number %d:\n",k);
        printArray(X,m);
        k++;

        cont = false;
        for (i = 0; i<m; i++)
        {
            if(fabs(prevX[i]-X[i]) > error)
            {
                cont = true;
            }
        }
    }
    while(cont);

    printf("\n");

    freeMatrix(matrix,m);
    free(X);
    free(prevX);
}

void simpson()
{

    TokenStack* function;
    double start, end, h;
    double tempStart, tempEnd;
    double result13, result38;
    int i,n;

    function = (TokenStack*) malloc(sizeof(TokenStack));
    function = getFunction(function);

    start = getDouble("starting point");
    end = getDouble("ending point");
    n = getInt("\"n\" (integral count)");
    h = (end-start)/n;

    tempStart = start;
    tempEnd = end;

    for(i = 0; i<n; i++)
    {

        tempEnd = tempStart+h;

        result13 += (tempEnd-tempStart)*(calculateFunction(function,tempStart)
                                         +4*calculateFunction(function,(tempStart+tempEnd)/2)
                                         +calculateFunction(function,tempEnd))/6;

        tempStart = tempEnd;
    }

    tempStart = start;
    tempEnd = end;

    for(i = 0; i<n; i++)
    {

        tempEnd = tempStart+h;

        result38 += (tempEnd-tempStart)*(calculateFunction(function,tempStart)
                                         +3*calculateFunction(function,(2*tempStart+tempEnd)/3)
                                         +3* calculateFunction(function, (tempStart+2*tempEnd)/3)
                                         +calculateFunction(function,tempEnd))/8;

        tempStart = tempEnd;
    }

    printf("\nResult of 1/3 Simpson: %lf\n", result13);
    printf("Result of 3/8 Simpson: %lf\n\n", result38);

    free(function);
    return;
}

void trapez()
{

    TokenStack* function;
    double start, end, result = 0, h;
    int i,n;

    function = (TokenStack*) malloc(sizeof(TokenStack));
    function = getFunction(function);

    start = getDouble("starting point");
    end = getDouble("ending point");
    n = getInt("\"n\"");

    h = (end-start)/n;

    result += calculateFunction(function,start);
    result += calculateFunction(function,end);

    for(i = 1; i<n; i++)
    {
        result += 2*calculateFunction(function, start + i*h);
    }

    result = result*h/2;

    printf("Result: %lf\n\n", result);

    free(function);
    return;
}

unsigned long long factorial(int n)
{
    int i;
    unsigned long long result = 1;
    for(i = 2; i <= n; i++)
        result *= i;
    return result;
}


unsigned long long combination(int n, int r)
{
    if (r > n) return 0;
    return factorial(n) / (factorial(r) * factorial(n - r));
}

double forwardDifference(double* fxData, int degree, int point)
{

    int i;
    double result=0;

    for(i=0; i<=degree; i++)
    {

        result += pow(-1,i) * combination(degree, i) * fxData[point+(degree-i)];

    }
    /*
    printf("\nForward distance of first point in degree of %d is: %f\n", degree, result);
    */
    return result;
}

void gregoryNewton()
{

    int dataLength,i;
    int degree = 0;
    double *xData, *fxData;
    double point, h;
    double midCalc=1;
    double result = 0;

    dataLength  = getInt("length of the dataset");

    xData = (double*) malloc(dataLength*sizeof(double));
    fxData = (double*) malloc(dataLength*sizeof(double));

    printf("Please enter the x values: ");
    for(i=0; i<dataLength; i++)
    {
        scanf("%lf", xData+i);
    }

    printf("Please enter the f(x) values: ");
    for(i=0; i<dataLength; i++)
    {
        scanf("%lf", fxData+i);
    }

    point = getDouble("x value you want to find f(x) of");

    h = xData[1] - xData[0];

    while(degree < dataLength)
    {

        midCalc *= forwardDifference(fxData, degree, 0);
        midCalc /= pow(h,degree);
        midCalc /= factorial(degree);

        for(i=0; i<degree; i++)
        {
            midCalc *= (point-xData[i]);
        }

        result += midCalc;
        /*
        printf("\nmidCalc: %f\n", midCalc);
        */
        midCalc = 1;
        degree++;
    }

    printf("\nResult is: %lf\n\n",result);


    free(xData);
    free(fxData);
    return;
}

int main()
{
    int notQuit = true;
    int choice;

    while(notQuit)
    {

        printf( "Quit: 0\n"
                "Bisection: 1\n"
                "Regula-Falsi: 2\n"
                "Newton-Raphson: 3\n"
                "NxN matrix inverse: 4\n"
                "Cholesky (ALU): 5\n"
                "Gauss Seidal: 6\n"
                "Numerical Differentiation: 7\n"
                "Simpson(1/3  and 3/8): 8\n"
                "Trapez: 9\n"
                "Gregory-Newton: 10\n");

        printf("\nEnter your choice: ");
        scanf("%d", &choice);

        switch (choice)
        {
        case 0:
            printf("Quitting...\n");
            notQuit = false;
            break;
        case 1:
            printf("Starting Bisection Method...\n");
            bisectionMethod();
            break;
        case 2:
            printf("Starting Regula-Falsi Method...\n");
            regulaFalsi();
            break;
        case 3:
            printf("Starting Newton Raphson Method...\n");
            newtonRaphson();
            break;
        case 4:
            printf("Starting Inverse Matrix...\n");
            inverseMatrix();
            break;
        case 5:
            printf("Starting Cholesky Method...\n");
            cholesky();
            break;
        case 6:
            printf("Starting Gauss-Seidal Method...\n");
            gaussSeidal();
            break;
        case 7:
            printf("Starting Numerical Differentiation Method...\n");
            numericalDiff();
            break;
        case 8:
            printf("Starting Simpson Method...\n");
            simpson();
            break;
        case 9:
            printf("Starting Trapez Method...\n");
            trapez();
            break;
        case 10:
            printf("Starting Gregory-Newton Method...\n");
            gregoryNewton();
            break;
        default:
            printf("Invalid choice!\n");
            break;
        }
    }

    return 0;
}
