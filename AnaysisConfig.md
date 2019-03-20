# Analysis config

## Example

```json
{
    "project_name": "life-simulator",
    "entry_point_func_name": "main",
    "target_variables": [
        "name",
        "age"
    ],
    "module_mappings": [
        {
            "name": "TIMER",
            "map_to": "tick"
        }
    ]
}
```

## Spec

|  Key | Type | Description | Required |
| --- | --- | --- | --- |
|  project_name | String | Project name (ex. robot-walk-app) | Yes |
|  entry_point_func_name | String | Specify the entry point function name. (ex. master) | Yes |
|  target_variables | Array of String | Global variables of interest | Yes |
|  module_mappings | Array of `ModuleMapping` | List of mapping of module and function | Yes |
|  controls | Array of `Control` | Modify conditional expression value | No |

### ModuleMapping

|  Key | Type | Description | Required |
| --- | --- | --- | --- |
|  name | String | Module name | Yes |
|  map_to | String | Function name | Yes |

`ModuleMapping` represents how each module is corresponding to function in actual source code.

### Control

|  Key | Type | Description | Required |
| --- | --- | --- | --- |
|  file | String | File name | Yes |
|  line_number | Int | Line number | Yes |
|  value | Boolean | Set conditional expression value | Yes |
