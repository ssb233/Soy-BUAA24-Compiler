#include <stdio.h>

const int const_a = 1, const_b=2;
const char const_ch = 'a';
const int const_arr[4]={1,2,3};
const char const_charArr[5]="abcd";

int var_a = 1,var_b;
char var_ch = 'b';
int var_arr[4]={1,2,3};
char var_chaRd[5]="abcd";
int nonInitialArr[3];

void testVoid() {
    return;
}
//int getint(){
//    int a;
//    scanf("%d",&a);
//    return a;
//}
int addOne(int a){
    int c=2;
    if(c!=1)printf("test add 1\n");
    if(!c)c=c+2;
    if(c>=0)c=c+1;
    return a+c;
}
int addTwo(int a,int arr[]){
    int c=2;
    return a+arr[0];
}
int add(int a, int b) {
    int c = 1;
    if(c==1)printf("test add 2\n");
    return a + b + c;
}

char compareChar(char a, char b) {
    printf("test if exp\n");
    if (a > b)return a;
    else if (a < b)return b;
    return a;
}

void Exp(){
    {}
    int a=1;
    int b=1;
    ;
    a=2;
    for(a=b;a<2;a=a+1);
    for(;a<3;a=a+1);
    for(a=4;a<4;);
    for(a=4;;a=a+1){
        break;
    }
    for(a=4;;){break;}
    for(;a<=3;){ continue;}
    for(;;a=a+1){break;}
    for(;;){break;}
    a=1+2-3/3+2*2+2%2;

    //exp->addExp->MulExp->UnaryExp->PrimaryExp/Ident(FuncParams)/UnaryOp UnaryExp
    //primaryExp->(exp)/LVal/Number/Character
    1+1-1-2;
    1+2*2/2+3%2;
    +2;
    -2;
    a+2-a+a/2;
    a+(a+2);
    int arr[2]={1,2};
    arr[0]=2;
    return;
}
void input(){
    int a;
    a = getint();
    char c;
    c = getchar();
    return;
}
void testAndOr(){
    int a=1,b=2,c=3;
    int d;
    if(a<1 && b<2)d=1;
    if(a==1 || c>2)d=2;
    if(a<1 && b<2 || a==1 && c==3)d=3;
    return;
}
int main() {
    printf("22373526\n");
    testVoid();
    Exp();
    input();
    testAndOr();
    int a = 1, b = 2;
    char ch_a = 'a', ch_b = 'b';
    printf("add result is %d\n", add(a, var_arr[0]));
    printf("add one result is %d\n",addOne(a));
    printf("add two result is %d\n",addTwo(a,var_arr));
    printf("compare result is %c\n", compareChar(ch_a, ch_b));
    printf("%d\n",a);
    printf("%d",a);
    return 0;
}