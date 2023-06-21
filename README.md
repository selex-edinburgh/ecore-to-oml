# Ecore-to-OML Transformation
A tool to generate OML description and vocabulary files (*.oml) from Ecore models and metamodels (*.xmi, *.ecore).

## Build and Run
1. This `Build and Run` requires an OML project. To create an OML project, please follow the tutorials [here](http://www.opencaesar.io/oml-tutorials/#install-oml-rosetta), or we could use the OML project that comes along with this repo in the `targetoml` directory.
2. Download the source code.
    ```
    create repo clone selex-edinburgh/ecore-to-oml
    ```
3. Change directory into `ecore-to-oml/org.york.leonardo`.
   ```
    cd ecore-to-oml/org.york
    ```
4. Open `pom.xml` and scroll down to the part that looks like below.
   ```xml
    <systemPath>
        /home/alfa/projects/ecore-to-oml/dependencies/com.leonardo.lsaf.sadl-1.0.0-SNAPSHOT.jar
    </systemPath>
    ```
    Update the `systemPath` so that the path is the **absolute path** to the file `com.leonardo.lsaf.sadl-1.0.0-SNAPSHOT.jar` on your **local machine**. The jar file is in the `dependencies` directory in the repository's root. For example:
    ```xml
    <systemPath>
      /absolute/path/to/the/jar/file/on/your/local/machine/com.leonardo.lsaf.sadl-1.0.0-SNAPSHOT.jar
    </systemPath>
    ```
5. Build the project using the following command. The command will create the file `org.york.leonardo-0.0.1-SNAPSHOT-jar-with-dependencies.jar` under the `target` directory.
    ```
    mvn clean install
    ```
6. Change the directory to the target directory. 
    ```
    cd target
    ```
7. Test running the tool using the command below.
    ```
    java -jar target/org.york.leonardo-0.0.1-SNAPSHOT-jar-with-dependencies
    ```
    It will show the following output with an error message.
    ```
    Missing required options: '--model=<modelPath>', '--metamodel=<metamodelPath>', '--oml-project=<omlProjectPath>'
    Usage: ecore2oml [-hV] [-etl] -m=<modelPath> -mm=<metamodelPath>
                    -o=<omlProjectPath>
    A tool to generate OML descriptions and vocabularies from Ecore
    models/metamodels.
        -etl, --etl-mode      Use Epsilon Transformation Language (ETL) instead
                                of Epsilon Generation Language (EGL)
    -h, --help                Show this help message and exit.
    -m, --model=<modelPath>   path to the source model (*.xmi, *.sadl)
        -mm, --metamodel=<metamodelPath>
                                path to the source metamodel (*.ecore)
    -o, --oml-project=<omlProjectPath>
                                path to the target OML project, containing catalog.
                                xml)
    -V, --version             Print version information and exit.
    ```

    ```
8. Now we execute the jar file with some parameters; `-m` is the path to the source XMI/SADL model (*.xmi, *.sadl), `-mm` is the metamodel (*,ecore) of the source model, and `-o` is the target OML project. Make sure all paths are correctly **adjusted** to your local machine. The model and metamodel are based on the [Java MoDisco metamodel](https://help.eclipse.org/latest/index.jsp?topic=%2Forg.eclipse.modisco.java.doc%2Fmediawiki%2Fjava_metamodel%2Fuser.html&cp%3D59_0_1_2_0).
    ```
    java -jar org.york.leonardo-0.0.1-SNAPSHOT-jar-with-dependencies.jar -m /home/alfa/projects/ecore-to-oml/org.york.leonardo/model/java.xmi -mm /home/alfa/projects/ecore-to-oml/org.york.leonardo/model/java.ecore -o /home/alfa/projects/ecore-to-oml/targetoml
    ```
    The target OML project is the `targetoml` directory in the repo, but you could also use other OML projects created using Eclipse or Rosetta. To generate an OML project, please follow the tutorials [here](http://www.opencaesar.io/oml-tutorials/#install-oml-rosetta).
9. The command above will generate two files, the OML vocabulary and description of the Java metamodel and model, `www.eclipse.org/MoDisco/Java/0.2.incubation/java/vocabulary/java.oml` and `www.eclipse.org/MoDisco/Java/0.2.incubation/java/description/java.oml`. Both are located under `targetoml/src/oml` directory.
10. Open both files using any text editors and compare the results with the `pizza` and `spacecraft` examples [here](http://www.opencaesar.io/oml-tutorials/#install-oml-rosetta) and OML documentation [here](http://www.opencaesar.io/oml/).

## Documentation
1. [Ecore-to-OML Mapping](docs/ecore_to_oml_mapping.md)
2. [Comparison of the Old and New Ecore-to-OML Generators](docs/comparison_of%20_old_and_new_generators.md)

