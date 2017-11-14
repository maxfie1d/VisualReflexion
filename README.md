
## Visual Reflexion

A tool to visualize inter-module dataflow applying Reflexion Model.


## Build

### Requirements

* Gradle
* Understand.jar (Add this to the build dependencies. See the Understand manual for more details.)

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
  -c config.json \        # Specify analysis config file
```


### Analysis config file

Example:

```json
{
    "project_name": "life-simulator",

    "entry_point_func_name": "main",
    
    "target_variables": [
        "name",
        "age"
    ],

    // List of mappings of module to function
    "module_mappings": [
        {
            // Module name
            "name": "TIMER",

            // Function name
            "map_to": "tick"
        }
    ]
}

```

### Output

Output graph text to Stdin. You can preview the graph on [viz-js.com](http://viz-js.com/).

Example:
![Imgur](https://i.imgur.com/4BGrLOV.png)


## Test

```bash
$ gradle test
```
