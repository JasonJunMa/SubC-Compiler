## Use jasmin to create .class file
After run the compiler, we will get a .j file.
jasmin hello.j
we will get hello.class

## Create a runtime library
go to class directory, run the command
jar -cvf subcRTL.jar BWrap.class CWrap.class IWrap.class RWrap.class Cloner.class PaddedString.class PascalRuntimeException.class PascalTextIn.class RangeChecker.class RunTimer.class wci/frontend/EofToken.class wci/frontend/Scanner.class wci/frontend/Source.class wci/frontend/Token.class wci/frontend/TokenType.class wci/frontend/subc/SubCScanner.class wci/frontend/subc/SubCToken.class wci/frontend/subc/SubCTokenType.class wci/frontend/subc/tokens/*.class wci/message/*.class
