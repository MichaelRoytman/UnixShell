# UnixShell
## A Unix shell written in Java

CommandManager.java manages the lifetime of commands by parsing through raw input, validating commands, creating the commands, and executing the commands. 

Cat.java, Cd.java, Grep.java, History.java, Lc.java, Ls.java, and Pwd.java are all classes that extend the Filter class and contain code that implement the Unix command they are named after.

CommandHistory.java stores the history of commands entered in the shell (used by History.java command).

CurrentDir.java stores the current working directory of the shell (used by Cd.java).

EndOfFileMarker.java is an empty class that symbolizes the end of input.

Filter.java is an abstract wrapper class that represents a Unix shell command.

MyShell.java is the wrapper class that contains the REPL loop.

Redirect.java implements that redirect command.

ShellSink.java handles printing final output of a shell command to the console.