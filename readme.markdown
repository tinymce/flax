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

[binaries](https://bintray.com/ephox/flax/flax)

[code](https://github.com/ephox/flax)


Getting Started (template)
--------------------------

1. Download any browser drivers you need and ensure they're on your path. e.g.
   - [geckodriver](https://github.com/mozilla/geckodriver/releases)

2. Use the giter8 template at [https://github.com/ephox/flax-template.g8]:

```
sbt new ephox/flax-template.g8
```


Getting Started (manually)
--------------------------


1. Download browser drivers, as above.

2. Add flax to your build.sbt file.

```scala
   resolvers += Resolver.bintrayRepo("ephox", "flax")

   libraryDependencies += "com.ephox" %% "flax" % FLAX_VERSION % Test withSources
```

3. Disable parallel execution in your build settings
   `parallelExecution := false`
4. Create a test base class. e.g. [ExampleTestBase.scala](src/it/scala/com/ephox/flax/it/ExampleTestBase.scala)

5. Write tests
   `TODO`


Implementation Details
----------------------

### Actions

Actions wrap a stack of types which provide:

 - effect tracking (IO)
 - input of a Selenium WebDriver wrapper object (Reader\[Driver, _\])
 - logging (Writer\[Log, _\])
 - error reporting (\/)
 
This stack is implemented as monad transformers in Scala.

### Log

The Log data type is a tree structure with no root node. 
It's a list of nodes, and each node has a payload and a Log value representing its children.

flatMapping appends the logs. 
Action.nested adds a new level of structure - it makes a new Log, and takes 
the previous log as its children.  


Developer notes
---------------

 - [sbt-bintray plugin](https://github.com/sbt/sbt-bintray)

Acknowledgements
----------------

Thanks to JFrog Bintray for hosting Flax's binaries.

