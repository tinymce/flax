Flax
====

_Flax is a good source of Selenium_

Flax is a scala library that provides a purely functional interface to the 
popular testing tool Selenium.

Flax wraps Selenium's Java client in a set of *Action* values, which can be 
composed in order to write tests. The Actions performed are logged, and 
a tree of operations is displayed in the event of test failure, to aid debugging.

Flax integrates into the Specs2 testing framework, but
other integrations are possible.

Flax has a dependency on scalaz for its functional abstractions.

Getting Started
---------------

Assuming you're using sbt: 

1. Download any browser drivers you need and ensure they're on your path. e.g.
   - geckodriver - [https://github.com/mozilla/geckodriver/releases]
2. Add flax to your build.sbt file.
3. Disable parallel execution in your build settings
   `parallelExecution := false`
4. Create a test base class. e.g. [src/it/scala/com/ephox/flax/it/ExampleTestBase.scala]

5. Write tests
   `TODO`

Examples
--------

TODO


Implementation Details
----------------------

### Actions

Actions wrap a stack of types which provide:

 - input of a Selenium WebDriver wrapper object (Reader\[Driver, _\])
 - effect tracking (IO)
 - logging (Writer\[Log, _\])
 - error reporting (\/)
 
This stack is implemented as a special-case data type to avoid problems
with monad transformers in Scala. 

### Log

The Log data type is a tree structure with no root node. 
It's a list of nodes, and each node has a payload and a Log value representing its children.

flatMapping appends the logs. 
Action.nested adds a new level of structure - it makes a new Log, and takes 
the previous log as its children.  

