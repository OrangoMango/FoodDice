#rm -r bin/*
cd src
echo "compiling source code..."
javac -d ../bin  --module-path $1 --add-modules javafx.controls,javafx.media com/orangomango/food/Launcher.java
cd ../bin
echo "copying resources..."
cp -r ../res/* .
echo "executing..."
java --module-path $1 --add-modules javafx.controls,javafx.media com.orangomango.food.Launcher
