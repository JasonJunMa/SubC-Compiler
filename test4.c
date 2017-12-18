int main(){
    int i;
    int j;
    // read in a value for i;
    writeln("Input i:");
    readln(i);
    writeln("You typed: ",i);
    // read in a value for j;
    writeln("Input j:");
    readln(j);
    writeln("You typed: ",j);
    if (i > j) {
        // allowing display “i is greater than j”
        writeln("i is greater than j");
    }
    else{
        // allowing display “i is less than or equal to j”
        writeln("i is less than or equal to j");
    }
    return 0;
}
