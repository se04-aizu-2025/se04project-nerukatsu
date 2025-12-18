# this is src directory
java/: コードを置く場所
resources/: 素材等を置く場所

# Compile
- main/java/ に cd して、必要な .java をまとめて javac する
    例）javac algorithm\*.java ui\cui\CliApp.java
- CliApp.java 等を java する
    例）java ui.cui.CliApp

現在は CliApp.java 内でてきとうな配列を作成している

# file description
- SortAlgorithm.java: ソートアルゴリズムの基盤となる。このコードをもとに様々なソートを作っていく
- BubbleSort.java: BubbleSort
- DataGenerator.java: ランダムな配列を作成する
- CliApp.java: cuiでアルゴリズムを実行するためのファイル
- GuiApp.java: guiでアルゴリズムを実行するためのファイル