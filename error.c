float fib1(int n,float c)
{
    int i,t1,t2,next;
    i = c;
    t1= 0;
    t2= 1;
    next = 0;
    write("Fibonacci Series: ");
    do{
        write(t1);
        write(", ");
        next = t1 + t2;
        t1 = t2;
        t2 = next;
        i = i+1;
    }while(i>n);
    return c;
}

void fib2(int n)
{
    int t1,t2,next;
    t1= 0;
    t2= 1;
    next = 0;
    write("Fibonacci Series: ", t1,", ", t2,", ");
    next = t1 + t2;
    while(next <= n)
    {
        write(next);
        write(", ");
        t1 = t2;
        t2 = next;
        next = t1 + t2;
    }
}
int main(){
    writeln("This program will print Fibonacci sequence!Have fun!");
    writeln("1:Get Fibonacci Series up to n number of terms");
    writeln("2:Get  Fibonacci Sequence Up to a Certain Number");
    writeln("Please choose 1 or 2: ");
    int j,k;
    float m;
    m = 0.1;
    readln(j);
    //int k;
    if(j==1){
        writeln("Please enter the number of terms:");
        readln(k);
        j=fib1(k,m);
        fib1(k);
    }
    else{
        writeln("Enter a positive number:");
        readln(k);
        fib2(k);
        m = fib2(k);
    }
    return 0;
}
