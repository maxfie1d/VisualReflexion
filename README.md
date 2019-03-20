## Visual Reflexion

A tool to visualize inter-module dataflow applying Reflexion Model.


## Build

### Requirements

* Gradle
* Understand.jar (See [Understand.md](Understand.md) or Understand official manual for more details.)

Note: If you don't have Understand, remove sources depending on Understand from the project.


```bash
$ git clone <repo>
$ cd <repo>
# Build jar. The jar file will be created at `<repo>/build/libs`.
$ gradle fatJar
```


## Usage

```
$ java -jar <visualreflexion.jar> \
  -i input.(json|udb) \   # Specify input file created using srcML&srcSlice or Understand
  -c config.json          # Specify analysis config file
```

If you use srcML&srcSlice to create input file, see [the wiki](https://github.com/maxfie1d/VisualReflexion/wiki/srcML%E3%81%A8srcSlice-fork%E3%82%92VisualReflexion%E3%81%A7%E4%BD%BF%E3%81%86).

### Analysis config file

See [AnalysisConfig.md](AnaysisConfig.md).

### Output

Output graph text to Stdin. You can preview the graph on [viz-js.com](http://viz-js.com/).

Example:

![Imgur](https://i.imgur.com/4BGrLOV.png)


## Test

```bash
$ gradle test
```

## Wiki

Some useful information is found on [Wiki](https://github.com/maxfie1d/VisualReflexion/wiki).

## Publication

- [Visualization of Inter-Module Dataflow through Global Variables for Source Code Review (2018)](https://search.ieice.org/bin/summary.php?id=e101-d_12_3238&category=D&year=2018&lang=E&abst=)
