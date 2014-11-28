# Rendering Haml from boot

This example will render `resources/index.html.haml`, adding the
resulting `index.html` to the output file set.

# Usage

The `haml` task defined in `build.boot` does the work - `show-files`
task just shows the input haml and the resulting html:

    boot show-files --ext .haml -- haml -- show-files --ext .html
