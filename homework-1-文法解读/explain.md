### 变量声明类型支持

`const`

`int`

`char`

```
const int const_a = 1, const_b=2;
const char const_ch = 'a';
const int const_arr[4]={1,2,3};
const char const_charArr[5]="abcd";

int var_a = 1,var_b;
char var_ch = 'b';
int var_arr[4]={1,2,3};
char var_chaRd[5]="abcd";
int nonInitialArr[3];
```

支持不初始化，支持连续定义多个变量

#### 返回语句return

所有函数都必须有`return [exp];`语句

不论函数返回类型为`int,char,void`

主函数`int main`需要返回0，`return 0;`

#### for/if语句

支持多个`if, else if, else`

`if 和 for`内部的条件判断语句可使用`And Or`

唯一需要注意的点为`i++,i--`语句的使用

该文法不支持`++,--`等语句

统一写为`i = i+1`

#### 函数的定义和声明

只支持`int, char ,void`三种类型函数

函数参数说明

**支持无参，一个参数，多个参数**

参数可为变量或者数组变量

```
void testVoid() {
    return;
}
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
```

### IO语句

输入语句以函数调用的形式出现，对应函数声明如下。虽然输入语句以函数调用形式出现，getint与 getchar 仍**被识别为关键字而不是标识符，在词法分析阶段和语法分析阶段需要注意。**

getint 会跳过空格，制表符 ( \t ，换行符 ( \n )，回车符 ( \r )，以及其他可能的空白字符。

getchar 不会跳过空格，制表符 ( \t )，换行符 ( \n )，回车符 ( \r )，以及其他可能的空白字符。

getint 只能用于为 int 类型变量赋值，getchar 只能用于为 char 类型变量赋值。

在设计编译器时，应该先将 getchar 返回值从 int 类型截断为 char 类型再赋值给 char 类型变量。

```
int x;
x = getint(); // 合法
x = getchar(); // 不合法
char c;
c = getint(); // 不合法
c = getchar(); // 合法
```

与 C 语言中的 printf 类似，输出语句中，格式字符将被替换为对应标识符，普通字符原样输出。其中格式字符只包含 %d 与 %c ，其他 C 语言中的格式字符，如 %f 都当做普通字符原样输出。printf 默认不换行。

### 函数参数问题

1. FuncFParam 定义一个函数的一个形式参数。当 Ident 后面的可选部分存在时， 表示数组定义。

2. 函数实参的语法是 Exp。对于普通变量，遵循按值传递；对于数组类型的参数，则形参接收的是实参数组的地址，并通过地址间接访问实参数组中的元素。

3. 普通常量可以作为函数参数，但那是常量数组不可以，如 const int arr[3] = {1,2,3} ，常量数组 arr **不能**作为参数传入到函数中

4. 函数调用时要保证实参类型和形参类型一致，具体请看下面例子。

```
void f1(int x){
return ;
}
void f2(int x[]){
return ;
}
void f3(char c){
return ;
}
void f4(char c[]){
return ;
}
int main(){
int x = 10;
int t[5] = {1, 2, 3, 4, 5};
char c = 'a';
char s[5] = "abcd";
f1(x); // 合法
f1(c); // 合法，具体原因见下方第五章中的解释
f1(t[0]); // 合法
f1(s[0]); // 合法，具体原因见下方第五章中的解释
f1(t); // 不合法
f2(t); // 合法
f2(s); // 不合法
f3(x); // 合法，具体原因见下方第五章中的解释
f3(c); // 合法
f3(t[0]); // 合法，具体原因见下方第五章中的解释
f3(s[0]); // 合法
f4(t); // 不合法
f4(s); // 合法
}
```

