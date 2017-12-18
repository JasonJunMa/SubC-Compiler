target:
	make clean
	mkdir classes
	javac -classpath classes -d classes -sourcepath src src/*java && cd classes && jar -cvf subcRTL.jar BWrap.class CWrap.class IWrap.class RWrap.class Cloner.class PaddedString.class PascalRuntimeException.class PascalTextIn.class RangeChecker.class RunTimer.class wci/frontend/EofToken.class wci/frontend/Scanner.class wci/frontend/Source.class wci/frontend/Token.class wci/frontend/TokenType.class wci/frontend/subc/SubCScanner.class wci/frontend/subc/SubCToken.class wci/frontend/subc/SubCTokenType.class wci/frontend/subc/tokens/*.class wci/message/*.class && cp subcRTL.jar ..

cp:
	java -classpath classes SubC compile -ixlafcr $(file)

run:
	java -cp .:subcRTL.jar $(file)

clean:
	rm -rf *.class
	rm -rf *.j
	rm -rf classesj
	rm -rf subcRTL.jar
