int main(){
    int i;
    int j;
    //  allow reading in a value for i;
    // allow reading in a value for j;
    writeln("Input i:");
    readln(i);
    writeln("You typed: ",i);

    writeln("Input j:");
    readln(j);
    writeln("You typed: ",j);

    i = j*5 + i/2 - 2;
    writeln("Peform i=j*5 + i/2 -2");
    writeln("Now i =",i);
    // allow displaying the value for I and j;
    return 0;
}
